package dev.streamprocessor.api.state;

public interface ValueState<T> {
    T value();
    void update(T value);
    void clear();
}
