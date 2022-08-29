package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalToken;

import static frontend.pascal.PascalErrorCode.INVALID_CHARACTER;
import static frontend.pascal.PascalTokenType.ERROR;
import static frontend.pascal.PascalTokenType.SPECIAL_SYMBOLS;

public class PascalSpecialSymbolToken
   extends PascalToken
{
   public PascalSpecialSymbolToken(Source source)
      throws Exception
   {
      super(source);
   }

   @Override
   protected void extract()
      throws Exception
   {
      var current = currentChar();

      this.text = Character.toString(current);
      this.type = null;

      switch (current)
      {
         case '+':
         case '-':
         case '*':
         case '/':
         case ',':
         case ';':
         case '\'':
         case '=':
         case '(':
         case ')':
         case '[':
         case ']':
         case '{':
         case '}':
         case '^':
         {
            nextChar();
            break;
         }
         case ':':
         case '>':
         {
            current = nextChar();
            if (current == '=')
            {
               this.text += current;
               nextChar();
            }
            break;
         }
         case '<':
         {
            current = nextChar();
            if (current == '=' || current == '>')
            {
               this.text += current;
               nextChar();
            }
            break;
         }
         case '.':
         {
            current = nextChar();
            if (current == '.')
            {
               this.text += current;
               nextChar();
            }
            break;
         }
         default:
         {
            nextChar();
            this.type = ERROR;
            this.value = INVALID_CHARACTER;
         }
      }

      if (this.type == null)
      {
         this.type = SPECIAL_SYMBOLS.get(text);
      }
   }
}