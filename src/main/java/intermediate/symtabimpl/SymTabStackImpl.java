package intermediate.symtabimpl;

import intermediate.SymTab;
import intermediate.SymTabEntry;
import intermediate.SymTabFactory;
import intermediate.SymTabStack;

import java.util.ArrayList;

public class SymTabStackImpl extends ArrayList<SymTab> implements SymTabStack
{
    private int currentNestingLevel;

    public SymTabStackImpl()
    {
        currentNestingLevel = 0;
        add(SymTabFactory.createSymTab(currentNestingLevel));
    }

    @Override
    public SymTab getLocalSymTab()
    {
        return get(currentNestingLevel);
    }

    @Override
    public SymTabEntry enterLocal(String name)
    {
        return get(currentNestingLevel).enter(name);
    }

    @Override
    public SymTabEntry lookupLocal(String name)
    {
        return get(currentNestingLevel).lookup(name);
    }

    @Override
    public SymTabEntry lookup(String name)
    {
        return lookupLocal(name);
    }

    @Override
    public int getCurrentNestingLevel()
    {
        return currentNestingLevel;
    }
}
