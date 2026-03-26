package dev.streamprocessor.internal;

import dev.restate.sdk.Context;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import dev.streamprocessor.api.functions.KeySelector;
import dev.streamprocessor.api.source.Source;

import java.util.List;

/**
 * Restate service that ingests source records, applies pre-keyBy stateless operators,
 * and routes results to the keyed stage or directly to the sink stage.
 *
 * For unbounded sources, uses token circulation for backpressure:
 * each ingest() invocation processes one record and the token is returned
 * by the sink calling ingest() again after writing.
 */
@Service
public class SourceOperatorService {
    static Source<Object> source;
    static List<OperatorNode> preKeyByOps;
    static KeySelector<Object, Object> keySelector;
    static boolean hasKeyedStage;

    /**
     * For bounded sources: reads all records and sends them to process().
     * For unbounded sources: reads ONE record (token model). The token
     * is returned by SinkOperatorService calling ingest() after writing.
     */
    @Handler
    public void ingest(Context ctx) throws Exception {
        if (source.isBounded()) {
            source.open();
            try {
                Object record;
                while ((record = source.next()) != null) {
                    ctx.send(SourceOperatorServiceHandlers.process(RecordEnvelope.wrap(record)));
                }
            } finally {
                source.close();
            }
        } else {
            Object record = source.next();
            if (record != null) {
                ctx.send(SourceOperatorServiceHandlers.process(RecordEnvelope.wrap(record)));
            }
        }
    }

    /**
     * Injects additional tokens into the system at runtime.
     * Each token becomes an ingest() invocation.
     */
    @Handler
    public void addTokens(Context ctx, int count) {
        for (int i = 0; i < count; i++) {
            ctx.send(SourceOperatorServiceHandlers.ingest());
        }
    }

    /**
     * Processes a single record through the pre-keyBy operator chain
     * and routes the results to the next stage.
     */
    @Handler
    public void process(Context ctx, String jsonRecord) throws Exception {
        Object record = RecordEnvelope.unwrap(jsonRecord);

        List<Object> results = OperatorChainExecutor.apply(record, preKeyByOps);

        for (Object result : results) {
            String outputJson = RecordEnvelope.wrap(result);
            if (hasKeyedStage) {
                Object key = keySelector.getKey(result);
                String keyStr = key.toString();
                ctx.send(KeyedOperatorServiceHandlers.process(keyStr, outputJson));
            } else {
                ctx.send(SinkOperatorServiceHandlers.process(outputJson));
            }
        }
    }
}
