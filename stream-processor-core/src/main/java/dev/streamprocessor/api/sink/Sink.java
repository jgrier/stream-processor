package dev.streamprocessor.api.sink;

import java.io.Serializable;

public interface Sink<T> extends Serializable {
    void open() throws Exception;
    void write(T value) throws Exception;
    void close() throws Exception;
}
