package intermediate;

import java.util.List;

public interface SymTab {
    public int getNestingLevel();

    public SymTabEntry enter(String name);

    public SymTabEntry lookup(String name);

    public List<SymTabEntry> sortedEntries();
}
