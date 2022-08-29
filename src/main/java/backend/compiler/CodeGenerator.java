package backend.compiler;

import backend.Backend;
import intermediate.ICode;
import intermediate.SymTabStack;
import message.Message;
import message.MessageType;

public class CodeGenerator extends Backend
{
   @Override
   public void process(ICode iCode, SymTabStack symTabStack)
   {
      long t0 = System.currentTimeMillis();
      long t1 = System.currentTimeMillis();
      int instructionCount = 0;
      float elapsed = (t1 - t0) / 1000f;
      sendMessage(new Message(MessageType.COMPILER_SUMMARY, new Number[]{instructionCount, elapsed}));
   }
}
