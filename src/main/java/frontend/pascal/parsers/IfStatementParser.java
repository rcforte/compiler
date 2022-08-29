package frontend.pascal.parsers;

import frontend.Token;
import frontend.pascal.PascalParserTD;
import frontend.pascal.PascalTokenType;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;
import intermediate.icodeimpl.ICodeNodeTypeImpl;

import java.util.EnumSet;

import static frontend.pascal.PascalErrorCode.MISSING_THEN;
import static frontend.pascal.PascalTokenType.ELSE;
import static frontend.pascal.PascalTokenType.THEN;

public class IfStatementParser extends StatementParser
{
   private static final EnumSet<PascalTokenType> THEN_SET =
      StatementParser.STATEMENT_START_SET.clone();

   static
   {
      THEN_SET.add(THEN);
      THEN_SET.addAll(StatementParser.STATEMENT_FOLLOW_SET);
   }

   public IfStatementParser(PascalParserTD parser)
   {
      super(parser);
   }

   public ICodeNode parse(Token token) throws Exception
   {
      token = nextToken();

      var ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);
      var expressionParser = new ExpressionParser(this);
      ifNode.addChild(expressionParser.parse(token));

      token = synchronize(THEN_SET);
      if (token.getType() == THEN) token = nextToken();
      else errorHandler.flag(token, MISSING_THEN, this);

      var statementParser = new StatementParser(this);
      ifNode.addChild(statementParser.parse(token));
      token = currentToken();

      if (token.getType() == ELSE)
      {
         token = nextToken();
         ifNode.addChild(statementParser.parse(token));
      }

      return ifNode;
   }
}
