package dev.streamprocessor.internal;

import dev.streamprocessor.api.functions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies a chain of stateless operators to a record, producing zero or more output records.
 */
@SuppressWarnings("unchecked")
public class OperatorChainExecutor {

    public static List<Object> apply(Object input, List<OperatorNode> operators) throws Exception {
        List<Object> current = new ArrayList<>();
        current.add(input);

        for (OperatorNode op : operators) {
            List<Object> next = new ArrayList<>();
            for (Object record : current) {
                switch (op.getType()) {
                    case MAP -> {
                        MapFunction<Object, Object> fn = (MapFunction<Object, Object>) op.getFunction();
                        next.add(fn.map(record));
                    }
                    case FILTER -> {
                        FilterFunction<Object> fn = (FilterFunction<Object>) op.getFunction();
                        if (fn.filter(record)) {
                            next.add(record);
                        }
                    }
                    case FLAT_MAP -> {
                        FlatMapFunction<Object, Object> fn = (FlatMapFunction<Object, Object>) op.getFunction();
                        ListCollector<Object> collector = new ListCollector<>();
                        fn.flatMap(record, collector);
                        next.addAll(collector.getRecords());
                    }
                    default -> throw new IllegalStateException("Unsupported operator in stateless chain: " + op.getType());
                }
            }
            current = next;
        }

        return current;
    }
}
