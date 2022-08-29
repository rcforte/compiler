package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalToken;

import static frontend.pascal.PascalErrorCode.RANGE_REAL;
import static frontend.pascal.PascalTokenType.*;

public class PascalNumberToken extends PascalToken
{
   public PascalNumberToken(Source source) throws Exception
   {
      super(source);
   }

   @Override
   public void extract() throws Exception
   {
      this.type = INTEGER;
      var textBuffer = new StringBuilder();
      extractNumber(textBuffer);
      this.text = textBuffer.toString();
   }

   private void extractNumber(StringBuilder textBuffer) throws Exception
   {
      // Extract the integer part of the number.
      var whole = unsignedIntegerDigits(textBuffer);
      if (type == ERROR) return;

      var current = currentChar();
      var sawDotDot = false;
      var fractional = "";

      // Check if this is a real number.
      if (current == '.')
      {
         // Check if this is a dot dot operator.
         if (peekChar() == '.')
         {
            sawDotDot = true;
         }
         else
         {
            type = REAL;
            textBuffer.append(current);
            nextChar();

            // Extract the fractional part of the number.
            fractional = unsignedIntegerDigits(textBuffer);
            if (type == ERROR) return;
         }
      }

      current = currentChar();
      var sign = '+';
      var exponent = "";

      // Check if it has an exponent.
      if (!sawDotDot && (current == 'e' || current == 'E'))
      {
         type = REAL;
         textBuffer.append(current);
         current = nextChar();

         // Check if it has an exponent sign.
         if (current == '+' || current == '-')
         {
            textBuffer.append(current);
            sign = current;
            current = nextChar();
         }

         // Extract the exponent part of the number.
         exponent = unsignedIntegerDigits(textBuffer);
         if (type == ERROR) return;
      }

      // Convert the extracted integer string to an int.
      if (type == INTEGER)
      {
         var num = computeInt(whole);
         if (type != ERROR) value = num;
      }
      else if (type == REAL)
      {
         // Convert the extracted real string into a float.
         var num = computeReal(whole, fractional, exponent, sign);
         if (type != ERROR) value = num;
      }
   }

   private float computeReal(String whole, String fractional, String exponent, char sign)
   {
      var exponentValue = computeInt(exponent);
      if (sign == '-') exponentValue = -exponentValue;

      var digits = whole;
      if (fractional != null)
      {
         digits += fractional;
         exponentValue -= fractional.length();
      }

      if (Math.abs(exponentValue + whole.length()) > 100)
      {
         type = ERROR;
         value = RANGE_REAL;
         return 0.0f;
      }

      var result = (float) 0.0f;
      var index = 0;
      while (index < digits.length())
         result = result * 10 + Character.getNumericValue(digits.charAt(index++));

      if (exponentValue != 0)
         result *= Math.pow(10, exponentValue);
      return result;
   }

   private int computeInt(String digits)
   {
      if (digits == null) return 0;

      var result = 0;
      var previous = -1;
      var index = 0;

      while (index < digits.length() && result >= previous)
      {
         previous = result;
         result = result * 10 + Character.getNumericValue(digits.charAt(index++));
      }

      // Checks for overflow.
      if (result < previous)
      {
         type = ERROR;
         value = PascalErrorCode.RANGE_INTEGER;
         return 0;
      }

      return result;
   }

   private String unsignedIntegerDigits(StringBuilder buffer) throws Exception
   {
      var current = currentChar();
      if (!Character.isDigit(current))
      {
         type = ERROR;
         value = PascalErrorCode.INVALID_NUMBER;
         return null;
      }

      var digits = new StringBuilder();
      while (Character.isDigit(current))
      {
         digits.append(current);
         buffer.append(current);
         current = nextChar();
      }
      return digits.toString();
   }
}
