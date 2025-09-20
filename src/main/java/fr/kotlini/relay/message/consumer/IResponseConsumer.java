package fr.kotlini.relay.message.consumer;

import fr.kotlini.relay.message.base.ResponseBaseMessage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface IResponseConsumer extends IConsumer {

  void addPendingResponse(
      String messageId, CompletableFuture<ResponseBaseMessage> future, long timeout, TimeUnit unit);
}
