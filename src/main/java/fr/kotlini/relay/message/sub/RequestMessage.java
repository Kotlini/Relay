package fr.kotlini.relay.message.sub;

import fr.kotlini.relay.message.MessageType;
import fr.kotlini.relay.message.base.RequestBaseMessage;

public class RequestMessage<T> extends RequestBaseMessage {

  private T payload;
  private String payloadType;

  public RequestMessage(
      String source, String destination, String messageId, T payload, boolean requiresResponse) {
    super(source, destination, messageId, MessageType.REQUEST, requiresResponse);
    this.payload = payload;
    this.payloadType = payload != null ? payload.getClass().getName() : null;
  }

  public T getPayload() {
    return payload;
  }

  public void setPayload(T payload) {
    this.payload = payload;
  }

    public String getPayloadType() {
        return payloadType;
    }
}
