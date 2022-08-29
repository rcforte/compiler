package intermediate.symtabimpl;

import intermediate.SymTab;
import intermediate.SymTabEntry;
import intermediate.SymTabKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymTabEntryImpl extends HashMap<SymTabKey, Object> implements SymTabEntry
{
   private String name;
   private SymTab symTab;
   private List<Integer> lineNumbers;

   public SymTabEntryImpl(String name, SymTab symTab)
   {
      this.name = name;
      this.symTab = symTab;
      this.lineNumbers = new ArrayList<>();
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public SymTab getSymTab()
   {
      return symTab;
   }

   @Override
   public void appendLineNumber(int lineNumber)
   {
      lineNumbers.add(lineNumber);
   }

   @Override
   public List<Integer> getLineNumbers()
   {
      return lineNumbers;
   }

   @Override
   public void setAttribute(SymTabKey key, Object value)
   {
      put(key, value);
   }

   @Override
   public Object getAttribute(SymTabKey key)
   {
      return get(key);
   }
}
