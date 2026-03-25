package dev.streamprocessor.example;

public record WordCount(String word, int count) {
    @Override
    public String toString() {
        return word + ": " + count;
    }
}
