package frontend.pascal.parsers;

import frontend.Token;
import frontend.pascal.PascalParserTD;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;

import static frontend.pascal.PascalErrorCode.MISSING_END;
import static frontend.pascal.PascalTokenType.END;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.COMPOUND;

public class CompoundStatementParser extends StatementParser
{
   public CompoundStatementParser(PascalParserTD parser)
   {
      super(parser);
   }

   public ICodeNode parse(Token token) throws Exception
   {
      token = nextToken();

      var compoundNode = ICodeFactory.createICodeNode(COMPOUND);
      var statementParser = new StatementParser(this);
      statementParser.parseList(token, compoundNode, END, MISSING_END);

      return compoundNode;
   }
}
