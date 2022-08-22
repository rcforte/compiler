package frontend.pascal;

import frontend.Parser;
import frontend.Token;
import message.Message;
import message.MessageType;

public class PascalErrorHandler {
  private static final int MAX_ERRORS = 25;
  private int errorCount;

  public void flag(Token token, PascalErrorCode error, Parser parser) {
    parser.sendMessage(new Message(MessageType.SYNTAX_ERROR,
      new Object[] {
        token.getLineNumber(),
        token.getPosition(),
        token.getText(),
        error.toString()
      }));
    errorCount++;
    if (errorCount >= MAX_ERRORS) {
      abortTranslation(PascalErrorCode.TOO_MANY_ERRORS, parser);
    }
  }

  public void abortTranslation(PascalErrorCode error, Parser parser) {
    var msg = "FATAL ERROR: " + error.toString();
    parser.sendMessage(new Message(MessageType.SYNTAX_ERROR, new Object[] {
      0, 0, "", msg
    }));
    System.exit(error.getStatus());
  }

  public int getErrorCount() {
    return errorCount;
  }
}
