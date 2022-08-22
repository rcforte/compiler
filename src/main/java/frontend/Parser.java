package frontend;

import intermediate.ICode;
import intermediate.SymTab;
import intermediate.SymTabFactory;
import intermediate.SymTabStack;
import message.Message;
import message.MessageHandler;
import message.MessageListener;
import message.MessageProducer;

public abstract class Parser implements MessageProducer {

  private final SymTab symtab;
  private final Scanner scanner;
  private final ICode icode;
  protected SymTabStack symTabStack;
  protected MessageHandler messageHandler;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
    this.symtab = null;
    this.icode = null;
    this.symTabStack = SymTabFactory.createSymTabStack();
    this.messageHandler = new MessageHandler();
  }

  public SymTabStack getSymTabStack() {
    return symTabStack;
  }

  public ICode getICode() {
    return icode;
  }

  public SymTab getSymtab() {
    return symtab;
  }

  public abstract void parse() throws Exception;

  public abstract int getErrorCount();

  public Token currentToken() {
    return scanner.currentToken();
  }

  public Token nextToken() throws Exception {
    return scanner.nextToken();
  }

  public void addMessageListener(MessageListener l) {
    messageHandler.addMessageListener(l);
  }

  public void removeMessageListener(MessageListener l) {
    messageHandler.removeMessageListener(l);
  }

  public void sendMessage(Message m) {
    messageHandler.sendMessage(m);
  }
}