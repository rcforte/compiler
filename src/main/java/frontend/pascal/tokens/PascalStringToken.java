package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalToken;
import frontend.pascal.PascalTokenType;

import java.io.IOException;

import static frontend.Source.EOF;

public class PascalStringToken
   extends PascalToken
{
   public PascalStringToken(Source source)
      throws Exception
   {
      super(source);
   }

   @Override
   protected void extract()
      throws IOException
   {
      var current = nextChar();
      var textBuffer = new StringBuilder();
      var valueBuffer = new StringBuilder();

      textBuffer.append("'");

      while (current != '\'' && current != EOF)
      {
         if (Character.isWhitespace(current))
         {
            current = ' ';
         }

         textBuffer.append(current);
         valueBuffer.append(current);
         current = nextChar();

         if (current == '\'')
         {
            while (current == '\'' && peekChar() == '\'')
            {
               textBuffer.append("''");
               valueBuffer.append("'");
               current = nextChar();
               current = nextChar();
            }
         }
      }

      if (current == '\'')
      {
         nextChar();
         textBuffer.append("'");
         text = textBuffer.toString();
         value = valueBuffer.toString();
         type = PascalTokenType.STRING;
      }
      else
      {
         type = PascalTokenType.ERROR;
         value = PascalErrorCode.UNEXPECTED_EOF;
         text = textBuffer.toString();
      }
   }
}
