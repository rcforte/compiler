package message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageHandler implements MessageProducer {
  private Message message;
  private final List<MessageListener> listeners = new CopyOnWriteArrayList<>();

  public void addMessageListener(MessageListener listener) {
    listeners.add(listener);
  }

  public void removeMessageListener(MessageListener listener) {
    listeners.remove(listener);
  }

  public void sendMessage(Message message) {
    this.message = message;
    for (var listener : listeners) {
      listener.messageReceived(message);
    }
  }
}
