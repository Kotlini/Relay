package fr.kotlini.relay.provider.credential.sub;

import fr.kotlini.relay.provider.IMessageProviderType;
import fr.kotlini.relay.provider.MessageProviderType;
import fr.kotlini.relay.provider.credential.base.BaseCredentials;

public class RabbitMQCredentials extends BaseCredentials {

  private final String queueName;
  private final String virtualHost;

  public RabbitMQCredentials(
      String host, int port, String username, String password, String queueName) {
    this(host, port, username, password, "/", queueName);
  }

  public RabbitMQCredentials(
      String host,
      int port,
      String username,
      String password,
      String virtualHost,
      String queueName) {
    super(host, port, username, password);
    this.virtualHost = virtualHost;
    this.queueName = queueName;
  }

  @Override
  public IMessageProviderType getProviderType() {
    return MessageProviderType.RABBIT_MQ;
  }

  public String getVirtualHost() {
    return this.virtualHost;
  }

  public String getQueueName() {
    return this.queueName;
  }

  @Override
  public String toString() {
    return String.format(
        "RabbitMQCredentials{host='%s', port=%d, username='%s'}", host, port, username);
  }
}
