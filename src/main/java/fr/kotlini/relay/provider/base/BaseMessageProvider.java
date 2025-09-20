package fr.kotlini.relay.provider.base;

import fr.kotlini.relay.provider.IMessageProvider;
import fr.kotlini.relay.provider.credential.ICredentials;

public abstract class BaseMessageProvider<T extends ICredentials> implements IMessageProvider {

  protected final T credentials;
  protected boolean connected;

  public BaseMessageProvider(T credentials) {
    this.credentials = credentials;
    this.connected = false;
  }

  @Override
  public boolean isConnected() {
    return this.connected;
  }
}
