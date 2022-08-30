package frontend.pascal.parsers;

import backend.interpreter.Executor;
import backend.interpreter.executors.ExpressionExecutor;
import backend.interpreter.executors.StatementExecutor;
import intermediate.ICodeNode;

public class IfExecutor extends StatementExecutor
{
   public IfExecutor(Executor parent)
   {
      super(parent);
   }

   public Object execute(ICodeNode node)
   {
      var children = node.getChildren();
      var exprNode = children.get(0);
      var thenStmtNode = children.get(1);
      var elseStmtNode = children.size() > 2 ? children.get(2) : null;

      var expressionExecutor = new ExpressionExecutor(this);
      var statementExecutor = new StatementExecutor(this);

      var b = (Boolean) expressionExecutor.execute(exprNode);
      if (b) statementExecutor.execute(thenStmtNode);
      else if (elseStmtNode != null) statementExecutor.execute(elseStmtNode);

      ++executionCount;
      return null;
   }
}
