package dev.streamprocessor.internal;

import dev.restate.sdk.Context;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import dev.streamprocessor.api.sink.Sink;

/**
 * Restate service that receives processed records and writes them to the configured sink.
 * For unbounded sources, returns the token by calling ingest() after writing.
 */
@Service
public class SinkOperatorService {
    @SuppressWarnings("unchecked")
    static Sink<Object> sink;
    static boolean unboundedSource;

    @Handler
    public void process(Context ctx, String jsonRecord) throws Exception {
        Object record = RecordEnvelope.unwrap(jsonRecord);
        sink.write(record);
        if (unboundedSource) {
            ctx.send(SourceOperatorServiceHandlers.ingest());
        }
    }
}
