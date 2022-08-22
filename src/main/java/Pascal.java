import backend.Backend;
import backend.BackendFactory;
import frontend.FrontendFactory;
import frontend.Parser;
import frontend.Source;
import frontend.pascal.PascalTokenType;
import intermediate.ICode;
import intermediate.SymTab;
import intermediate.SymTabStack;
import message.Message;
import message.MessageListener;
import message.MessageType;
import util.CrossReferencer;

import java.io.BufferedReader;
import java.io.FileReader;

public class Pascal {
  private static final String FLAGS = "[-ix]";
  private static final String USAGE = "Usage: Pascal execute|compile " + FLAGS + " <source file path>";
  private Source source;
  private Parser parser;
  private Backend backend;
  private ICode icode;
  private SymTabStack symTabStack;

  public Pascal(String op, String filePath, String flags) {
    try {
      System.out.println("Command line flags: " + flags);
      var intermediate = flags.indexOf("i") >= 0;
      var xref = flags.indexOf("x") >= 0;
      var listener = new PascalMessageListener();
      source = new Source(new BufferedReader(new FileReader(filePath)));
      source.addMessageListener(listener);
      parser = FrontendFactory.createParser(source, "pascal", "top-down");
      parser.addMessageListener(listener);
      backend = BackendFactory.createBackend(op);
      backend.addMessageListener(listener);
      parser.parse();
      source.close();
      icode = parser.getICode();
      symTabStack = parser.getSymTabStack();
      if (xref) {
        var crossReferencer = new CrossReferencer();
        crossReferencer.print(symTabStack);
      }
      backend.process(icode, symTabStack);
    } catch (Exception e) {
      System.err.println("Internal translation error");
      e.printStackTrace();
    }
  }

  public static void main(String... args) {
    try {
      var op = args[0];
      if (!op.equalsIgnoreCase("compile") && !op.equalsIgnoreCase("execute")) {
        throw new Exception();
      }
      var i = 0;
      var flags = new StringBuilder();
      while ((++i < args.length) && (args[i].charAt(0) == '-')) {
        flags.append(args[i].substring(1));
      }
      if (i < args.length) {
        var path = args[i];
        new Pascal(op, path, flags.toString());
      } else {
        throw new Exception();
      }
    } catch (Exception e) {
      System.out.println(USAGE);
    }
  }
}

class PascalMessageListener implements MessageListener {
  private static final String SOURCE_LINE_FORMAT = "%03d %s%n";
  private static final String PARSER_SUMMARY_FORMAT = "%n%,20d source lines.%n%,20d syntax error.%n%,20.2f seconds total parsing time.%n";
  private static final String INTERPRETER_SUMMARY_FORMAT = "%n%,20d statements executed.%n%,20d runtime errors.%n%,20.2f seconds total execution time%n.";
  private static final String COMPILER_SUMMARY_FORMAT = "%n%,20d instructions generated.%n%,20.2f seconds total code generation time.%n";
  private static final String TOKEN_FORMAT = ">>> %-15s line=%03d, pos=%2d, text=\"%s\"%n";
  private static final String VALUE_FORMAT = ">>>       value=%s%n";
  private static final int PREFIX_WIDTH = 4;

  @Override
  public void messageReceived(Message message) {
    var type = message.messageType();
    if (type == MessageType.SOURCE_LINE) {
      var body = (Object[]) message.getBody();
      var line = (Integer) body[0];
      var text = (String) body[1];
      System.out.printf(SOURCE_LINE_FORMAT, line, text);
    } else if (type == MessageType.PARSER_SUMMARY) {
      var body = (Number[]) message.getBody();
      var sourceLines = (Integer) body[0];
      var syntaxErrors = (Integer) body[1];
      float elapsedTime = (Float) body[2];
      System.out.printf(PARSER_SUMMARY_FORMAT, sourceLines, syntaxErrors, elapsedTime);
    } else if (type == MessageType.INTERPRETER_SUMMARY) {
      var body = (Number[]) message.getBody();
      var statementsExecuted = (Integer) body[0];
      var syntaxErrors = (Integer) body[1];
      float elapsedTime = (Float) body[2];
      System.out.printf(INTERPRETER_SUMMARY_FORMAT, statementsExecuted, syntaxErrors, elapsedTime);
    } else if (type == MessageType.COMPILER_SUMMARY) {
      var body = (Number[]) message.getBody();
      var instructionsGenerated = (Integer) body[0];
      float elapsedTime = (Float) body[1];
      System.out.printf(COMPILER_SUMMARY_FORMAT, instructionsGenerated, elapsedTime);
    } else if (type == MessageType.TOKEN) {
      var body = (Object[]) message.getBody();
      var lineNumber = (Integer) body[0];
      var position = (Integer) body[1];
      var tokenType = body[2];
      var tokenText = body[3];
      var tokenValue = body[4];
      System.out.printf(TOKEN_FORMAT, tokenType, lineNumber, position, tokenText);
      if (tokenValue != null) {
        if (tokenType == PascalTokenType.STRING) {
          tokenValue = "\"" + tokenValue + "\"";
        }
        System.out.printf(VALUE_FORMAT, tokenValue);
      }
    } else if (type == MessageType.SYNTAX_ERROR) {
      var body = (Object[]) message.getBody();
      var lineNumber = (Integer) body[0];
      var position = (Integer) body[1];
      var tokenText = body[2];
      var errorMessage = body[3];
      var spaceCount = PREFIX_WIDTH + position;
      var flagBuffer = new StringBuilder();
      for (int i = 0; i < spaceCount; i++) {
        flagBuffer.append(" ");
      }
      flagBuffer.append("^\n***");
      flagBuffer.append(errorMessage);
      if (tokenText != null) {
        flagBuffer.append("[at \"");
        flagBuffer.append(tokenText);
        flagBuffer.append("\"]");
      }
      System.out.println(flagBuffer.toString());
    } else {
      System.out.println("Panic!!!");
    }
  }
}



