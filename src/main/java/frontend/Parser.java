package frontend;

import intermediate.ICode;
import intermediate.SymTab;
import intermediate.SymTabFactory;
import intermediate.SymTabStack;
import message.Message;
import message.MessageHandler;
import message.MessageListener;
import message.MessageProducer;

public abstract class Parser
        implements MessageProducer {
    private final SymTab symtab;
    private final Scanner scanner;
    protected ICode iCode;
    protected SymTabStack symTabStack;
    protected MessageHandler messageHandler;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.symtab = null;
        this.iCode = null;
        this.symTabStack = SymTabFactory.createSymTabStack();
        this.messageHandler = new MessageHandler();
    }

    public abstract void parse()
            throws Exception;

    public abstract int getErrorCount();

    public SymTabStack getSymTabStack() {
        return symTabStack;
    }

    public ICode getICode() {
        return iCode;
    }

    public SymTab getSymtab() {
        return symtab;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public Token currentToken() {
        return scanner.currentToken();
    }

    public Token nextToken()
            throws Exception {
        return scanner.nextToken();
    }

    public void addMessageListener(MessageListener listener) {
        messageHandler.addMessageListener(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageHandler.removeMessageListener(listener);
    }

    public void sendMessage(Message message) {
        messageHandler.sendMessage(message);
    }
}