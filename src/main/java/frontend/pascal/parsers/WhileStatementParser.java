package frontend.pascal.parsers;

import frontend.Token;
import frontend.pascal.PascalParserTD;
import frontend.pascal.PascalTokenType;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;
import intermediate.icodeimpl.ICodeNodeTypeImpl;

import java.util.EnumSet;

import static frontend.pascal.PascalErrorCode.MISSING_DO;
import static frontend.pascal.PascalTokenType.DO;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.LOOP;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.TEST;

public class WhileStatementParser extends StatementParser
{
   private static final EnumSet<PascalTokenType> DO_SET = StatementParser.STATEMENT_START_SET.clone();

   static
   {
      DO_SET.add(DO);
      DO_SET.addAll(StatementParser.STATEMENT_FOLLOW_SET);
   }

   public WhileStatementParser(PascalParserTD parser)
   {
      super(parser);
   }

   public ICodeNode parse(Token token) throws Exception
   {
      token = nextToken();

      var loopNode = ICodeFactory.createICodeNode(LOOP);
      var breakNode = ICodeFactory.createICodeNode(TEST);
      var notNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

      loopNode.addChild(breakNode);
      breakNode.addChild(notNode);

      var expressionParser = new ExpressionParser(this);
      notNode.addChild(expressionParser.parse(token));

      token = synchronize(DO_SET);
      if (token.getType() == DO) token = nextToken();
      else errorHandler.flag(token, MISSING_DO, this);

      var statementParser = new StatementParser(this);
      loopNode.addChild(statementParser.parse(token));
      return loopNode;
   }
}
