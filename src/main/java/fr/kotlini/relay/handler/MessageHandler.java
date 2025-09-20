package fr.kotlini.relay.handler;

import fr.kotlini.relay.RelayMessaging;
import fr.kotlini.relay.message.IMessage;
import fr.kotlini.relay.message.IMessageId;
import fr.kotlini.relay.message.MessageType;
import fr.kotlini.relay.message.base.ResponseBaseMessage;
import fr.kotlini.relay.message.consumer.IRequestConsumer;
import fr.kotlini.relay.message.consumer.IResponseConsumer;
import fr.kotlini.relay.message.consumer.sub.SimpleRequestConsumer;
import fr.kotlini.relay.message.consumer.sub.SimpleResponseConsumer;
import fr.kotlini.relay.message.sub.RequestMessage;
import fr.kotlini.relay.node.INode;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

  private final RelayMessaging main;
  private IRequestConsumer requestConsumer;
  private IResponseConsumer responseConsumer;

  public MessageHandler(RelayMessaging main) {
    this.main = main;
    this.requestConsumer = new SimpleRequestConsumer(main);
    this.responseConsumer = new SimpleResponseConsumer(main);
  }

  public void initialize() throws Exception {
    if (!main.getProvider().isConnected()) {
      main.getProvider().connect();
    }

    main.getProvider().subscribe(main.getNode().getId(), this::handleIncomingMessage);
  }

  private void handleIncomingMessage(byte[] messageData) {
    try {
      MessageType type = main.getCodec().decodeType(messageData);

      switch (type) {
        case REQUEST -> requestConsumer.process(messageData);
        case RESPONSE -> responseConsumer.process(messageData);
      }
    } catch (Exception e) {
      LOGGER.error("Error handling incoming message", e);
    }
  }

  public <T> void publish(INode node, IMessageId messageId, T payload) throws Exception {
    RequestMessage<T> message =
        new RequestMessage<>(
            this.main.getNode().getId(), node.getId(), messageId.getId(), payload, false);
    publish(node, message);
  }

  public <T> CompletableFuture<ResponseBaseMessage> publishWithResponse(
      INode node, IMessageId messageId, T payload) throws Exception {
    return publishWithResponse(node, messageId, payload, 30, TimeUnit.SECONDS);
  }

  public <T> CompletableFuture<ResponseBaseMessage> publishWithResponse(
      INode node, IMessageId messageId, T payload, long timeout, TimeUnit unit) throws Exception {
    RequestMessage<T> message =
        new RequestMessage<>(
            this.main.getNode().getId(), node.getId(), messageId.getId(), payload, true);

    CompletableFuture<ResponseBaseMessage> future = new CompletableFuture<>();
    this.responseConsumer.addPendingResponse(message.getId(), future, timeout, unit);
    publish(node, message);
    return future;
  }

  public void publish(INode node, IMessage message) throws Exception {
    this.main.getProvider().publish(node.getId(), this.main.getCodec().encode(message));
  }

  public void shutdown() throws Exception {
    this.requestConsumer.shutdown();
    this.responseConsumer.shutdown();
  }

  public void setRequestConsumer(IRequestConsumer requestConsumer) {
    this.requestConsumer = requestConsumer;
  }

  public void setResponseConsumer(IResponseConsumer responseConsumer) {
    this.responseConsumer = responseConsumer;
  }
}
