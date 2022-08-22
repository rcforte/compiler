package frontend.pascal;

import frontend.EofToken;
import frontend.Parser;
import frontend.Scanner;
import message.Message;
import message.MessageType;

import java.io.IOException;

import static frontend.pascal.PascalTokenType.ERROR;
import static frontend.pascal.PascalTokenType.IDENTIFIER;

public class PascalParserTD extends Parser {
  protected final PascalErrorHandler errorHandler = new PascalErrorHandler();

  public PascalParserTD(Scanner scanner) {
    super(scanner);
  }

  @Override
  public void parse() throws Exception {
    try {
      var t0 = System.currentTimeMillis();
      var token = nextToken();
      while (!(token instanceof EofToken)) {
        var type = token.getType();
        if (type != ERROR) {
          if (type == IDENTIFIER) {
            var name = token.getText().toLowerCase();
            var entry = symTabStack.lookup(name);
            if (entry == null) {
              entry = symTabStack.enterLocal(name);
            }
            entry.appendLineNumber(token.getLineNumber());
          }
          // keep this for debugging.
          sendMessage(new Message(MessageType.TOKEN, new Object[]{
              token.getLineNumber(), token.getPosition(), token.getType(), token.getText(), token.getValue()
          }));
        } else {
          errorHandler.flag(token, (PascalErrorCode) token.getValue(), this);
        }
        token = nextToken();
      }
      var t1 = System.currentTimeMillis();
      var elapsed = (t1 - t0) / 1000f;
      sendMessage(new Message(MessageType.PARSER_SUMMARY, new Number[]{
        token.getLineNumber(), getErrorCount(), elapsed
      }));
    } catch (IOException e) {
      errorHandler.abortTranslation(PascalErrorCode.IO_ERROR, this);
    }
  }

  @Override
  public int getErrorCount() {
    return errorHandler.getErrorCount();
  }
}
