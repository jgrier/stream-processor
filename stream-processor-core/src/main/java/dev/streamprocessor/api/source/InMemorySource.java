package dev.streamprocessor.api.source;

import java.util.Iterator;

public class InMemorySource<T> implements Source<T> {
    private final Iterable<T> data;
    private transient Iterator<T> iterator;

    public InMemorySource(Iterable<T> data) {
        this.data = data;
    }

    @Override
    public void open() {
        this.iterator = data.iterator();
    }

    @Override
    public T next() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    @Override
    public void close() {
        this.iterator = null;
    }
}
