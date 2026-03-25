package dev.streamprocessor.api.state;

import dev.streamprocessor.api.functions.ReduceFunction;
import java.io.Serializable;

public class ReducingStateDescriptor<T> implements Serializable {
    private final String name;
    private final Class<T> typeClass;
    private final ReduceFunction<T> reduceFunction;

    public ReducingStateDescriptor(String name, ReduceFunction<T> reduceFunction, Class<T> typeClass) {
        this.name = name;
        this.typeClass = typeClass;
        this.reduceFunction = reduceFunction;
    }

    public String getName() { return name; }
    public Class<T> getTypeClass() { return typeClass; }
    public ReduceFunction<T> getReduceFunction() { return reduceFunction; }
}
