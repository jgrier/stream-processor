package dev.streamprocessor.api.functions;

import java.io.Serializable;

@FunctionalInterface
public interface ReduceFunction<T> extends Serializable {
    T reduce(T value1, T value2) throws Exception;
}
