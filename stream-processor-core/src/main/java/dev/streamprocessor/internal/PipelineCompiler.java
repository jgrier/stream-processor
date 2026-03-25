package dev.streamprocessor.internal;

import dev.streamprocessor.api.functions.KeySelector;
import dev.streamprocessor.api.sink.Sink;
import dev.streamprocessor.api.source.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * Walks the operator DAG and compiles it into a PipelinePlan with stages
 * that map to Restate services.
 */
public class PipelineCompiler {

    @SuppressWarnings("unchecked")
    public static PipelinePlan compile(OperatorNode sourceNode) {
        if (sourceNode.getType() != OperatorType.SOURCE) {
            throw new IllegalArgumentException("Pipeline must start with a source");
        }

        Source<?> source = (Source<?>) sourceNode.getFunction();
        List<OperatorNode> preKeyByOps = new ArrayList<>();
        KeySelector<?, ?> keySelector = null;
        OperatorNode keyedOp = null;
        List<OperatorNode> postKeyedOps = new ArrayList<>();
        Sink<?> sink = null;

        OperatorNode current = sourceNode.getChild();
        boolean pastKeyBy = false;
        boolean pastKeyedOp = false;

        while (current != null) {
            switch (current.getType()) {
                case MAP, FILTER, FLAT_MAP -> {
                    if (!pastKeyBy) {
                        preKeyByOps.add(current);
                    } else {
                        postKeyedOps.add(current);
                        pastKeyedOp = true;
                    }
                }
                case KEY_BY -> {
                    keySelector = (KeySelector<?, ?>) current.getFunction();
                    pastKeyBy = true;
                }
                case REDUCE, PROCESS -> {
                    keyedOp = current;
                    pastKeyedOp = true;
                }
                case SINK -> {
                    sink = (Sink<?>) current.getFunction();
                }
                default -> throw new IllegalStateException("Unexpected operator: " + current.getType());
            }
            current = current.getChild();
        }

        if (sink == null) {
            throw new IllegalStateException("Pipeline must end with a sink");
        }

        return new PipelinePlan(source, preKeyByOps, keySelector, keyedOp, postKeyedOps, sink);
    }
}
