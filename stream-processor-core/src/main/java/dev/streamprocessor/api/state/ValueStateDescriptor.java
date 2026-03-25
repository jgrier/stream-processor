package dev.streamprocessor.api.state;

import java.io.Serializable;

public class ValueStateDescriptor<T> implements Serializable {
    private final String name;
    private final Class<T> typeClass;

    public ValueStateDescriptor(String name, Class<T> typeClass) {
        this.name = name;
        this.typeClass = typeClass;
    }

    public String getName() { return name; }
    public Class<T> getTypeClass() { return typeClass; }
}
