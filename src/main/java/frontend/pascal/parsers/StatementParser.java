package frontend.pascal.parsers;

import frontend.EofToken;
import frontend.Token;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalParserTD;
import frontend.pascal.PascalTokenType;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;

import java.util.EnumSet;

import static frontend.pascal.PascalErrorCode.MISSING_SEMICOLON;
import static frontend.pascal.PascalTokenType.*;
import static intermediate.icodeimpl.ICodeKeyImpl.LINE;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.NO_OP;

public class StatementParser extends PascalParserTD
{
   protected static final EnumSet<PascalTokenType> STATEMENT_START_SET =
      EnumSet.of(BEGIN, CASE, FOR, PascalTokenType.IF, REPEAT, WHILE, IDENTIFIER, SEMICOLON);
   protected static final EnumSet<PascalTokenType> STATEMENT_FOLLOW_SET =
      EnumSet.of(SEMICOLON, END, ELSE, UNTIL, DOT);

   public StatementParser(PascalParserTD parser)
   {
      super(parser);
   }

   public ICodeNode parse(Token token) throws Exception
   {
      var statementNode = (ICodeNode) switch ((PascalTokenType) token.getType())
         {
            case BEGIN -> new CompoundStatementParser(this).parse(token);
            case IDENTIFIER -> new AssignmentStatementParser(this).parse(token);
            case REPEAT -> new RepeatStatementParser(this).parse(token);
            case WHILE -> new WhileStatementParser(this).parse(token);
            case FOR -> new ForStatementParser(this).parse(token);
            case IF -> new IfStatementParser(this).parse(token);
            case CASE -> new CaseStatementParser(this).parse(token);
            default -> ICodeFactory.createICodeNode(NO_OP);
         };
      setLineNumber(statementNode, token);
      return statementNode;
   }

   public void setLineNumber(ICodeNode node, Token token)
   {
      if (node != null) node.setAttribute(LINE, token.getLineNumber());
   }

   public void parseList(Token token, ICodeNode parentNode, PascalTokenType terminator, PascalErrorCode errorCode)
      throws Exception
   {
      var terminatorSet = STATEMENT_START_SET.clone();
      terminatorSet.add(terminator);

      while (!(token instanceof EofToken)
         && (token.getType() != terminator))
      {
         var statementNode = parse(token);
         parentNode.addChild(statementNode);

         token = currentToken();
         var tokenType = token.getType();
         if (tokenType == SEMICOLON)
            token = nextToken();
         else if (STATEMENT_START_SET.contains(tokenType))
            errorHandler.flag(token, MISSING_SEMICOLON, this);
         token = synchronize(terminatorSet);
      }

      if (token.getType() == terminator) token = nextToken();
      else errorHandler.flag(token, errorCode, this);
   }
}
