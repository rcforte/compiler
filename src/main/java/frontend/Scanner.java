package frontend;

import java.io.IOException;

public abstract class Scanner
{
    protected Token currentToken;
    protected final Source source;

    public Scanner(Source source)
    {
        if (source == null)
        {
            throw new IllegalArgumentException("source cannot be null");
        }

        this.source = source;
    }

    public char currentChar()
        throws IOException
    {
        return source.currentChar();
    }

    public char nextChar()
        throws IOException
    {
        return source.nextChar();
    }

    public Token currentToken()
    {
        return currentToken;
    }

    public Token nextToken()
        throws Exception
    {
        currentToken = extractToken();
        return currentToken;
    }

    public abstract Token extractToken()
        throws Exception;
}
