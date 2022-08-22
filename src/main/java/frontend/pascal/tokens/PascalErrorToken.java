package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalErrorCode;
import frontend.pascal.PascalToken;
import frontend.pascal.PascalTokenType;

public class PascalErrorToken extends PascalToken {
  public PascalErrorToken(Source source, PascalErrorCode value, String text) throws Exception {
    super(source);
    this.text = text;
    this.value = value;
    this.type = PascalTokenType.ERROR;
  }
}
