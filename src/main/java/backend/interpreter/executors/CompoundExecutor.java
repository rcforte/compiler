package backend.interpreter.executors;

import backend.interpreter.Executor;
import intermediate.ICodeNode;

public class CompoundExecutor extends Executor {
    public CompoundExecutor(Executor parent) {
        super(parent);
    }

    public Object execute(ICodeNode node) {
        var statementExecutor = new StatementExecutor(this);
        for (var child : node.getChildren()) {
            statementExecutor.execute(child);
        }
        return null;
    }
}
