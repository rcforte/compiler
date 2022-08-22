package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalToken;
import frontend.pascal.PascalTokenType;

import static frontend.pascal.PascalTokenType.IDENTIFIER;

public class PascalWordToken extends PascalToken {
  public PascalWordToken(Source source) throws Exception {
    super(source);
  }

  @Override
  public void extract() throws Exception {
    var buff = new StringBuilder();
    var curr = currentChar();
    while (Character.isLetterOrDigit(curr)) {
      buff.append(curr);
      curr = nextChar();
    }
    text = buff.toString();
    type = (PascalTokenType.RESERVED_WORDS.contains(text)
        ? PascalTokenType.valueOf(text)
        : IDENTIFIER);
  }
}
