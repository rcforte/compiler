package frontend;

import message.*;

import java.io.BufferedReader;
import java.io.IOException;

public class Source implements MessageProducer {
  public static final char EOL = '\n';
  public static final char EOF = (char) 0;
  private BufferedReader reader;
  private String line;
  private int lineNum;
  private int currentPos;
  private final MessageHandler messageHandler = new MessageHandler();

  public Source(BufferedReader reader) throws IOException {
    this.reader = reader;
    this.currentPos = -2;
    this.lineNum = 0;
  }

  public int lineNum() {
    return lineNum;
  }

  public int position() {
    return currentPos;
  }

  public char currentChar() throws IOException {
    if (currentPos == -2) {
      readLine();
      return nextChar();
    } else if (line == null) {
      return EOF;
    } else if (currentPos == -1 || currentPos == line.length()) {
      return EOL;
    } else if (currentPos > line.length()) {
      readLine();
      return nextChar();
    } else {
      return line.charAt(currentPos);
    }
  }

  public char nextChar() throws IOException {
    ++currentPos;
    return currentChar();
  }

  public char peekChar() throws IOException {
    currentChar();
    if (line == null) {
      return EOF;
    }
    int nextPos = currentPos + 1;
    return nextPos < line.length() ? line.charAt(nextPos) : EOL;
  }

  private void readLine() throws IOException {
    currentPos = -1;
    line = reader.readLine();
    if (line != null) {
      ++lineNum;
    }
    if (line != null) {
      sendMessage(new Message(MessageType.SOURCE_LINE, new Object[] {
        lineNum, line
      }));
    }
  }

  public void close() throws IOException {
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  public void addMessageListener(MessageListener l) {
    messageHandler.addMessageListener(l);
  }

  public void removeMessageListener(MessageListener l) {
    messageHandler.removeMessageListener(l);
  }

  public void sendMessage(Message m) {
    messageHandler.sendMessage(m);
  }
}
