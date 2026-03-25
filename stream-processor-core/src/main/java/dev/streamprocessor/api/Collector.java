package dev.streamprocessor.api;

public interface Collector<T> {
    void collect(T record);
}
