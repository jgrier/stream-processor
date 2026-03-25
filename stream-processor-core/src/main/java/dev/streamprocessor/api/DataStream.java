package dev.streamprocessor.api;

import dev.streamprocessor.api.functions.*;
import dev.streamprocessor.api.sink.Sink;
import dev.streamprocessor.internal.OperatorNode;
import dev.streamprocessor.internal.OperatorType;

public class DataStream<T> {
    protected final StreamExecutionEnvironment env;
    protected final OperatorNode node;

    public DataStream(StreamExecutionEnvironment env, OperatorNode node) {
        this.env = env;
        this.node = node;
    }

    public <OUT> DataStream<OUT> map(MapFunction<T, OUT> mapper) {
        OperatorNode child = new OperatorNode(OperatorType.MAP, mapper);
        node.setChild(child);
        return new DataStream<>(env, child);
    }

    public DataStream<T> filter(FilterFunction<T> filter) {
        OperatorNode child = new OperatorNode(OperatorType.FILTER, filter);
        node.setChild(child);
        return new DataStream<>(env, child);
    }

    public <OUT> DataStream<OUT> flatMap(FlatMapFunction<T, OUT> flatMapper) {
        OperatorNode child = new OperatorNode(OperatorType.FLAT_MAP, flatMapper);
        node.setChild(child);
        return new DataStream<>(env, child);
    }

    public <KEY> KeyedStream<T, KEY> keyBy(KeySelector<T, KEY> keySelector) {
        OperatorNode child = new OperatorNode(OperatorType.KEY_BY, keySelector);
        node.setChild(child);
        return new KeyedStream<>(env, child, keySelector);
    }

    public void addSink(Sink<T> sink) {
        OperatorNode child = new OperatorNode(OperatorType.SINK, sink);
        node.setChild(child);
    }

    public OperatorNode getNode() { return node; }
}
