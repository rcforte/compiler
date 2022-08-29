package frontend.pascal;

import frontend.EofToken;
import frontend.Scanner;
import frontend.Source;
import frontend.Token;
import frontend.pascal.tokens.*;

import static frontend.Source.EOF;
import static frontend.pascal.PascalErrorCode.INVALID_CHARACTER;
import static frontend.pascal.PascalTokenType.SPECIAL_SYMBOLS;

public class PascalScanner
   extends Scanner
{
   public PascalScanner(Source source)
   {
      super(source);
   }

   @Override
   public Token extractToken()
      throws Exception
   {
      skipWhitespace();

      var current = currentChar();
      if (current == EOF)
      {
         return new EofToken(source);
      }
      else if (Character.isLetter(current))
      {
         return new PascalWordToken(source);
      }
      else if (Character.isDigit(current))
      {
         return new PascalNumberToken(source);
      }
      else if (current == '\'')
      {
         return new PascalStringToken(source);
      }
      else if (SPECIAL_SYMBOLS.containsKey(Character.toString(current)))
      {
         return new PascalSpecialSymbolToken(source);
      }
      else
      {
         var result = new PascalErrorToken(source, INVALID_CHARACTER, Character.toString(current));
         nextChar();
         return result;
      }
   }

   private void skipWhitespace()
      throws Exception
   {
      var current = currentChar();
      while (Character.isWhitespace(current) || current == '{')
      {
         if (current == '{')
         {
            current = nextChar();
            while (current != '}' && current != EOF)
            {
               current = nextChar();
            }
            if (current == '}')
            {
               current = nextChar();
            }
         }
         else
         {
            current = nextChar();
         }
      }
   }
}
