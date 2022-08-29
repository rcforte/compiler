import backend.Backend;
import backend.BackendFactory;
import frontend.FrontendFactory;
import frontend.Parser;
import frontend.Source;
import frontend.pascal.PascalTokenType;
import intermediate.ICode;
import intermediate.SymTabStack;
import message.Message;
import message.MessageListener;
import message.MessageType;
import util.CrossReferencer;
import util.ParseTreePrinter;

import java.io.BufferedReader;
import java.io.FileReader;

public class Pascal
{
   private static final String FLAGS = "[-ix]";
   private static final String USAGE = "Usage: Pascal execute|compile " + FLAGS + " <source file path>";
   private Source source;
   private Parser parser;
   private Backend backend;
   private ICode iCode;
   private SymTabStack symTabStack;

   public Pascal(String operation, String filePath, String flags)
   {
      if (operation == null) throw new IllegalArgumentException("operation cannot be null");
      if (filePath == null) throw new IllegalArgumentException("filePath cannot be null");
      if (flags == null) throw new IllegalArgumentException("flags cannot be null");

      try
      {
         var intermediate = flags.contains("i");
         var xref = flags.contains("x");
         var listener = new PascalMessageListener();

         source = new Source(new BufferedReader(new FileReader(filePath)));
         source.addMessageListener(listener);

         parser = FrontendFactory.createParser(source, "pascal", "top-down");
         parser.addMessageListener(listener);

         backend = BackendFactory.createBackend(operation);
         backend.addMessageListener(listener);

         parser.parse();
         source.close();

         if (parser.getErrorCount() == 0)
         {
            iCode = parser.getICode();
            symTabStack = parser.getSymTabStack();
            if (xref) new CrossReferencer().print(symTabStack);
            if (intermediate) new ParseTreePrinter(System.out).print(iCode);
            backend.process(iCode, symTabStack);
         }
      }
      catch (Exception e)
      {
         System.err.println("Internal translation error");
         e.printStackTrace();
      }
   }

   public static void main(String... args)
   {
      try
      {
         var operation = args[0];
         if (!operation.equalsIgnoreCase("compile")
            && !operation.equalsIgnoreCase("execute"))
         {
            throw new Exception();
         }

         var argsIndex = 0;
         var flagsBuffer = new StringBuilder();
         while (++argsIndex < args.length && args[argsIndex].charAt(0) == '-')
            flagsBuffer.append(args[argsIndex].substring(1));

         if (argsIndex < args.length)
         {
            var path = args[argsIndex];
            var flags = flagsBuffer.toString();
            new Pascal(operation, path, flags);
         }
         else
         {
            throw new Exception();
         }
      }
      catch (Exception e)
      {
         System.out.println(USAGE);
      }
   }
}

class PascalMessageListener implements MessageListener
{
   private static final String SOURCE_LINE_FORMAT = "%03d %s%n";
   private static final String PARSER_SUMMARY_FORMAT = "%n%,20d source lines.%n%,20d syntax error.%n%,20.2f seconds total parsing time.%n";
   private static final String INTERPRETER_SUMMARY_FORMAT = "%n%,20d statements executed.%n%,20d runtime errors.%n%,20.2f seconds total execution time%n.";
   private static final String COMPILER_SUMMARY_FORMAT = "%n%,20d instructions generated.%n%,20.2f seconds total code generation time.%n";
   private static final String ASSIGN_FORMAT = ">>> LINE %03d: %s = %s\n";
   private static final String TOKEN_FORMAT = ">>> %-15s line=%03d, pos=%2d, text=\"%s\"%n";
   private static final String VALUE_FORMAT = ">>>       value=%s%n";
   private static final int PREFIX_WIDTH = 4;

   private boolean firstOutputMessage = true;

   @Override
   public void messageReceived(Message message)
   {
      var type = message.messageType();
      if (type == MessageType.SOURCE_LINE)
      {
         var messageBody = (Object[]) message.getBody();
         var lineNumber = (Integer) messageBody[0];
         var lineText = (String) messageBody[1];
         System.out.printf(SOURCE_LINE_FORMAT, lineNumber, lineText);
      }
      else if (type == MessageType.PARSER_SUMMARY)
      {
         var body = (Number[]) message.getBody();
         var sourceLines = (Integer) body[0];
         var syntaxErrors = (Integer) body[1];
         float elapsedTime = (Float) body[2];
         System.out.printf(PARSER_SUMMARY_FORMAT, sourceLines, syntaxErrors, elapsedTime);
      }
      else if (type == MessageType.INTERPRETER_SUMMARY)
      {
         var body = (Number[]) message.getBody();
         var statementsExecuted = (Integer) body[0];
         var syntaxErrors = (Integer) body[1];
         float elapsedTime = (Float) body[2];
         System.out.printf(INTERPRETER_SUMMARY_FORMAT, statementsExecuted, syntaxErrors, elapsedTime);
      }
      else if (type == MessageType.COMPILER_SUMMARY)
      {
         var body = (Number[]) message.getBody();
         var instructionsGenerated = (Integer) body[0];
         float elapsedTime = (Float) body[1];
         System.out.printf(COMPILER_SUMMARY_FORMAT, instructionsGenerated, elapsedTime);
      }
      else if (type == MessageType.TOKEN)
      {
         var body = (Object[]) message.getBody();
         var lineNumber = (Integer) body[0];
         var position = (Integer) body[1];
         var tokenType = body[2];
         var tokenText = body[3];
         var tokenValue = body[4];
         System.out.printf(TOKEN_FORMAT, tokenType, lineNumber, position, tokenText);

         if (tokenValue != null)
         {
            if (tokenType == PascalTokenType.STRING)
               tokenValue = "\"" + tokenValue + "\"";
            System.out.printf(VALUE_FORMAT, tokenValue);
         }

      }
      else if (type == MessageType.SYNTAX_ERROR)
      {
         var body = (Object[]) message.getBody();
         var lineNumber = (Integer) body[0];
         var position = (Integer) body[1];
         var tokenText = body[2];
         var errorMessage = body[3];
         var spaceCount = PREFIX_WIDTH + position;

         var flagBuffer = new StringBuilder();
         for (int i = 0; i < spaceCount; i++)
            flagBuffer.append(" ");
         flagBuffer.append("^\n***");
         flagBuffer.append(errorMessage);

         if (tokenText != null)
         {
            flagBuffer.append("[at \"");
            flagBuffer.append(tokenText);
            flagBuffer.append("\"]");
         }

         System.out.println(flagBuffer.toString());
      }
      else if (type == MessageType.ASSIGN)
      {
         if (firstOutputMessage)
         {
            System.out.println("\n===== OUTPUT =====\n");
            firstOutputMessage = false;
         }

         var body = (Object[]) message.getBody();
         var lineNumber = (Integer) body[0];
         var variableName = (String) body[1];
         var value = body[2];

         System.out.printf(ASSIGN_FORMAT, lineNumber, variableName, value);
      }
      else if (type == MessageType.RUNTIME_ERROR)
      {
         var body = (Object[]) message.getBody();
         var errorMessage = (String) body[0];
         var lineNumber = (Integer) body[1];

         System.out.print("*** RUNTIME ERROR");
         if (lineNumber != null)
            System.out.print(" AT LINE " + String.format("%03d", lineNumber));
         System.out.println(": " + errorMessage);
      }
      else
      {
         System.out.println("Panic!!!");
      }
   }
}
