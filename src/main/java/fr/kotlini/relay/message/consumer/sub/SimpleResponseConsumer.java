package fr.kotlini.relay.message.consumer.sub;

import fr.kotlini.relay.RelayMessaging;
import fr.kotlini.relay.message.base.ResponseBaseMessage;
import fr.kotlini.relay.message.consumer.base.ResponseBaseConsumer;
import fr.kotlini.relay.message.sub.ResponseMessage;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleResponseConsumer extends ResponseBaseConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleResponseConsumer.class);

  public SimpleResponseConsumer(RelayMessaging main) {
    super(main);
  }

  @Override
  public void process(byte[] data) throws Exception {
    ResponseMessage<?> response = main.getCodec().decode(data, ResponseMessage.class);
    CompletableFuture<ResponseBaseMessage> future =
        pendingResponses.remove(response.getOriginalMessageId());

    if (future == null) {
      LOGGER.warn("Received response for unknown message {}", response.getOriginalMessageId());
      return;
    }

    future.complete(response);
  }
}
