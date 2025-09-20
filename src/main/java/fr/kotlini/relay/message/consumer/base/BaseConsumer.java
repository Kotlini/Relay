package fr.kotlini.relay.message.consumer.base;

import fr.kotlini.relay.RelayMessaging;
import fr.kotlini.relay.message.consumer.IConsumer;

public abstract class BaseConsumer implements IConsumer {

  protected final RelayMessaging main;

  public BaseConsumer(RelayMessaging main) {
    this.main = main;
  }
}
