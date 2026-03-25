package dev.streamprocessor.internal;

import dev.restate.sdk.ObjectContext;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.VirtualObject;
import dev.restate.sdk.common.StateKey;
import dev.streamprocessor.api.Collector;
import dev.streamprocessor.api.functions.ProcessFunction;
import dev.streamprocessor.api.functions.ReduceFunction;
import dev.streamprocessor.api.state.RuntimeContext;

import java.util.List;

/**
 * Restate virtual object that handles per-key stateful processing.
 * Each unique key gets its own isolated state, matching Flink's keyed stream semantics.
 */
@VirtualObject
public class KeyedOperatorService {
    static OperatorNode keyedOp;
    static List<OperatorNode> postKeyedOps;

    private static final StateKey<String> REDUCE_STATE = StateKey.of("reduce_state", String.class);

    @SuppressWarnings("unchecked")
    @Handler
    public void process(ObjectContext ctx, String jsonRecord) throws Exception {
        Object record = RecordEnvelope.unwrap(jsonRecord);
        Object result;

        if (keyedOp != null && keyedOp.getType() == OperatorType.REDUCE) {
            ReduceFunction<Object> reducer = (ReduceFunction<Object>) keyedOp.getFunction();
            var existingJson = ctx.get(REDUCE_STATE);
            if (existingJson.isPresent()) {
                Object existing = RecordEnvelope.unwrap(existingJson.get());
                result = reducer.reduce(existing, record);
            } else {
                result = record;
            }
            ctx.set(REDUCE_STATE, RecordEnvelope.wrap(result));
        } else if (keyedOp != null && keyedOp.getType() == OperatorType.PROCESS) {
            ProcessFunction<Object, Object, Object> fn =
                    (ProcessFunction<Object, Object, Object>) keyedOp.getFunction();
            RuntimeContext runtimeContext = new RestateRuntimeContext(ctx);
            fn.open(runtimeContext);
            ListCollector<Object> collector = new ListCollector<>();
            ProcessFunction.Context procCtx = new ProcessFunctionContext(ctx, runtimeContext);
            fn.processElement(record, procCtx, collector);

            List<Object> results = collector.getRecords();
            if (postKeyedOps != null && !postKeyedOps.isEmpty()) {
                List<Object> postResults = new java.util.ArrayList<>();
                for (Object r : results) {
                    postResults.addAll(OperatorChainExecutor.apply(r, postKeyedOps));
                }
                results = postResults;
            }
            for (Object r : results) {
                ctx.send(SinkOperatorServiceHandlers.process(RecordEnvelope.wrap(r)));
            }
            return;
        } else {
            result = record;
        }

        // Apply post-keyed operators
        List<Object> results = List.of(result);
        if (postKeyedOps != null && !postKeyedOps.isEmpty()) {
            List<Object> postResults = new java.util.ArrayList<>();
            for (Object r : results) {
                postResults.addAll(OperatorChainExecutor.apply(r, postKeyedOps));
            }
            results = postResults;
        }

        for (Object r : results) {
            ctx.send(SinkOperatorServiceHandlers.process(RecordEnvelope.wrap(r)));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static class ProcessFunctionContext extends ProcessFunction.Context {
        private final ObjectContext ctx;
        private final RuntimeContext runtimeContext;

        ProcessFunctionContext(ObjectContext ctx, RuntimeContext runtimeContext) {
            // We need an instance of ProcessFunction to create the Context
            // This is a workaround since Context is a non-static inner class in the plan
            this.ctx = ctx;
            this.runtimeContext = runtimeContext;
        }

        @Override
        public String getCurrentKey() {
            return ctx.key();
        }

        @Override
        public RuntimeContext getRuntimeContext() {
            return runtimeContext;
        }
    }
}
