package backend.interpreter.executors;

import backend.interpreter.Executor;
import intermediate.ICodeNode;

import java.util.List;

import static intermediate.icodeimpl.ICodeKeyImpl.VALUE;

public class SelectExecutor extends StatementExecutor
{
   public SelectExecutor(Executor parent)
   {
      super(parent);
   }

   public Object execute(ICodeNode node)
   {
      var selectChildren = node.getChildren();
      var exprNode = selectChildren.get(0);
      var expressionExecutor = new ExpressionExecutor(this);
      var selectValue = expressionExecutor.execute(exprNode);
      var selectedBranchNode = searchBranches(selectValue, selectChildren);
      if (selectedBranchNode != null)
      {
         var stmtNode = selectedBranchNode.getChildren().get(1);
         var statementExecutor = new StatementExecutor(this);
         statementExecutor.execute(stmtNode);
      }
      ++executionCount;
      return null;
   }

   private ICodeNode searchBranches(Object selectValue, List<ICodeNode> selectChildren)
   {
      for (var i = 1; i < selectChildren.size(); i++)
      {
         var branchNode = selectChildren.get(i);
         if (searchConstants(selectValue, branchNode)) return branchNode;
      }
      return null;
   }

   private boolean searchConstants(Object selectValue, ICodeNode branchNode)
   {
      var integerMode = selectValue instanceof Integer;
      var constantsNode = branchNode.getChildren().get(0);
      var constantsList = constantsNode.getChildren();

      if (selectValue instanceof Integer value)
      {
         for (var constantNode : constantsList)
         {
            var constant = (Integer) constantNode.getAttribute(VALUE);
            if (value.equals(constant)) return true;
         }
      }
      else
      {
         var value = (String) selectValue;
         for (var constantNode : constantsList)
         {
            String constant = (String) constantNode.getAttribute(VALUE);
            if (value.equals(constant)) return true;
         }
      }
      return false;
   }
}
