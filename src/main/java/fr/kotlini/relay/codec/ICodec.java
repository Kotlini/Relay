package fr.kotlini.relay.codec;

import fr.kotlini.relay.message.MessageType;

public interface ICodec {

  byte[] encode(Object object) throws Exception;

  <T> T decode(byte[] data, Class<T> clazz) throws Exception;

  MessageType decodeType(byte[] data) throws Exception;

  CodecType getCodecType();
}
