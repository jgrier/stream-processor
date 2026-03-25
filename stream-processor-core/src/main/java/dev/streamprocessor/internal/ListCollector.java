package dev.streamprocessor.internal;

import dev.streamprocessor.api.Collector;
import java.util.ArrayList;
import java.util.List;

public class ListCollector<T> implements Collector<T> {
    private final List<T> records = new ArrayList<>();

    @Override
    public void collect(T record) {
        records.add(record);
    }

    public List<T> getRecords() { return records; }
}
