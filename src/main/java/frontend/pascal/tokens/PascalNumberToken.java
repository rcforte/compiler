package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalToken;

import static frontend.pascal.PascalErrorCode.RANGE_REAL;
import static frontend.pascal.PascalTokenType.*;

public class PascalNumberToken
    extends PascalToken
{
    public PascalNumberToken(Source source)
        throws Exception
    {
        super(source);
    }

    @Override
    public void extract()
        throws Exception
    {
        this.type = INTEGER;

        var textBuffer = new StringBuilder();
        extractNumber(textBuffer);

        this.text = textBuffer.toString();
    }

    private void extractNumber(StringBuilder textBuffer)
        throws Exception
    {
        var whole = unsignedIntegerDigits(textBuffer);

        if (type == ERROR)
        {
            return;
        }

        var current = currentChar();
        var sawDotDot = false;
        var fractional = "";

        if (current == '.')
        {
            if (peekChar() == '.')
            {
                sawDotDot = true;
            }
            else
            {
                type = REAL;
                textBuffer.append(current);
                current = nextChar();
                fractional = unsignedIntegerDigits(textBuffer);

                if (type == ERROR)
                {
                    return;
                }
            }
        }
        current = currentChar();

        var sign = '+';
        var exponent = "";
        if (!sawDotDot && (current == 'e' || current == 'E'))
        {
            type = REAL;
            textBuffer.append(current);
            current = nextChar();
            if (current == '+' || current == '-')
            {
                textBuffer.append(current);
                sign = current;
                current = nextChar();
            }
            exponent = unsignedIntegerDigits(textBuffer);
            if (type == ERROR)
            {
                return;
            }
        }

        if (type == INTEGER)
        {
            var num = computeInt(whole);
            if (type != ERROR)
            {
                value = num;
            }
        }
        else if (type == REAL)
        {
            var num = computeReal(whole, fractional, exponent, sign);
            if (type != ERROR)
            {
                value = num;
            }
        }
    }

    private float computeReal(String whole, String fractional, String exponent, char esign)
    {
        var exponentValue = computeInt(exponent);
        if (esign == '-')
        {
            exponentValue = -exponentValue;
        }

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

        float res = 0.0f;

        var index = 0;
        while (index < digits.length())
        {
            res = res * 10 + Character.getNumericValue(digits.charAt(index));
            index++;
        }

        if (exponentValue != 0)
        {
            res *= Math.pow(10, exponentValue);
        }

        return res;
    }

    private int computeInt(String digits)
    {
        if (digits == null)
        {
            return 0;
        }

        var result = 0;
        var previous = -1;
        var index = 0;

        while (index < digits.length() && result >= previous)
        {
            previous = result;
            result = result * 10 + Character.getNumericValue(digits.charAt(index));
            index++;
        }

        if (result < previous)
        {
            // overflow
            type = ERROR;
            value = PascalErrorCode.RANGE_INTEGER;
            return 0;
        }

        return result;
    }

    private String unsignedIntegerDigits(StringBuilder buffer)
        throws Exception
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
