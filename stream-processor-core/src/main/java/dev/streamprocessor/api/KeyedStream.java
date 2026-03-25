package dev.streamprocessor.api;

import dev.streamprocessor.api.functions.KeySelector;
import dev.streamprocessor.api.functions.ProcessFunction;
import dev.streamprocessor.api.functions.ReduceFunction;
import dev.streamprocessor.internal.OperatorNode;
import dev.streamprocessor.internal.OperatorType;

public class KeyedStream<T, KEY> extends DataStream<T> {
    private final KeySelector<T, KEY> keySelector;

    public KeyedStream(StreamExecutionEnvironment env, OperatorNode node, KeySelector<T, KEY> keySelector) {
        super(env, node);
        this.keySelector = keySelector;
    }

    public DataStream<T> reduce(ReduceFunction<T> reducer) {
        OperatorNode child = new OperatorNode(OperatorType.REDUCE, reducer);
        node.setChild(child);
        return new DataStream<>(env, child);
    }

    public <OUT> DataStream<OUT> process(ProcessFunction<KEY, T, OUT> processFunction) {
        OperatorNode child = new OperatorNode(OperatorType.PROCESS, processFunction);
        node.setChild(child);
        return new DataStream<>(env, child);
    }

    public KeySelector<T, KEY> getKeySelector() { return keySelector; }
}
