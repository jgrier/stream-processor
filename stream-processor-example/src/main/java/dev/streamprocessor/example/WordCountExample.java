package dev.streamprocessor.example;

import dev.streamprocessor.api.Collector;
import dev.streamprocessor.api.StreamExecutionEnvironment;
import dev.streamprocessor.api.sink.ConsoleSink;

import java.util.List;

public class WordCountExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.fromCollection(List.of(
                        "hello world",
                        "hello restate",
                        "world of stream processing"
                ))
                .flatMap((String line, Collector<String> out) -> {
                    for (String word : line.split("\\s+")) {
                        out.collect(word);
                    }
                })
                .map(word -> new WordCount(word, 1))
                .keyBy(WordCount::word)
                .reduce((a, b) -> new WordCount(a.word(), a.count() + b.count()))
                .addSink(new ConsoleSink<>());

        env.execute("WordCount");
    }
}
