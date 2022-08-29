package backend.interpreter;

import backend.Backend;
import backend.interpreter.executors.StatementExecutor;
import intermediate.ICode;
import intermediate.SymTabStack;
import message.Message;
import message.MessageType;

public class Executor extends Backend
{
   protected static int executionCount;
   protected static RuntimeErrorHandler errorHandler;

   static
   {
      executionCount = 0;
      errorHandler = new RuntimeErrorHandler();
   }

   public Executor()
   {
   }

   public Executor(Executor parent)
   {
      super();
   }

   public RuntimeErrorHandler getErrorHandler()
   {
      return errorHandler;
   }

   @Override
   public void process(ICode iCode, SymTabStack symTabStack) throws Exception
   {
      this.symTabStack = symTabStack;
      this.iCode = iCode;

      var startTime = System.currentTimeMillis();
      var rootNode = iCode.getRoot();

      var statementExecutor = new StatementExecutor(this);
      statementExecutor.execute(rootNode);

      var endTime = System.currentTimeMillis();
      var elapsed = (float) (endTime - startTime) / 1000f;

      var runtimeErrors = errorHandler.getErrorCount();
      sendMessage(new Message(MessageType.INTERPRETER_SUMMARY,
         new Number[]{executionCount, runtimeErrors, elapsed}));
   }
}
