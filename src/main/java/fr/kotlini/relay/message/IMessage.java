package fr.kotlini.relay.message;

public interface IMessage {

  String getId();

  String getSource();

  String getDestination();

  String getMessageId();

  MessageType getType();

  long getTimestamp();
}
