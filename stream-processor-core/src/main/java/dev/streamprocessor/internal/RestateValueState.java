package dev.streamprocessor.internal;

import dev.restate.sdk.ObjectContext;
import dev.restate.sdk.common.StateKey;
import dev.streamprocessor.api.state.ValueState;
import dev.streamprocessor.api.state.ValueStateDescriptor;

public class RestateValueState<T> implements ValueState<T> {
    private final ObjectContext ctx;
    private final StateKey<T> stateKey;

    public RestateValueState(ObjectContext ctx, ValueStateDescriptor<T> descriptor) {
        this.ctx = ctx;
        this.stateKey = StateKey.of(descriptor.getName(), descriptor.getTypeClass());
    }

    @Override
    public T value() {
        return ctx.get(stateKey).orElse(null);
    }

    @Override
    public void update(T value) {
        ctx.set(stateKey, value);
    }

    @Override
    public void clear() {
        ctx.clear(stateKey);
    }
}
