package intermediate.symtabimpl;

import intermediate.SymTab;
import intermediate.SymTabEntry;
import intermediate.SymTabFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class SymTabImpl extends TreeMap<String, SymTabEntry> implements SymTab
{
    private int nestingLevel;

    public SymTabImpl(int nestingLevel)
    {
        this.nestingLevel = nestingLevel;
    }

    @Override
    public int getNestingLevel()
    {
        return nestingLevel;
    }

    @Override
    public SymTabEntry enter(String name)
    {
        var entry = SymTabFactory.createSymTabEntry(name, this);
        put(name, entry);
        return entry;
    }

    @Override
    public SymTabEntry lookup(String name)
    {
        return get(name);
    }

    @Override
    public List<SymTabEntry> sortedEntries()
    {
        var res = new ArrayList<SymTabEntry>(size());
        for (var entry : values())
        {
            res.add(entry);
        }
        return res;
    }
}
