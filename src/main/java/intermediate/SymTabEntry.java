package intermediate;

import java.util.List;

public interface SymTabEntry
{
    public String getName();

    public SymTab getSymTab();

    public void appendLineNumber(int lineNumber);

    public List<Integer> getLineNumbers();

    public void setAttribute(SymTabKey key, Object value);

    public Object getAttribute(SymTabKey key);
}
