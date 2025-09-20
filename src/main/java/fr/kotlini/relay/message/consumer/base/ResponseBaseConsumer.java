package fr.kotlini.relay.message.consumer.base;

import fr.kotlini.relay.RelayMessaging;
import fr.kotlini.relay.message.base.ResponseBaseMessage;
import fr.kotlini.relay.message.consumer.IResponseConsumer;
import java.util.concurrent.*;

public abstract class ResponseBaseConsumer extends BaseConsumer implements IResponseConsumer {

  protected final ConcurrentMap<String, CompletableFuture<ResponseBaseMessage>> pendingResponses;
  protected final ScheduledExecutorService timeOutExecutor;

  public ResponseBaseConsumer(RelayMessaging main) {
    super(main);
    this.pendingResponses = new ConcurrentHashMap<>();
    this.timeOutExecutor = Executors.newScheduledThreadPool(1);
  }

  @Override
  public void addPendingResponse(
      String messageId,
      CompletableFuture<ResponseBaseMessage> future,
      long timeout,
      TimeUnit unit) {
    timeOutExecutor.schedule(
        () -> {
          CompletableFuture<ResponseBaseMessage> futureResponse =
              pendingResponses.remove(messageId);
          if (futureResponse != null && !future.isDone()) {
            futureResponse.completeExceptionally(new TimeoutException("Response timeout!"));
          }
        },
        timeout,
        unit);
    this.pendingResponses.put(messageId, future);
  }

  @Override
  public void shutdown() {
    this.pendingResponses
        .values()
        .forEach(
            future -> {
              if (!future.isDone()) {
                future.completeExceptionally(new RuntimeException("Shutting down!"));
              }
            });
    this.pendingResponses.clear();

    // TODO SHUTDOWN !
    this.timeOutExecutor.shutdown();
  }
}
