package dev.streamprocessor.api.functions;

import dev.streamprocessor.api.Collector;
import java.io.Serializable;

@FunctionalInterface
public interface FlatMapFunction<IN, OUT> extends Serializable {
    void flatMap(IN value, Collector<OUT> out) throws Exception;
}
