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
 */
@Service
public class SourceOperatorService {
    static Source<Object> source;
    static List<OperatorNode> preKeyByOps;
    static KeySelector<Object, Object> keySelector;
    static boolean hasKeyedStage;

    /**
     * Called once to read all records from the source and push them into the pipeline.
     * Each record is sent to the process handler via durable one-way send.
     */
    @Handler
    public void ingest(Context ctx) throws Exception {
        source.open();
        try {
            Object record;
            while ((record = source.next()) != null) {
                String json = RecordEnvelope.wrap(record);
                ctx.send(SourceOperatorServiceHandlers.process(json));
            }
        } finally {
            source.close();
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
