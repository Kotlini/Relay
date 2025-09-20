package fr.kotlini.relay.message.consumer.sub;

import fr.kotlini.relay.IMessageListener;
import fr.kotlini.relay.RelayMessaging;
import fr.kotlini.relay.message.base.ResponseBaseMessage;
import fr.kotlini.relay.message.consumer.base.RequestBaseConsumer;
import fr.kotlini.relay.message.sub.RequestMessage;
import fr.kotlini.relay.model.AckResponse;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRequestConsumer extends RequestBaseConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRequestConsumer.class);
  private static final AckResponse SUCCESS_RESPONSE = new AckResponse(true, "Success !");
  private static final AckResponse ERROR_RESPONSE = new AckResponse(false, "No listener found !");

  public SimpleRequestConsumer(RelayMessaging main) {
    super(main);
  }

  @Override
  public void process(byte[] data) throws Exception {
    RelayMessaging main = super.main;
    RequestMessage<?> request = main.getCodec().decode(data, RequestMessage.class);
    IMessageListener listener = main.getListenerHandler().getListener(request.getMessageId());
    if (listener == null) {
      if (request.requiresResponse()) {
        main.getProvider().publish(request.getSource(), main.getCodec().encode(ERROR_RESPONSE));
      } else {
        LOGGER.warn("No listener for message type {}", request.getMessageId());
      }
      return;
    }

    ResponseBaseMessage response = listener.handle(request);
    if (request.requiresResponse()) {
      main.getProvider()
          .publish(
              request.getSource(),
              main.getCodec().encode(Objects.requireNonNullElse(response, SUCCESS_RESPONSE)));
    }
  }
}
