package fr.kotlini.relay.provider;

import fr.kotlini.relay.provider.credential.ICredentials;
import fr.kotlini.relay.provider.credential.sub.RabbitMQCredentials;
import fr.kotlini.relay.provider.sub.RabbitMQProvider;
import java.util.function.Function;

public enum MessageProviderType implements IMessageProviderType {
  RABBIT_MQ(credentials -> new RabbitMQProvider((RabbitMQCredentials) credentials)),
  ;

  private final Function<ICredentials, IMessageProvider> factory;

  MessageProviderType(Function<ICredentials, IMessageProvider> factory) {
    this.factory = factory;
  }

  public <T extends ICredentials> IMessageProvider create(T credentials) {
    return factory.apply(credentials);
  }

  @Override
  public String getId() {
    return name();
  }
}
