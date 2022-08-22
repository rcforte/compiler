package frontend.pascal.tokens;

import frontend.Source;
import frontend.pascal.PascalToken;
import frontend.pascal.PascalTokenType;

import static frontend.pascal.PascalTokenType.IDENTIFIER;
import static frontend.pascal.PascalTokenType.RESERVED_WORDS;

public class PascalWordToken
        extends PascalToken {
    public PascalWordToken(Source source)
            throws Exception {
        super(source);
    }

    @Override
    public void extract()
            throws Exception {
        var buffer = new StringBuilder();
        var current = currentChar();

        while (Character.isLetterOrDigit(current)) {
            buffer.append(current);
            current = nextChar();
        }

        this.text = buffer.toString();
        this.type = (RESERVED_WORDS.contains(text)
                ? PascalTokenType.valueOf(text)
                : IDENTIFIER);
    }
}
