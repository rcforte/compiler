package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalToken;
import frontend.pascal.PascalTokenType;

import static frontend.pascal.PascalErrorCode.INVALID_CHARACTER;
import static frontend.pascal.PascalTokenType.*;

public class PascalSpecialSymbolToken extends PascalToken {
  public PascalSpecialSymbolToken(Source source) throws Exception {
    super(source);
  }

  @Override
  protected void extract() throws Exception {
    char curr = currentChar();
    text = Character.toString(curr);
    type = null;
    switch (curr) {
      case '+': case '-': case '*': case '/': case ',': case ';': case '\'': case '=': case '(':
      case ')': case '[': case ']': case '{': case '}': case '^': {
        nextChar();
        break;
      }
      case ':': case '>': {
        curr = nextChar();
        if (curr == '=') {
          text += curr;
          nextChar();
        }
        break;
      }
      case '<': {
        curr = nextChar();
        if (curr == '=' || curr == '>') {
          text += curr;
          nextChar();
        }
        break;
      }
      case '.': {
        curr = nextChar();
        if (curr == '.') {
          text += curr;
          nextChar();
        }
        break;
      }
      default: {
        nextChar();
        type = ERROR;
        value = INVALID_CHARACTER;
      }
    }
    if (type == null) {
      type = SPECIAL_SYMBOLS.get(text);
    }
  }
}