package frontend;

import java.io.IOException;

public class Token
{
    protected TokenType type;
    protected String text;
    protected Object value;
    protected Source source;
    protected int lineNumber;
    protected int position;

    public Token(Source source)
        throws Exception
    {
        if (source == null)
        {
            throw new IllegalArgumentException("source cannot be null");
        }

        this.source = source;
        this.lineNumber = source.getLineNumber();
        this.position = source.getPosition();

        extract();
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public int getPosition()
    {
        return position;
    }

    public TokenType getType()
    {
        return type;
    }

    public String getText()
    {
        return text;
    }

    public Object getValue()
    {
        return value;
    }

    protected void extract()
        throws Exception
    {
        this.text = Character.toString(currentChar());
        this.value = null;

        nextChar();
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

    public char peekChar()
        throws IOException
    {
        return source.peekChar();
    }

    @Override
    public String toString()
    {
        return "text=" + text + ", value=" + value;
    }
}
