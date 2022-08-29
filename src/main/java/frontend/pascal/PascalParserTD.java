package frontend.pascal;

import frontend.EofToken;
import frontend.Parser;
import frontend.Scanner;
import frontend.Token;
import frontend.pascal.parsers.StatementParser;
import intermediate.ICodeFactory;
import intermediate.ICodeNode;
import message.Message;
import message.MessageType;

import java.io.IOException;
import java.util.EnumSet;

import static frontend.pascal.PascalErrorCode.MISSING_PERIOD;
import static frontend.pascal.PascalErrorCode.UNEXPECTED_TOKEN;
import static frontend.pascal.PascalTokenType.BEGIN;
import static frontend.pascal.PascalTokenType.DOT;

public class PascalParserTD extends Parser
{
   protected static final PascalErrorHandler errorHandler = new PascalErrorHandler();

   public PascalParserTD(Scanner scanner)
   {
      super(scanner);
   }

   public PascalParserTD(PascalParserTD parent)
   {
      super(parent.getScanner());
   }

   @Override
   public void parse()
      throws Exception
   {
      try
      {
         var t0 = System.currentTimeMillis();

         iCode = ICodeFactory.createICode();
         var token = nextToken();
         var rootNode = (ICodeNode) null;

         var type = token.getType();
         if (type == BEGIN)
         {
            var statementParser = new StatementParser(this);
            rootNode = statementParser.parse(token);
            token = currentToken();
         }
         else
         {
            errorHandler.flag(token, UNEXPECTED_TOKEN, this);
         }

         if (token.getType() != DOT)
            errorHandler.flag(token, MISSING_PERIOD, this);
         token = currentToken();

         if (rootNode != null)
            iCode.setRoot(rootNode);

         var elapsed = (System.currentTimeMillis() - t0) / 1000f;
         sendMessage(new Message(MessageType.PARSER_SUMMARY,
            new Number[]{token.getLineNumber(), getErrorCount(), elapsed}));
      }
      catch (IOException e)
      {
         errorHandler.abortTranslation(PascalErrorCode.IO_ERROR, this);
      }
   }

   public Token synchronize(EnumSet syncSet)
      throws Exception
   {
      var token = currentToken();
      if (!syncSet.contains(token.getType()))
      {
         errorHandler.flag(token, UNEXPECTED_TOKEN, this);
         do
         {
            token = nextToken();
         }
         while (!(token instanceof EofToken)
            && !syncSet.contains(token.getType()));
      }
      return token;
   }

   @Override
   public int getErrorCount()
   {
      return errorHandler.getErrorCount();
   }
}
