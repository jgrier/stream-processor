package dev.streamprocessor.api.functions;

import java.io.Serializable;

@FunctionalInterface
public interface MapFunction<IN, OUT> extends Serializable {
    OUT map(IN value) throws Exception;
}
