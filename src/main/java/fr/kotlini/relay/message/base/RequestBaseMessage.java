package fr.kotlini.relay.message.base;

import fr.kotlini.relay.message.MessageType;

public abstract class RequestBaseMessage extends BaseMessage {

  protected final boolean requiresResponse;

  protected RequestBaseMessage(
      String source,
      String destination,
      String messageId,
      MessageType type,
      boolean requiresResponse) {
    super(source, destination, messageId, type);
    this.requiresResponse = requiresResponse;
  }

  protected RequestBaseMessage(
      String id,
      String source,
      String destination,
      String messageId,
      MessageType type,
      long timestamp,
      boolean requiresResponse) {
    super(id, source, destination, messageId, type, timestamp);
    this.requiresResponse = requiresResponse;
  }

  public boolean requiresResponse() {
    return requiresResponse;
  }
}
