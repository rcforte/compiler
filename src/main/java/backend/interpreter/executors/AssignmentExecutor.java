package backend.interpreter.executors;

import backend.interpreter.Executor;
import intermediate.ICodeNode;
import intermediate.SymTabEntry;
import message.Message;

import static intermediate.icodeimpl.ICodeKeyImpl.ID;
import static intermediate.icodeimpl.ICodeKeyImpl.LINE;
import static intermediate.symtabimpl.SymTabKeyImpl.DATA_VALUE;
import static message.MessageType.ASSIGN;

public class AssignmentExecutor extends Executor
{
   public AssignmentExecutor(Executor parent)
   {
      super(parent);
   }

   public Object execute(ICodeNode node)
   {
      var variableNode = node.getChildren().get(0);
      var expressionNode = node.getChildren().get(1);

      var expressionExecutor = new ExpressionExecutor(this);
      var value = expressionExecutor.execute(expressionNode);

      var variableId = (SymTabEntry) variableNode.getAttribute(ID);
      variableId.setAttribute(DATA_VALUE, value);

      sendMessage(node, variableId.getName(), value);

      ++executionCount;

      return null;
   }

   private void sendMessage(ICodeNode node, String variableName, Object value)
   {
      var lineNumber = node.getAttribute(LINE);
      if (lineNumber != null)
         sendMessage(new Message(ASSIGN, new Object[]{lineNumber, variableName, value}));
   }
}
