package frontend.pascal;

import frontend.Parser;
import frontend.Token;
import message.Message;

import static frontend.pascal.PascalErrorCode.TOO_MANY_ERRORS;
import static message.MessageType.SYNTAX_ERROR;

public class PascalErrorHandler {
    private static final int MAX_ERRORS = 7;
    private int errorCount;

    public void flag(Token token, PascalErrorCode error, Parser parser) {
        parser.sendMessage(new Message(SYNTAX_ERROR,
                new Object[]{token.getLineNumber(), token.getPosition(), token.getText(), error.toString()}));
        if (++errorCount >= MAX_ERRORS) {
            abortTranslation(TOO_MANY_ERRORS, parser);
        }
    }

    public void abortTranslation(PascalErrorCode error, Parser parser) {
        var message = "FATAL ERROR: " + error.toString();
        parser.sendMessage(new Message(SYNTAX_ERROR, new Object[]{0, 0, "", message}));
        System.exit(error.getStatus());
    }

    public int getErrorCount() {
        return errorCount;
    }
}
