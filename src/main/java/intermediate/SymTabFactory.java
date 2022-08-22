package intermediate;

import intermediate.symtabimpl.SymTabEntryImpl;
import intermediate.symtabimpl.SymTabImpl;
import intermediate.symtabimpl.SymTabStackImpl;

public class SymTabFactory
{
    public static SymTabStack createSymTabStack()
    {
        return new SymTabStackImpl();
    }

    public static SymTab createSymTab(int nestingLevel)
    {
        return new SymTabImpl(nestingLevel);
    }

    public static SymTabEntry createSymTabEntry(String name, SymTab symTab)
    {
        return new SymTabEntryImpl(name, symTab);
    }
}
