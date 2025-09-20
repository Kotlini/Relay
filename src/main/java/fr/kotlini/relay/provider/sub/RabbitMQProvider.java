package fr.kotlini.relay.provider.sub;

import com.rabbitmq.client.*;
import fr.kotlini.relay.provider.IMessageProvider;
import fr.kotlini.relay.provider.credential.sub.RabbitMQCredentials;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQProvider implements IMessageProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProvider.class);

  private final RabbitMQCredentials credentials;
  private Connection connection;
  private Channel channel;
  private volatile boolean connected = false;
  private final ConcurrentMap<String, String> declaredQueues = new ConcurrentHashMap<>();

  public RabbitMQProvider(RabbitMQCredentials credentials) {
    this.credentials = credentials;
  }

  @Override
  public void connect() throws Exception {
    if (connected) {
      return;
    }

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(credentials.getHost());
    factory.setPort(credentials.getPort());
    factory.setUsername(credentials.getUsername());
    factory.setPassword(credentials.getPassword());
    factory.setVirtualHost(credentials.getVirtualHost());

    factory.setAutomaticRecoveryEnabled(true);
    factory.setNetworkRecoveryInterval(5000);
    factory.setConnectionTimeout(30000);

    connection = factory.newConnection();
    channel = connection.createChannel();

    declareQueue(credentials.getQueueName());

    connected = true;
  }

  @Override
  public void disconnect() throws Exception {
    if (!connected) {
      return;
    }

    connected = false;

    Exception lastException = null;

    if (channel != null && channel.isOpen()) {
      try {
        channel.close();
      } catch (Exception e) {
        lastException = e;
      }
    }

    if (connection != null && connection.isOpen()) {
      try {
        connection.close();
      } catch (Exception e) {
        lastException = e;
      }
    }

    declaredQueues.clear();

    if (lastException != null) {
      throw lastException;
    }
  }

  @Override
  public void publish(String channel, byte[] data) throws Exception {
    if (!connected || this.channel == null) {
      throw new IllegalStateException("Provider not connected!");
    }

    declareQueue(channel);

    try {
      this.channel.basicPublish("", channel, MessageProperties.PERSISTENT_BASIC, data);
    } catch (IOException e) {
      throw new Exception("Failed to publish message to " + channel, e);
    }
  }

  @Override
  public void subscribe(String channel, MessageConsumer messageConsumer) throws Exception {
    if (!connected || this.channel == null) {
      throw new IllegalStateException("Provider not connected");
    }

    declareQueue(channel);

    DeliverCallback deliverCallback =
        (consumerTag, delivery) -> {
          try {
            messageConsumer.accept(delivery.getBody());

            this.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

          } catch (Exception e) {
            LOGGER.error("Error processing message: {}", e.getMessage());

            try {
              this.channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            } catch (IOException ioException) {
              LOGGER.error("Failed to nack message: {}", ioException.getMessage());
            }
          }
        };

    CancelCallback cancelCallback =
        consumerTag -> System.out.println("Consumer cancelled: " + consumerTag);

    this.channel.basicQos(1);
    this.channel.basicConsume(channel, false, deliverCallback, cancelCallback);
  }

  @Override
  public boolean isConnected() {
    return connected
        && connection != null
        && connection.isOpen()
        && channel != null
        && channel.isOpen();
  }

  private void declareQueue(String queueName) throws Exception {
    if (declaredQueues.containsKey(queueName)) {
      return;
    }

    try {
      channel.queueDeclare(queueName, true, false, false, null);
      declaredQueues.put(queueName, queueName);
    } catch (IOException e) {
      throw new Exception("Failed to declare queue: " + queueName, e);
    }
  }
}
