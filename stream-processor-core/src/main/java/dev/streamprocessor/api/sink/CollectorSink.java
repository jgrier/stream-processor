package dev.streamprocessor.api.sink;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollectorSink<T> implements Sink<T> {
    private final List<T> results = new CopyOnWriteArrayList<>();

    @Override
    public void open() {}

    @Override
    public void write(T value) {
        results.add(value);
    }

    @Override
    public void close() {}

    public List<T> getResults() {
        return results;
    }
}
