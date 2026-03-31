package dev.streamprocessor.example;

import dev.streamprocessor.api.StreamExecutionEnvironment;
import dev.streamprocessor.api.sink.FileSink;
import dev.streamprocessor.api.source.RandomWordSource;

import java.util.concurrent.ThreadLocalRandom;

public class ContinuousWordCountExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.addSource(new RandomWordSource())
                .map(word -> new WordCount(word, 1))
                .keyBy(WordCount::word)
                .reduce((a, b) -> new WordCount(a.word(), a.count() + b.count()))
                .addSink(new FileSink<>("/tmp/wordcount-output.txt"));

        env.execute("ContinuousWordCount");
    }
}
