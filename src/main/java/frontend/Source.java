package frontend;

import message.*;

import java.io.BufferedReader;
import java.io.IOException;

public class Source implements MessageProducer
{
    public static final char EOL = '\n';
    public static final char EOF = (char) 0;

    private BufferedReader reader;
    private String line;
    private int lineNumber;
    private int position;
    private final MessageHandler messageHandler = new MessageHandler();

    public Source(BufferedReader reader)
        throws IOException
    {
        this.reader = reader;
        this.position = -2;
        this.lineNumber = 0;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public int getPosition()
    {
        return position;
    }

    public char currentChar()
        throws IOException
    {
        if (position == -2)
        {
            readLine();
            return nextChar();
        }
        else if (line == null)
        {
            return EOF;
        }
        else if (position == -1 || position == line.length())
        {
            return EOL;
        }
        else if (position > line.length())
        {
            readLine();
            return nextChar();
        }
        else
        {
            return line.charAt(position);
        }
    }

    public char nextChar()
        throws IOException
    {
        ++position;
        return currentChar();
    }

    public char peekChar()
        throws IOException
    {
        currentChar();
        if (line == null)
        {
            return EOF;
        }
        var nextPosition = position + 1;
        return nextPosition < line.length()
            ? line.charAt(nextPosition)
            : EOL;
    }

    private void readLine()
        throws IOException
    {
        position = -1;
        line = reader.readLine();
        if (line != null)
        {
            ++lineNumber;
        }
        if (line != null)
        {
            sendMessage(new Message(MessageType.SOURCE_LINE,
                new Object[]{lineNumber, line}));
        }
    }

    public void close()
        throws IOException
    {
        if (reader != null)
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void addMessageListener(MessageListener listener)
    {
        messageHandler.addMessageListener(listener);
    }

    public void removeMessageListener(MessageListener listener)
    {
        messageHandler.removeMessageListener(listener);
    }

    public void sendMessage(Message message)
    {
        messageHandler.sendMessage(message);
    }
}
