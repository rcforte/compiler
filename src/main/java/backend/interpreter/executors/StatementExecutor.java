package backend.interpreter.executors;

import backend.interpreter.Executor;
import backend.interpreter.RuntimeErrorCode;
import intermediate.ICodeNode;
import intermediate.icodeimpl.ICodeNodeTypeImpl;
import message.Message;

import static intermediate.icodeimpl.ICodeKeyImpl.LINE;
import static message.MessageType.SOURCE_LINE;

public class StatementExecutor extends Executor
{
   public StatementExecutor(Executor parent)
   {
      super(parent);
   }

   public Object execute(ICodeNode node)
   {
      sendSourceLineMessage(node);

      return switch ((ICodeNodeTypeImpl) node.getType())
         {
            case COMPOUND -> new CompoundExecutor(this).execute(node);
            case ASSIGN -> new AssignmentExecutor(this).execute(node);
            case NO_OP -> null;
            default ->
            {
               errorHandler.flag(node, RuntimeErrorCode.UNIMPLEMENTED_FEATURE, this);
               yield null;
            }
         };
   }

   private void sendSourceLineMessage(ICodeNode node)
   {
      var line = node.getAttribute(LINE);
      if (line != null)
         sendMessage(new Message(SOURCE_LINE, new Object[]{line, "<No line>"}));
   }
}
