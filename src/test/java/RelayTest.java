import static org.junit.jupiter.api.Assertions.*;

import fr.kotlini.relay.*;
import fr.kotlini.relay.message.IMessageId;
import fr.kotlini.relay.message.base.ResponseBaseMessage;
import fr.kotlini.relay.message.sub.RequestMessage;
import fr.kotlini.relay.message.sub.ResponseMessage;
import fr.kotlini.relay.provider.MessageProviderType;
import fr.kotlini.relay.provider.credential.sub.RabbitMQCredentials;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;

class RelayTest {

  private RelayMessaging nodeA;
  private RelayMessaging nodeB;

  @BeforeEach
  void setUp() throws Exception {
    RabbitMQCredentials credentials =
        new RabbitMQCredentials("localhost", 5672, "guest", "guest", "test-queue");

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
  }

  @AfterEach
  void tearDown() throws Exception {
    nodeA.stop();
    nodeB.stop();
  }

  @Test
  void testSendMessageBetweenNodes() throws Exception {
    IMessageId messageId = () -> "test.message";

    CompletableFuture<String> future = new CompletableFuture<>();

    nodeB.registerListener(
        messageId,
        request -> {
            future.complete(((RequestMessage<String>) request).getPayload());
          return new ResponseMessage<>(
              request.getSource(),
              request.getDestination(),
              messageId.getId(),
              request.getId(),
              "ack-from-B");
        });

    nodeA.publish(() -> "node-B", messageId, "Hello from A");

    String received = future.get(2, TimeUnit.SECONDS);

    assertEquals("Hello from A", received);
  }

  @Test
  void testSendAndReceiveResponse() throws Exception {
    IMessageId messageId = () -> "test.response";

    nodeB.registerListener(
        messageId,
        request ->
            new ResponseMessage<>(
                request.getSource(),
                request.getDestination(),
                messageId.getId(),
                request.getId(),
                "pong"));

    CompletableFuture<ResponseBaseMessage> responseFuture =
        nodeA.publishWithResponse(() -> "node-B", messageId, "ping");

    ResponseBaseMessage response = responseFuture.get(2, TimeUnit.SECONDS);

    assertInstanceOf(ResponseMessage.class, response);
    assertEquals("pong", ((ResponseMessage<?>) response).getPayload());
  }

  @Test
  void testTimeoutWhenTargetUnavailable() throws Exception {
    IMessageId messageId = () -> "test.timeout";

    CompletableFuture<ResponseBaseMessage> future =
        nodeA.publishWithResponse(() -> "node-UNKNOWN", messageId, "data", 1, TimeUnit.SECONDS);

    assertThrows(ExecutionException.class, () -> future.get(2, TimeUnit.SECONDS));
  }
}
