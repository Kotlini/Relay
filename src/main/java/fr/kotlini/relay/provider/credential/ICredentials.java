package fr.kotlini.relay.provider.credential;

import fr.kotlini.relay.provider.IMessageProviderType;

public interface ICredentials {

  String getHost();

  int getPort();

  String getUsername();

  String getPassword();

  IMessageProviderType getProviderType();
}
