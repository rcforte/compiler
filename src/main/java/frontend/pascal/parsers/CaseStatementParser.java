package frontend.pascal.parsers;

import frontend.EofToken;
import frontend.Token;
import frontend.TokenType;
import frontend.pascal.PascalParserTD;
import frontend.pascal.PascalTokenType;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static frontend.pascal.PascalErrorCode.*;
import static frontend.pascal.PascalTokenType.*;
import static intermediate.icodeimpl.ICodeKeyImpl.VALUE;
import static intermediate.icodeimpl.ICodeNodeTypeImpl.*;

public class CaseStatementParser extends StatementParser
{
   private static final EnumSet<PascalTokenType> CONSTANT_START_SET =
      EnumSet.of(IDENTIFIER, INTEGER, PLUS, MINUS, STRING);
   private static final EnumSet<PascalTokenType> OF_SET = CONSTANT_START_SET.clone();

   static
   {
      OF_SET.add(OF);
      OF_SET.addAll(StatementParser.STATEMENT_FOLLOW_SET);
   }

   private static final EnumSet<PascalTokenType> COMMA_SET = CONSTANT_START_SET.clone();

   static
   {
      COMMA_SET.add(COMMA);
      COMMA_SET.add(COLON);
      COMMA_SET.addAll(StatementParser.STATEMENT_START_SET);
      COMMA_SET.addAll(StatementParser.STATEMENT_FOLLOW_SET);
   }

   public CaseStatementParser(PascalParserTD parser)
   {
      super(parser);
   }

   public ICodeNode parse(Token token) throws Exception
   {
      token = nextToken();

      var selectNode = ICodeFactory.createICodeNode(SELECT);
      var expressionParser = new ExpressionParser(this);
      selectNode.addChild(expressionParser.parse(token));

      token = synchronize(OF_SET);
      if (token.getType() == OF) token = nextToken();
      else errorHandler.flag(token, MISSING_OF, this);

      var constantSet = new HashSet<Object>();
      while (!(token instanceof EofToken)
         && token.getType() != END)
      {
         selectNode.addChild(parseBranch(token, constantSet));
         token = currentToken();
         var tokenType = token.getType();
         if (tokenType == SEMICOLON) token = nextToken();
         else if (CONSTANT_START_SET.contains(tokenType)) errorHandler.flag(token, MISSING_SEMICOLON, this);
      }

      if (token.getType() == END) token = nextToken();
      else errorHandler.flag(token, MISSING_END, this);

      return selectNode;
   }

   private ICodeNode parseBranch(Token token, HashSet<Object> constantSet) throws Exception
   {
      var branchNode = ICodeFactory.createICodeNode(SELECT_BRANCH);
      var constantsNode = ICodeFactory.createICodeNode(SELECT_CONSTANTS);
      branchNode.addChild(constantsNode);

      parseConstantList(token, constantsNode, constantSet);

      token = currentToken();
      if (token.getType() == COLON) token = nextToken();
      else errorHandler.flag(token, MISSING_COLON, this);

      var statementParser = new StatementParser(this);
      branchNode.addChild(statementParser.parse(token));

      return branchNode;
   }

   private void parseConstantList(Token token, ICodeNode constantsNode, Set<Object> constantSet)
      throws Exception
   {
      while (CONSTANT_START_SET.contains(token.getType()))
      {
         constantsNode.addChild(parseConstant(token, constantSet));
         token = synchronize(COMMA_SET);
         if (token.getType() == COMMA) token = nextToken();
         else errorHandler.flag(token, MISSING_COMMA, this);
      }
   }

   private ICodeNode parseConstant(Token token, Set<Object> constantSet)
      throws Exception
   {
      var sign = (TokenType) null;

      token = synchronize(CONSTANT_START_SET);
      var tokenType = token.getType();
      if (tokenType == PLUS || tokenType == MINUS)
      {
         sign = tokenType;
         token = nextToken();
      }

      var constantNode = switch ((PascalTokenType) token.getType())
         {
            case IDENTIFIER -> parseIdentifierConstant(token, sign);
            case INTEGER -> parseIntegerConstant(token.getText(), sign);
            case STRING -> parseCharacterConstant(token, (String) token.getValue(), sign);
            default -> null;
         };
      if (constantNode == null)
      {
         errorHandler.flag(token, INVALID_CONSTANT, this);
      }
      else
      {
         var value = constantNode.getAttribute(VALUE);
         if (constantSet.contains(value)) errorHandler.flag(token, CASE_CONSTANT_REUSED, this);
         else constantSet.add(value);
      }
      nextToken();
      return constantNode;
   }

   private ICodeNode parseIdentifierConstant(Token token, TokenType sign) throws Exception
   {
      errorHandler.flag(token, INVALID_CONSTANT, this);
      return null;
   }

   private ICodeNode parseIntegerConstant(String value, TokenType sign)
   {
      var constantNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
      var intValue = Integer.parseInt(value);
      if (sign == MINUS) intValue = -intValue;

      constantNode.setAttribute(VALUE, intValue);
      return constantNode;
   }

   private ICodeNode parseCharacterConstant(Token token, String value, TokenType sign)
   {
      var constantNode = (ICodeNode) null;
      if (sign != null)
      {
         errorHandler.flag(token, INVALID_CONSTANT, this);
      }
      else
      {
         if (value.length() == 1)
         {
            constantNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
            constantNode.setAttribute(VALUE, value);
         }
         else
         {
            errorHandler.flag(token, INVALID_CONSTANT, this);
         }
      }

      return constantNode;
   }
}
