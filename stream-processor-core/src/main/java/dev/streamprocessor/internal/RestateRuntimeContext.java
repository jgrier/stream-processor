package dev.streamprocessor.internal;

import dev.restate.sdk.ObjectContext;
import dev.streamprocessor.api.state.*;

public class RestateRuntimeContext implements RuntimeContext {
    private final ObjectContext ctx;

    public RestateRuntimeContext(ObjectContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public <T> ValueState<T> getState(ValueStateDescriptor<T> descriptor) {
        return new RestateValueState<>(ctx, descriptor);
    }

    @Override
    public <T> ReducingState<T> getReducingState(ReducingStateDescriptor<T> descriptor) {
        return new RestateReducingState<>(ctx, descriptor);
    }
}
