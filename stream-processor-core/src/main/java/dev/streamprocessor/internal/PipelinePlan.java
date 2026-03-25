package dev.streamprocessor.internal;

import dev.streamprocessor.api.functions.KeySelector;
import dev.streamprocessor.api.sink.Sink;
import dev.streamprocessor.api.source.Source;

import java.util.List;

/**
 * A compiled representation of a stream processing pipeline, split into stages
 * that map to Restate services.
 */
public class PipelinePlan {
    private final Source<?> source;
    private final List<OperatorNode> preKeyByOps;
    private final KeySelector<?, ?> keySelector;
    private final OperatorNode keyedOp;
    private final List<OperatorNode> postKeyedOps;
    private final Sink<?> sink;

    public PipelinePlan(Source<?> source, List<OperatorNode> preKeyByOps,
                        KeySelector<?, ?> keySelector, OperatorNode keyedOp,
                        List<OperatorNode> postKeyedOps, Sink<?> sink) {
        this.source = source;
        this.preKeyByOps = preKeyByOps;
        this.keySelector = keySelector;
        this.keyedOp = keyedOp;
        this.postKeyedOps = postKeyedOps;
        this.sink = sink;
    }

    public Source<?> getSource() { return source; }
    public List<OperatorNode> getPreKeyByOps() { return preKeyByOps; }
    public KeySelector<?, ?> getKeySelector() { return keySelector; }
    public OperatorNode getKeyedOp() { return keyedOp; }
    public List<OperatorNode> getPostKeyedOps() { return postKeyedOps; }
    public Sink<?> getSink() { return sink; }
    public boolean hasKeyBy() { return keySelector != null; }
}
