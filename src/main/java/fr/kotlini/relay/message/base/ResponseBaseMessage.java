package fr.kotlini.relay.message.base;

import fr.kotlini.relay.message.MessageType;

public abstract class ResponseBaseMessage extends BaseMessage {

  protected final String originalMessageId;

  public ResponseBaseMessage(
      String source, String destination, String messageId, String originalMessageId) {
    super(source, destination, messageId, MessageType.RESPONSE);
    this.originalMessageId = originalMessageId;
  }

  public ResponseBaseMessage(
      String id,
      String source,
      String destination,
      String messageId,
      long timestamp,
      String originalMessageId) {
    super(id, source, destination, messageId, MessageType.RESPONSE, timestamp);
    this.originalMessageId = originalMessageId;
  }

  public String getOriginalMessageId() {
    return originalMessageId;
  }
}
