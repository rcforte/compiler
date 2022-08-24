package backend;

import intermediate.ICode;
import intermediate.SymTabStack;
import message.Message;
import message.MessageHandler;
import message.MessageListener;
import message.MessageProducer;

public abstract class Backend implements MessageProducer {
    protected static MessageHandler messageHandler = new MessageHandler();
    protected static SymTabStack symTabStack;

    static {
        messageHandler = new MessageHandler();
    }

    protected ICode iCode;

    public void addMessageListener(MessageListener listener) {
        messageHandler.addMessageListener(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageHandler.removeMessageListener(listener);
    }

    public void sendMessage(Message message) {
        messageHandler.sendMessage(message);
    }

    public abstract void process(ICode iCode, SymTabStack symTabStack)
            throws Exception;
}
