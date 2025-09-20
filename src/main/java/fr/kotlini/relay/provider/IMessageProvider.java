package fr.kotlini.relay.provider;

public interface IMessageProvider {

  void publish(String channel, byte[] data) throws Exception;

  void subscribe(String channel, MessageConsumer consumer) throws Exception;

  void connect() throws Exception;

  void disconnect() throws Exception;

  boolean isConnected();

  @FunctionalInterface
  interface MessageConsumer {
    void accept(byte[] data);
  }
}
