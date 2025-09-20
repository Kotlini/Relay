package fr.kotlini.relay;

import fr.kotlini.relay.message.base.RequestBaseMessage;
import fr.kotlini.relay.message.base.ResponseBaseMessage;

@FunctionalInterface
public interface IMessageListener {

  ResponseBaseMessage handle(RequestBaseMessage message);
}
