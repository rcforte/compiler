package backend.interpreter.executors;

import backend.interpreter.Executor;
import intermediate.ICodeNode;
import intermediate.icodeimpl.ICodeNodeTypeImpl;

import static intermediate.icodeimpl.ICodeNodeTypeImpl.TEST;

public class LoopExecutor extends StatementExecutor
{
   public LoopExecutor(Executor parent)
   {
      super(parent);
   }

   public Object execute(ICodeNode node)
   {
      var exprNode = (ICodeNode) null;
      var loopChildren = node.getChildren();

      var expressionExecutor = new ExpressionExecutor(this);
      var statementExecutor = new StatementExecutor(this);

      var exitLoop = false;
      while (!exitLoop)
      {
         ++executionCount;
         for (var child : loopChildren)
         {
            var childType = (ICodeNodeTypeImpl) child.getType();
            if (childType == TEST)
            {
               if (exprNode == null) exprNode = child.getChildren().get(0);
               exitLoop = (Boolean) expressionExecutor.execute(exprNode);
            }
            else
            {
               statementExecutor.execute(child);
            }
         }
         if (exitLoop) break;
      }

      return null;
   }
}
