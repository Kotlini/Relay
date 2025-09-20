package fr.kotlini.relay.message.consumer;

public interface IConsumer {

  void process(byte[] data) throws Exception;

  void shutdown() throws Exception;
}
