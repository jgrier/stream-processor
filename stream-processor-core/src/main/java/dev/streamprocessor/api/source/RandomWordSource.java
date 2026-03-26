package dev.streamprocessor.api.source;

import java.util.Random;

/**
 * An unbounded source that generates random words from a fixed vocabulary.
 * Designed for the token circulation backpressure model — next() is safe
 * to call across separate Restate invocations without open/close.
 */
public class RandomWordSource implements Source<String> {
    private static final String[] VOCABULARY = {
            "hello", "world", "restate", "stream", "processing",
            "durable", "state", "flink", "event", "reduce",
            "filter", "keyed", "operator", "pipeline", "token"
    };

    private final Random random = new Random();

    @Override
    public void open() {}

    @Override
    public String next() {
        return VOCABULARY[random.nextInt(VOCABULARY.length)];
    }

    @Override
    public void close() {}

    @Override
    public boolean isBounded() { return false; }
}
