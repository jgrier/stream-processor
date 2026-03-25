package dev.streamprocessor.api.state;

public interface ReducingState<T> {
    T get();
    void add(T value) throws Exception;
    void clear();
}
