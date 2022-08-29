package frontend;

import intermediate.ICode;
import intermediate.SymTabFactory;
import intermediate.SymTabStack;
import message.Message;
import message.MessageHandler;
import message.MessageListener;
import message.MessageProducer;

public abstract class Parser implements MessageProducer
{
   protected static SymTabStack symTabStack;
   protected static MessageHandler messageHandler;

   static
   {
      symTabStack = SymTabFactory.createSymTabStack();
      messageHandler = new MessageHandler();
   }

   private final Scanner scanner;
   protected ICode iCode;

   public Parser(Scanner scanner)
   {
      this.scanner = scanner;
      this.iCode = null;
   }

   public abstract void parse() throws Exception;

   public abstract int getErrorCount();

   public SymTabStack getSymTabStack()
   {
      return symTabStack;
   }

   public ICode getICode()
   {
      return iCode;
   }

   public Scanner getScanner()
   {
      return scanner;
   }

   public Token currentToken()
   {
      return scanner.currentToken();
   }

   public Token nextToken() throws Exception
   {
      return scanner.nextToken();
   }

   public void addMessageListener(MessageListener listener)
   {
      messageHandler.addMessageListener(listener);
   }

   public void removeMessageListener(MessageListener listener)
   {
      messageHandler.removeMessageListener(listener);
   }

   public void sendMessage(Message message)
   {
      messageHandler.sendMessage(message);
   }
}