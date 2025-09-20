package fr.kotlini.relay.provider.credential.base;

import fr.kotlini.relay.provider.credential.ICredentials;

public abstract class BaseCredentials implements ICredentials {

  protected final String host;
  protected final int port;
  protected final String username;
  protected final String password;

  public BaseCredentials(String host, int port, String username, String password) {
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
  }

  @Override
  public String getHost() {
    return this.host;
  }

  @Override
  public int getPort() {
    return this.port;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public String getPassword() {
    return this.password;
  }
}
