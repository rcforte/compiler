package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalToken;

import static frontend.pascal.PascalErrorCode.RANGE_REAL;
import static frontend.pascal.PascalTokenType.*;

public class PascalNumberToken extends PascalToken {
  public PascalNumberToken(Source source) throws Exception {
    super(source);
  }

  @Override
  public void extract() throws Exception {
    type = INTEGER;
    var textBuffer = new StringBuilder();
    extractNumber(textBuffer);
    text = textBuffer.toString();
  }

  private void extractNumber(StringBuilder textBuffer) throws Exception {
    var whole = unsignedIntegerDigits(textBuffer);
    if (type == ERROR) {
      return;
    }
    var curr = currentChar();
    var sawDD = false;
    var fractional = "";
    if (curr == '.') {
      if (peekChar() == '.') {
        sawDD = true;
      } else {
        type = REAL;
        textBuffer.append(curr);
        curr = nextChar();
        fractional = unsignedIntegerDigits(textBuffer);
        if (type == ERROR) {
          return;
        }
      }
    }
    curr = currentChar();
    var sign = '+';
    var exponent = "";
    if (!sawDD && (curr == 'e' || curr == 'E')) {
      type = REAL;
      textBuffer.append(curr);
      curr = nextChar();
      if (curr == '+' || curr == '-') {
        textBuffer.append(curr);
        sign = curr;
        curr = nextChar();
      }
      exponent = unsignedIntegerDigits(textBuffer);
      if (type == ERROR) {
        return;
      }
    }
    if (type == INTEGER) {
      var num = computeInt(whole);
      if (type != ERROR) {
        value = num;
      }
    } else if (type == REAL) {
      var num = computeReal(whole, fractional, exponent, sign);
      if (type != ERROR) {
        value = num;
      }
    }
  }

  private float computeReal(String whole, String fractional, String exponent, char esign) {
    var exponentValue = computeInt(exponent);
    if (esign == '-') {
      exponentValue = -exponentValue;
    }
    var digits = whole;
    if (fractional != null) {
      digits += fractional;
      exponentValue -= fractional.length();
    }
    if (Math.abs(exponentValue + whole.length()) > 100) {
      type = ERROR;
      value = RANGE_REAL;
      return 0.0f;
    }
    var index = 0;
    float res = 0.0f;
    while (index < digits.length()) {
      res = res * 10 + Character.getNumericValue(digits.charAt(index));
      index++;
    }
    if (exponentValue != 0) {
      res *= Math.pow(10, exponentValue);
    }
    return res;
  }

  private int computeInt(String digits) {
    if (digits == null) {
      return 0;
    }
    var res = 0;
    var prev = -1;
    var index = 0;
    while (index < digits.length() && res >= prev) {
      prev = res;
      res = res * 10 + Character.getNumericValue(digits.charAt(index));
      index++;
    }
    if (res < prev) { // overflow
      type = ERROR;
      value = PascalErrorCode.RANGE_INTEGER;
      return 0;
    }
    return res;
  }

  private String unsignedIntegerDigits(StringBuilder buffer) throws Exception {
    var curr = currentChar();
    if (!Character.isDigit(curr)) {
      type = ERROR;
      value = PascalErrorCode.INVALID_NUMBER;
      return null;
    }
    var digits = new StringBuilder();
    while (Character.isDigit(curr)) {
      digits.append(curr);
      buffer.append(curr);
      curr = nextChar();
    }
    return digits.toString();
  }
}
