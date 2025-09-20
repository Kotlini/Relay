package fr.kotlini.relay.model;

import fr.kotlini.relay.message.IMessageId;
import java.io.Serializable;

public record AckResponse(boolean success, String message) implements Serializable {

  public static final IMessageId ID = () -> "ASK";

  @Override
  public String toString() {
    return "AckResponse{success=" + success + ", message='" + message + "'}";
  }
}
