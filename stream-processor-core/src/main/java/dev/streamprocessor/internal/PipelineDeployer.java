package dev.streamprocessor.internal;

import dev.restate.sdk.endpoint.Endpoint;
import dev.restate.sdk.http.vertx.RestateHttpServer;
import dev.streamprocessor.api.functions.KeySelector;
import dev.streamprocessor.api.sink.Sink;
import dev.streamprocessor.api.source.Source;

import java.util.List;

/**
 * Configures the Restate services with the compiled pipeline plan,
 * starts the Restate HTTP endpoint, and drives source ingestion.
 */
public class PipelineDeployer {
    private final List<OperatorNode> sources;
    private final int port;
    private final String restateUrl;

    public PipelineDeployer(List<OperatorNode> sources, int port, String restateUrl) {
        this.sources = sources;
        this.port = port;
        this.restateUrl = restateUrl;
    }

    @SuppressWarnings("unchecked")
    public void deploy(String jobName) throws Exception {
        if (sources.isEmpty()) {
            throw new IllegalStateException("No sources defined in pipeline");
        }

        // For the demo, we support a single pipeline (first source)
        PipelinePlan plan = PipelineCompiler.compile(sources.get(0));

        // Configure the services with the pipeline plan
        configureSources(plan);
        configureKeyed(plan);
        configureSink(plan);

        System.out.println("[" + jobName + "] Starting Restate endpoint on port " + port + "...");

        // Build and start the Restate endpoint
        Endpoint.Builder builder = Endpoint.builder();
        builder.bind(new SourceOperatorService());
        if (plan.hasKeyBy()) {
            builder.bind(new KeyedOperatorService());
        }
        builder.bind(new SinkOperatorService());

        RestateHttpServer.listen(builder.build(), port);

        System.out.println("[" + jobName + "] Restate endpoint started on port " + port);
        System.out.println("[" + jobName + "] Register this deployment with Restate:");
        System.out.println("  restate deployments register http://localhost:" + port);
        System.out.println("[" + jobName + "] Then trigger source ingestion:");
        System.out.println("  curl -X POST http://localhost:8080/SourceOperatorService/ingest");
        System.out.println();
        System.out.println("[" + jobName + "] Waiting for deployment registration...");

        // Keep the server running
        Thread.currentThread().join();
    }

    @SuppressWarnings("unchecked")
    private void configureSources(PipelinePlan plan) {
        SourceOperatorService.source = (Source<Object>) plan.getSource();
        SourceOperatorService.preKeyByOps = plan.getPreKeyByOps();
        SourceOperatorService.keySelector = plan.hasKeyBy()
                ? (KeySelector<Object, Object>) plan.getKeySelector()
                : null;
        SourceOperatorService.hasKeyedStage = plan.hasKeyBy();
    }

    private void configureKeyed(PipelinePlan plan) {
        KeyedOperatorService.keyedOp = plan.getKeyedOp();
        KeyedOperatorService.postKeyedOps = plan.getPostKeyedOps();
    }

    @SuppressWarnings("unchecked")
    private void configureSink(PipelinePlan plan) {
        SinkOperatorService.sink = (Sink<Object>) plan.getSink();
    }
}
