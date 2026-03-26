package dev.streamprocessor.api.source;

import java.io.Serializable;

public interface Source<T> extends Serializable {
    void open() throws Exception;
    T next() throws Exception;
    void close() throws Exception;

    default boolean isBounded() { return true; }
}
