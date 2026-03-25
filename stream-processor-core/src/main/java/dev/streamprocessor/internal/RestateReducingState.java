package dev.streamprocessor.internal;

import dev.restate.sdk.ObjectContext;
import dev.restate.sdk.common.StateKey;
import dev.streamprocessor.api.functions.ReduceFunction;
import dev.streamprocessor.api.state.ReducingState;
import dev.streamprocessor.api.state.ReducingStateDescriptor;

public class RestateReducingState<T> implements ReducingState<T> {
    private final ObjectContext ctx;
    private final StateKey<T> stateKey;
    private final ReduceFunction<T> reduceFunction;

    public RestateReducingState(ObjectContext ctx, ReducingStateDescriptor<T> descriptor) {
        this.ctx = ctx;
        this.stateKey = StateKey.of(descriptor.getName(), descriptor.getTypeClass());
        this.reduceFunction = descriptor.getReduceFunction();
    }

    @Override
    public T get() {
        return ctx.get(stateKey).orElse(null);
    }

    @Override
    public void add(T value) throws Exception {
        var current = ctx.get(stateKey);
        if (current.isPresent()) {
            ctx.set(stateKey, reduceFunction.reduce(current.get(), value));
        } else {
            ctx.set(stateKey, value);
        }
    }

    @Override
    public void clear() {
        ctx.clear(stateKey);
    }
}
