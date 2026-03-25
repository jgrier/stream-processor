package dev.streamprocessor.api.functions;

import java.io.Serializable;

@FunctionalInterface
public interface FilterFunction<T> extends Serializable {
    boolean filter(T value) throws Exception;
}
