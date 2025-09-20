package fr.kotlini.relay;

import fr.kotlini.relay.codec.ICodec;
import fr.kotlini.relay.codec.sub.JsonCodec;
import fr.kotlini.relay.handler.ListenerHandler;
import fr.kotlini.relay.handler.MessageHandler;
import fr.kotlini.relay.message.IMessage;
import fr.kotlini.relay.message.IMessageId;
import fr.kotlini.relay.message.base.ResponseBaseMessage;
import fr.kotlini.relay.node.INode;
import fr.kotlini.relay.provider.IMessageProvider;
import fr.kotlini.relay.provider.MessageProviderType;
import fr.kotlini.relay.provider.credential.ICredentials;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelayMessaging {

  private static final Logger LOGGER = LoggerFactory.getLogger(RelayMessaging.class);

  private final IMessageProvider provider;
  private final INode node;
  private final ICodec codec;
  private final ListenerHandler listenerHandler;
  private final MessageHandler messageHandler;

  private RelayMessaging(IMessageProvider provider, INode node, ICodec codec) {
    this.provider = provider;
    this.node = node;
    this.codec = codec;
    this.messageHandler = new MessageHandler(this);
    this.listenerHandler = new ListenerHandler();
  }

  public void start() throws Exception {
    try {
      messageHandler.initialize();
    } catch (Exception e) {
      LOGGER.error("Failed to start KingConnect: {}", e.getMessage());
      throw e;
    }
  }

  public void stop() throws Exception {
    try {
      this.messageHandler.shutdown();

      if (this.provider.isConnected()) {
        this.provider.disconnect();
      }

      this.listenerHandler.clear();
    } catch (Exception e) {
      LOGGER.error("Error stopping KingConn   ectv: {}", e.getMessage());
      throw e;
    }
  }

  public boolean isRunning() {
    return this.provider.isConnected();
  }

  public <T> void registerListener(IMessageId messageId, IMessageListener listener) {
    listenerHandler.registerListener(messageId, listener);
  }

  public void unregisterListener(String messageType) {
    listenerHandler.unregisterListener(messageType);
  }

  public void nativePublish(INode node, IMessage message) throws Exception {
    nativePublish(node.getId(), message);
  }

  public void nativePublish(String nodeId, IMessage message) throws Exception {
    this.provider.publish(nodeId, this.codec.encode(message));
  }

  public <T> void publish(INode node, IMessageId messageId, T payload) throws Exception {
    messageHandler.publish(node, messageId, payload);
  }

  public <T> CompletableFuture<ResponseBaseMessage> publishWithResponse(
      INode node, IMessageId messageId, T payload) throws Exception {
    return messageHandler.publishWithResponse(node, messageId, payload);
  }

  public <T> CompletableFuture<ResponseBaseMessage> publishWithResponse(
      INode node, IMessageId messageId, T payload, long timeout, TimeUnit unit) throws Exception {
    return messageHandler.publishWithResponse(node, messageId, payload, timeout, unit);
  }

  public <T> void broadcast(IMessageId messageId, T payload, INode... nodes) {
    for (INode node : nodes) {
      if (Objects.equals(node.getId(), this.node.getId())) {
        continue;
      }

      try {
        publish(node, messageId, payload);
      } catch (Exception e) {
        LOGGER.error("Failed to broadcast to {}: {}", node.getId(), e.getMessage());
      }
    }
  }

  public IMessageProvider getProvider() {
    return provider;
  }

  public INode getNode() {
    return node;
  }

  public ICodec getCodec() {
    return codec;
  }

  public MessageHandler getMessageHandler() {
    return messageHandler;
  }

  public ListenerHandler getListenerHandler() {
    return listenerHandler;
  }

  public static class Builder {

    private INode node;
    private IMessageProvider provider;
    private ICodec codec = new JsonCodec();

    public Builder nodeId(INode node) {
      this.node = node;
      return this;
    }

    public Builder provider(MessageProviderType type, ICredentials credentials) {
      this.provider = type.create(credentials);
      return this;
    }

    public Builder codec(ICodec codec) {
      this.codec = codec;
      return this;
    }

    public RelayMessaging build() {
      if (node == null) {
        throw new IllegalArgumentException("Node cannot be null");
      }
      if (provider == null) {
        throw new IllegalArgumentException("Provider cannot be null");
      }

      return new RelayMessaging(provider, node, codec);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
