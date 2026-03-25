package dev.streamprocessor.internal;

public class OperatorNode {
    private final OperatorType type;
    private final Object function;
    private OperatorNode child;

    public OperatorNode(OperatorType type, Object function) {
        this.type = type;
        this.function = function;
    }

    public OperatorType getType() { return type; }
    public Object getFunction() { return function; }
    public OperatorNode getChild() { return child; }

    public void setChild(OperatorNode child) {
        this.child = child;
    }
}
