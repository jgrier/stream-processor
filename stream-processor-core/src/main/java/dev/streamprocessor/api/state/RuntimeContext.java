package dev.streamprocessor.api.state;

public interface RuntimeContext {
    <T> ValueState<T> getState(ValueStateDescriptor<T> descriptor);
    <T> ReducingState<T> getReducingState(ReducingStateDescriptor<T> descriptor);
}
