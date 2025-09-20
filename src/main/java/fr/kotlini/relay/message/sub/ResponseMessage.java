package fr.kotlini.relay.message.sub;

import fr.kotlini.relay.message.MessageType;
import fr.kotlini.relay.message.base.ResponseBaseMessage;

public class ResponseMessage<T> extends ResponseBaseMessage {

  private T payload;

  public ResponseMessage(
      String source, String destination, String messageId, String originalMessageId, T payload) {
    super(source, destination, messageId, originalMessageId);
    this.payload = payload;
  }

  public ResponseMessage(
      String id,
      String source,
      String destination,
      String messageId,
      long timestamp,
      String originalMessageId,
      T payload) {
    super(id, source, destination, messageId, timestamp, originalMessageId);
    this.payload = payload;
  }

  public T getPayload() {
    return payload;
  }

  public void setPayload(T payload) {
    this.payload = payload;
  }

  @Override
  public MessageType getType() {
    return MessageType.RESPONSE;
  }
}
