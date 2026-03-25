package dev.streamprocessor.api;

import dev.streamprocessor.api.source.InMemorySource;
import dev.streamprocessor.api.source.Source;
import dev.streamprocessor.internal.OperatorNode;
import dev.streamprocessor.internal.OperatorType;
import dev.streamprocessor.internal.PipelineDeployer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StreamExecutionEnvironment {
    private final List<OperatorNode> sources = new ArrayList<>();
    private int port = 9080;
    private String restateUrl = "http://localhost:8080";

    private StreamExecutionEnvironment() {}

    public static StreamExecutionEnvironment getExecutionEnvironment() {
        return new StreamExecutionEnvironment();
    }

    public <T> DataStream<T> addSource(Source<T> source) {
        OperatorNode node = new OperatorNode(OperatorType.SOURCE, source);
        sources.add(node);
        return new DataStream<>(this, node);
    }

    public <T> DataStream<T> fromCollection(Collection<T> collection) {
        return addSource(new InMemorySource<>(collection));
    }

    public StreamExecutionEnvironment setPort(int port) {
        this.port = port;
        return this;
    }

    public StreamExecutionEnvironment setRestateUrl(String restateUrl) {
        this.restateUrl = restateUrl;
        return this;
    }

    public void execute() throws Exception {
        execute("stream-processor-job");
    }

    public void execute(String jobName) throws Exception {
        PipelineDeployer deployer = new PipelineDeployer(sources, port, restateUrl);
        deployer.deploy(jobName);
    }

    public List<OperatorNode> getSources() { return sources; }
    public int getPort() { return port; }
    public String getRestateUrl() { return restateUrl; }
}
