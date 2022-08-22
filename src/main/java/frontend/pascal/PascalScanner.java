package frontend.pascal;

import frontend.EofToken;
import frontend.Scanner;
import frontend.Source;
import frontend.Token;
import frontend.pascal.tokens.*;

import static frontend.Source.EOF;
import static frontend.pascal.PascalErrorCode.INVALID_CHARACTER;
import static frontend.pascal.PascalTokenType.SPECIAL_SYMBOLS;

public class PascalScanner extends Scanner {
  public PascalScanner(Source source) {
    super(source);
  }

  @Override
  public Token extractToken() throws Exception {
    skipWhitespace();
    char cur = currentChar();
    if (cur == EOF) {
      return new EofToken(source);
    } else if (Character.isLetter(cur)) {
      return new PascalWordToken(source);
    } else if (Character.isDigit(cur)) {
      return new PascalNumberToken(source);
    } else if (cur == '\'') {
      return new PascalStringToken(source);
    } else if (SPECIAL_SYMBOLS.containsKey(Character.toString(cur))) {
      return new PascalSpecialSymbolToken(source);
    } else {
      var res = new PascalErrorToken(source, INVALID_CHARACTER, Character.toString(cur));
      nextChar();
      return res;
    }
  }

  private void skipWhitespace() throws Exception {
    char cur = currentChar();
    while (Character.isWhitespace(cur) || cur == '{') {
      if (cur == '{') {
        cur = nextChar();
        while (cur != '}' && cur != EOF) {
          cur = nextChar();
        }
        if (cur == '}') {
          cur = nextChar();
        }
      } else {
        cur = nextChar();
      }
    }
  }
}
