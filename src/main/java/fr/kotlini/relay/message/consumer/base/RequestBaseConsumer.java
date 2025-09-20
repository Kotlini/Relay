package fr.kotlini.relay.message.consumer.base;

import fr.kotlini.relay.RelayMessaging;
import fr.kotlini.relay.message.consumer.IRequestConsumer;

public abstract class RequestBaseConsumer extends BaseConsumer implements IRequestConsumer {

  public RequestBaseConsumer(RelayMessaging main) {
    super(main);
  }

  @Override
  public void shutdown() throws Exception {}
}
