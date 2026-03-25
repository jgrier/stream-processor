package dev.streamprocessor.api.functions;

import dev.streamprocessor.api.Collector;
import dev.streamprocessor.api.state.RuntimeContext;
import java.io.Serializable;

public abstract class ProcessFunction<K, IN, OUT> implements Serializable {

    public abstract void processElement(IN value, Context ctx, Collector<OUT> out) throws Exception;

    public void open(RuntimeContext runtimeContext) throws Exception {}

    public void close() throws Exception {}

    public abstract static class Context {
        public abstract String getCurrentKey();
        public abstract RuntimeContext getRuntimeContext();
    }
}
