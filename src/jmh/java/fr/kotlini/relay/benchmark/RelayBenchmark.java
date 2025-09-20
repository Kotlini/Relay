package fr.kotlini.relay.benchmark;

import fr.kotlini.relay.*;
import fr.kotlini.relay.message.IMessageId;
import fr.kotlini.relay.message.base.ResponseBaseMessage;
import fr.kotlini.relay.message.sub.ResponseMessage;
import fr.kotlini.relay.node.INode;
import fr.kotlini.relay.provider.MessageProviderType;
import fr.kotlini.relay.provider.credential.sub.RabbitMQCredentials;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class RelayBenchmark {

    private RelayMessaging nodeA;
    private RelayMessaging nodeB;

    private IMessageId messageId;
    private INode nodeBRef;

    @Setup(Level.Iteration)
    public void setUp() throws Exception {
        RabbitMQCredentials credentials =
                new RabbitMQCredentials("localhost", 5672, "guest", "guest", "bench-queue");

        nodeA =
                RelayMessaging.builder()
                        .nodeId(() -> "node-A")
                        .provider(MessageProviderType.RABBIT_MQ, credentials)
                        .build();

        nodeB =
                RelayMessaging.builder()
                        .nodeId(() -> "node-B")
                        .provider(MessageProviderType.RABBIT_MQ, credentials)
                        .build();

        nodeA.start();
        nodeB.start();

        messageId = () -> "bench.message";
        nodeBRef = () -> "node-B";

        nodeB.registerListener(
                messageId,
                request ->
                        new ResponseMessage<>(
                                request.getDestination(),
                                request.getSource(),
                                messageId.getId(),
                                request.getId(),
                                "pong-from-B"));
    }

    @TearDown(Level.Iteration)
    public void tearDown() throws Exception {
        nodeA.stop();
        nodeB.stop();
    }

    @Benchmark
    @Threads(4)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testPublishWithResponseAtoB() throws Exception {
        CompletableFuture<ResponseBaseMessage> future =
                nodeA.publishWithResponse(nodeBRef, messageId, "ping-from-A");

        future.get(1, TimeUnit.SECONDS);
    }
}
