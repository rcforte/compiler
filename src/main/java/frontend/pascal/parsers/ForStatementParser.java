package frontend.pascal.parsers;

import frontend.Token;
import frontend.pascal.PascalParserTD;
import frontend.pascal.PascalTokenType;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;

import java.util.EnumSet;

import static frontend.pascal.PascalErrorCode.MISSING_DO;
import static frontend.pascal.PascalErrorCode.MISSING_TO_DOWNTO;
import static frontend.pascal.PascalTokenType.*;
import static intermediate.icodeimpl.ICodeKeyImpl.VALUE;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.*;

public class ForStatementParser extends StatementParser
{
   private static EnumSet<PascalTokenType> TO_DOWNTO_SET =
      ExpressionParser.EXPRESSION_START_SET.clone();

   static
   {
      TO_DOWNTO_SET.add(TO);
      TO_DOWNTO_SET.add(DOWNTO);
      TO_DOWNTO_SET.addAll(StatementParser.STATEMENT_FOLLOW_SET);
   }

   private static final EnumSet<PascalTokenType> DO_SET =
      StatementParser.STATEMENT_START_SET.clone();

   static
   {
      DO_SET.add(DO);
      DO_SET.addAll(StatementParser.STATEMENT_FOLLOW_SET);
   }

   public ForStatementParser(PascalParserTD parser)
   {
      super(parser);
   }

   public ICodeNode parse(Token token) throws Exception
   {
      token = nextToken();
      var targetToken = token;

      var compoundNode = ICodeFactory.createICodeNode(COMPOUND);
      var loopNode = ICodeFactory.createICodeNode(LOOP);
      var testNode = ICodeFactory.createICodeNode(TEST);

      var assignmentParser = new AssignmentStatementParser(this);
      var initAssignNode = assignmentParser.parse(token);

      setLineNumber(initAssignNode, targetToken);

      compoundNode.addChild(initAssignNode);
      compoundNode.addChild(loopNode);

      token = synchronize(TO_DOWNTO_SET);
      var direction = token.getType();
      if (direction == TO || direction == DOWNTO)
      {
         token = nextToken();
      }
      else
      {
         direction = TO;
         errorHandler.flag(token, MISSING_TO_DOWNTO, this);
      }

      var relOpNode = ICodeFactory.createICodeNode(direction == TO ? GT : LT);
      var controlVarNode = initAssignNode.getChildren().get(0);
      relOpNode.addChild(controlVarNode.copy());

      var expressionParser = new ExpressionParser(this);
      relOpNode.addChild(expressionParser.parse(token));

      testNode.addChild(relOpNode);
      loopNode.addChild(testNode);

      token = synchronize(DO_SET);
      if (token.getType() == DO) token = nextToken();
      else errorHandler.flag(token, MISSING_DO, this);

      var statementParser = new StatementParser(this);
      loopNode.addChild(statementParser.parse(token));

      var nextAssignNode = ICodeFactory.createICodeNode(ASSIGN);
      nextAssignNode.addChild(controlVarNode.copy());

      var arithOpNode = ICodeFactory.createICodeNode(direction == TO ? ADD : SUBTRACT);
      arithOpNode.addChild(controlVarNode.copy());

      var oneNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
      oneNode.setAttribute(VALUE, 1);
      arithOpNode.addChild(oneNode);

      nextAssignNode.addChild(arithOpNode);
      loopNode.addChild(nextAssignNode);

      setLineNumber(nextAssignNode, targetToken);

      return compoundNode;
   }
}
