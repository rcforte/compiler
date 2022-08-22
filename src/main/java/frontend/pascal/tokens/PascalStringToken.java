package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalToken;
import frontend.pascal.PascalTokenType;

import java.io.IOException;

import static frontend.Source.EOF;

public class PascalStringToken extends PascalToken {
  public PascalStringToken(Source source) throws Exception {
    super(source);
  }

  @Override
  protected void extract() throws IOException {
    var curr = nextChar();
    var textBuffer = new StringBuilder();
    textBuffer.append("'");
    var valueBuffer = new StringBuilder();
    while (curr != '\'' && curr != EOF) {
      if (Character.isWhitespace(curr)) {
        curr = ' ';
      }
      textBuffer.append(curr);
      valueBuffer.append(curr);
      curr = nextChar();
      if (curr == '\'') {
        while (curr == '\'' && peekChar() == '\'') {
          textBuffer.append("''");
          valueBuffer.append("'");
          curr = nextChar();
          curr = nextChar();
        }
      }
    }
    if (curr == '\'') {
      nextChar();
      textBuffer.append("'");
      text = textBuffer.toString();
      value = valueBuffer.toString();
      type = PascalTokenType.STRING;
    } else {
      type = PascalTokenType.ERROR;
      value = PascalErrorCode.UNEXPECTED_EOF;
      text = textBuffer.toString();
    }
  }
}
