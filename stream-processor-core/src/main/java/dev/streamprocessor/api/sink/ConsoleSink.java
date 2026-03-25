package dev.streamprocessor.api.sink;

public class ConsoleSink<T> implements Sink<T> {
    @Override
    public void open() {}

    @Override
    public void write(T value) {
        System.out.println(value);
    }

    @Override
    public void close() {}
}
