package fr.kotlini.relay.message.base;

import fr.kotlini.relay.message.IMessage;
import fr.kotlini.relay.message.MessageType;
import java.util.UUID;

public abstract class BaseMessage implements IMessage {

  protected final String id;
  protected final String source;
  protected final String destination;
  protected final String messageId;
  protected final MessageType type;
  protected final long timestamp;

  protected BaseMessage(String source, String destination, String messageId, MessageType type) {
    this.id = UUID.randomUUID().toString();
    this.source = source;
    this.destination = destination;
    this.messageId = messageId;
    this.type = type;
    this.timestamp = System.currentTimeMillis();
  }

  protected BaseMessage(
      String id,
      String source,
      String destination,
      String messageId,
      MessageType type,
      long timestamp) {
    this.id = id;
    this.source = source;
    this.destination = destination;
    this.messageId = messageId;
    this.type = type;
    this.timestamp = timestamp;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public String getDestination() {
    return destination;
  }

  @Override
  public String getMessageId() {
    return messageId;
  }

  @Override
  public MessageType getType() {
    return type;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return String.format(
        "%s{id='%s', source='%s', destination='%s', messageType='%s'}",
        getClass().getSimpleName(), id, source, destination, type);
  }
}
