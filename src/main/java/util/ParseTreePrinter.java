package util;

import intermediate.ICode;
import intermediate.ICodeNode;
import intermediate.SymTabEntry;
import intermediate.icodeimpl.ICodeNodeImpl;

import java.io.PrintStream;
import java.util.List;

public class ParseTreePrinter
{
   private static final int INDENT_WIDTH = 4;
   private static final int LINE_WIDTH = 80;

   private PrintStream ps;
   private int length;
   private String indentation;
   private String indent;
   private StringBuilder line;


   public ParseTreePrinter(PrintStream ps)
   {
      this.ps = ps;
      this.length = 0;
      this.indentation = "";
      this.line = new StringBuilder();

      this.indent = "";
      for (int i = 0; i < INDENT_WIDTH; i++)
      {
         this.indent += " ";
      }
   }

   public void print(ICode iCode)
   {
      ps.println("\n===== INTERMEDIATE CODE =====\n");
      printNode((ICodeNodeImpl) iCode.getRoot());
      printLine();
   }

   /**
    * Post order traversal for the syntax tree.
    */
   private void printNode(ICodeNodeImpl node)
   {
      append(indentation);
      append("<" + node.toString());

      printAttributes(node);
      printTypeSpec(node);

      List<ICodeNode> childNodes = node.getChildren();
      if (childNodes != null && !childNodes.isEmpty())
      {
         append(">");
         printLine();

         printChildNodes(childNodes);

         append(indentation);
         append("</" + node.toString() + ">");
      }
      else
      {
         append(" ");
         append("/>");
      }

      printLine();
   }

   private void printAttributes(ICodeNodeImpl node)
   {
      var saveIndentation = indentation;
      indentation += indent;
      for (var entry : node.entrySet())
      {
         printAttribute(entry.getKey().toString(), entry.getValue());
      }
      indentation = saveIndentation;
   }

   private void printAttribute(String keyString, Object value)
   {
      var isSymTabEntry = value instanceof SymTabEntry;
      var valueString = isSymTabEntry
         ? ((SymTabEntry) value).getName()
         : value.toString();
      var text = keyString.toLowerCase() + "=\"" + valueString + "\"";
      append(" ");
      append(text);
      if (isSymTabEntry)
      {
         var level = ((SymTabEntry) value).getSymTab().getNestingLevel();
         printAttribute("LEVEL", level);
      }
   }

   private void printChildNodes(List<ICodeNode> childNodes)
   {
      var saveIndentation = indentation;
      indentation += indent;
      for (var child : childNodes)
      {
         printNode((ICodeNodeImpl) child);
      }
      indentation = saveIndentation;
   }

   private void printTypeSpec(ICodeNodeImpl node)
   {
   }

   private void append(String text)
   {
      var textLength = text.length();
      boolean lineBreak = false;
      if (length + textLength > LINE_WIDTH)
      {
         printLine();
         line.append(indentation);
         length = indentation.length();
         lineBreak = true;
      }

      if (!(lineBreak && text.equals(" ")))
      {
         line.append(text);
         length += textLength;
      }
   }

   private void printLine()
   {
      if (length != 0)
      {
         ps.println(line);
         line.setLength(0);
         length = 0;
      }
   }
}
