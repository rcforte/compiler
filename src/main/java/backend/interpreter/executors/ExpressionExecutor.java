package backend.interpreter.executors;

import backend.interpreter.Executor;
import intermediate.ICodeNode;
import intermediate.SymTabEntry;
import intermediate.icodeimpl.ICodeNodeTypeImpl;

import java.util.EnumSet;

import static backend.interpreter.RuntimeErrorCode.DIVISION_BY_ZERO;
import static intermediate.icodeimpl.ICodeKeyImpl.ID;
import static intermediate.icodeimpl.ICodeKeyImpl.VALUE;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static intermediate.symtabimpl.SymTabKeyImpl.DATA_VALUE;

public class ExpressionExecutor extends Executor
{
   private static final EnumSet<ICodeNodeTypeImpl> ARITH_OPS = EnumSet.of(
      ADD, SUBTRACT, MULTIPLY, FLOAT_DIVIDE, INTEGER_DIVIDE);

   public ExpressionExecutor(Executor parent)
   {
      super(parent);
   }

   public Object execute(ICodeNode node)
   {
      var nodeType = (ICodeNodeTypeImpl) node.getType();
      switch (nodeType)
      {
         case VARIABLE:
         {
            var entry = (SymTabEntry) node.getAttribute(ID);
            return entry.getAttribute(DATA_VALUE);
         }
         case INTEGER_CONSTANT:
         {
            return (Integer) node.getAttribute(VALUE);
         }
         case REAL_CONSTANT:
         {
            return (Float) node.getAttribute(VALUE);
         }
         case STRING_CONSTANT:
         {
            return (String) node.getAttribute(VALUE);
         }
         case NEGATE:
         {
            var expressionNode = node.getChildren().get(0);
            var value = execute(expressionNode);
            if (value instanceof Integer v)
               return -v;
            else
               return -((Float) value);
         }
         case NOT:
         {
            var expressionNode = node.getChildren().get(0);
            boolean value = (Boolean) execute(expressionNode);
            return !value;
         }
         default:
         {
            return executeBinaryOperator(node, nodeType);
         }
      }
   }


   private Object executeBinaryOperator(ICodeNode node, ICodeNodeTypeImpl nodeType)
   {
      var children = node.getChildren();
      var operandNode1 = children.get(0);
      var operandNode2 = children.get(1);

      var operand1 = execute(operandNode1);
      var operand2 = execute(operandNode2);

      boolean integerMode = (operand1 instanceof Integer) && (operand2 instanceof Integer);

      if (ARITH_OPS.contains(nodeType))
      {
         if (integerMode)
         {
            var value1 = (Integer) operand1;
            int value2 = (Integer) operand2;

            switch (nodeType)
            {
               case ADD:
                  return value1 + value2;
               case SUBTRACT:
                  return value1 - value2;
               case FLOAT_DIVIDE:
               {
                  if (value2 != 0)
                  {
                     return ((float) (value1) / (float) (value2));
                  }
                  else
                  {
                     errorHandler.flag(node, DIVISION_BY_ZERO, this);
                     return 0;
                  }
               }
               case INTEGER_DIVIDE:
               {
                  if (value2 != 0)
                  {
                     return value1 / value2;
                  }
                  else
                  {
                     errorHandler.flag(node, DIVISION_BY_ZERO, this);
                     return 0;
                  }
               }
               case MOD:
               {
                  if (value2 != 0)
                  {
                     return value1 % value2;
                  }
                  else
                  {
                     errorHandler.flag(node, DIVISION_BY_ZERO, this);
                     return 0;
                  }
               }
            }
         }
         else
         {
            var value1 = operand1 instanceof Integer ? (Integer) operand1 : (Float) operand1;
            var value2 = operand2 instanceof Integer ? (Integer) operand2 : (Float) operand2;

            switch (nodeType)
            {
               case ADD:
                  return value1 + value2;
               case SUBTRACT:
                  return value1 - value2;
               case MULTIPLY:
                  return value1 * value2;
               case FLOAT_DIVIDE:
               {
                  if (value2 != 0.0)
                  {
                     return value1 / value2;
                  }
                  else
                  {
                     errorHandler.flag(node, DIVISION_BY_ZERO, this);
                     return 0.0f;
                  }
               }
            }
         }
      }
      else if (nodeType == AND || nodeType == OR)
      {
         var value1 = (Boolean) operand1;
         var value2 = (Boolean) operand2;

         switch (nodeType)
         {
            case AND:
               return value1 && value2;
            case OR:
               return value1 || value2;
         }
      }
      else if (integerMode)
      {
         var value1 = (Integer) operand1;
         var value2 = (Integer) operand2;

         switch (nodeType)
         {
            case EQ:
               return value1 == value2;
            case NE:
               return value1 != value2;
            case LT:
               return value1 < value2;
            case LE:
               return value1 <= value2;
            case GT:
               return value1 > value2;
            case GE:
               return value1 >= value2;
         }
      }
      else
      {
         var value1 = (Float) operand1;
         var value2 = (Float) operand2;

         switch (nodeType)
         {
            case EQ:
               return value1 == value2;
            case NE:
               return value1 != value2;
            case LT:
               return value1 < value2;
            case LE:
               return value1 <= value2;
            case GT:
               return value1 > value2;
            case GE:
               return value1 >= value2;
         }
      }

      System.out.println("*** Error: The execution flow should never get here");
      return 0;
   }
}
