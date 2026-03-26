package dev.streamprocessor.api.sink;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileSink<T> implements Sink<T> {
    private final String filePath;
    private transient PrintWriter writer;

    public FileSink(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void open() throws Exception {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)), true);
    }

    @Override
    public void write(T value) {
        if (writer == null) {
            try {
                open();
            } catch (Exception e) {
                throw new RuntimeException("Failed to open file sink", e);
            }
        }
        writer.println(value);
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
