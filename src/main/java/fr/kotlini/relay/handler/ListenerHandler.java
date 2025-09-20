package fr.kotlini.relay.handler;

import fr.kotlini.relay.IMessageListener;
import fr.kotlini.relay.message.IMessageId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ListenerHandler {

  private final ConcurrentMap<String, IMessageListener> listeners;

  public ListenerHandler() {
    this.listeners = new ConcurrentHashMap<>();
  }

  public ListenerHandler(ConcurrentMap<String, IMessageListener> listeners) {
    this.listeners = listeners;
  }

  public void registerListener(IMessageId messageId, IMessageListener listener) {
    registerListener(messageId.getId(), listener);
  }

  public void registerListener(String messageType, IMessageListener listener) {
    this.listeners.put(messageType, listener);
  }

  public void unregisterListener(String messageType) {
    this.listeners.remove(messageType);
  }

  public IMessageListener getListener(String messageType) {
    return this.listeners.get(messageType);
  }

  public IMessageListener getListener(IMessageId messageId) {
    return getListener(messageId.getId());
  }

  public void clear() {
    this.listeners.clear();
  }

  public ConcurrentMap<String, IMessageListener> getListeners() {
    return this.listeners;
  }
}
