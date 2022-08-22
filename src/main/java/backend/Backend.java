package backend;

import intermediate.ICode;
import intermediate.SymTab;
import intermediate.SymTabStack;
import message.Message;
import message.MessageHandler;
import message.MessageListener;
import message.MessageProducer;

public abstract class Backend implements MessageProducer {
  private final MessageHandler messageHandler = new MessageHandler();

  public void addMessageListener(MessageListener l) {
    messageHandler.addMessageListener(l);
  }

  public void removeMessageListener(MessageListener l) {
    messageHandler.removeMessageListener(l);
  }

  public void sendMessage(Message message) {
    messageHandler.sendMessage(message);
  }

  public abstract void process(ICode icode, SymTabStack symtab);
}
