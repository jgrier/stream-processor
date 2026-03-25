package dev.streamprocessor.internal;

import dev.restate.sdk.Context;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import dev.streamprocessor.api.sink.Sink;

/**
 * Restate service that receives processed records and writes them to the configured sink.
 */
@Service
public class SinkOperatorService {
    @SuppressWarnings("unchecked")
    static Sink<Object> sink;

    @Handler
    public void process(Context ctx, String jsonRecord) throws Exception {
        Object record = RecordEnvelope.unwrap(jsonRecord);
        sink.write(record);
    }
}
