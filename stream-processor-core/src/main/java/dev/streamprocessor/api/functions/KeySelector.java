package dev.streamprocessor.api.functions;

import java.io.Serializable;

@FunctionalInterface
public interface KeySelector<IN, KEY> extends Serializable {
    KEY getKey(IN value) throws Exception;
}
