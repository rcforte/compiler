package frontend.pascal.parsers;

import frontend.Token;
import frontend.pascal.PascalParserTD;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;

import static frontend.pascal.PascalErrorCode.MISSING_UNTIL;
import static frontend.pascal.PascalTokenType.UNTIL;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.LOOP;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.TEST;

public class RepeatStatementParser extends StatementParser
{
   public RepeatStatementParser(PascalParserTD parser)
   {
      super(parser);
   }

   public ICodeNode parse(Token token) throws Exception
   {
      token = nextToken();

      var loopNode = ICodeFactory.createICodeNode(LOOP);
      var testNode = ICodeFactory.createICodeNode(TEST);

      var statementParser = new StatementParser(this);
      statementParser.parseList(token, loopNode, UNTIL, MISSING_UNTIL);
      token = currentToken();

      var expressionParser = new ExpressionParser(this);
      testNode.addChild(expressionParser.parse(token));
      loopNode.addChild(testNode);

      return loopNode;
   }
}
