package backend.interpreter.executors;

import backend.interpreter.Executor;
import intermediate.ICodeNode;
import intermediate.SymTabEntry;

import static intermediate.icodeimpl.ICodeKeyImpl.ID;
import static intermediate.symtabimpl.SymTabKeyImpl.DATA_VALUE;

public class AssignmentExecutor extends Executor {
    public AssignmentExecutor(Executor parent) {
        super(parent);
    }

    public Object execute(ICodeNode node) {
        var variableNode = node.getChildren().get(0);
        var expressionNode = node.getChildren().get(1);

        var expressionExecutor = new ExpressionExecutor(this);
        var result = expressionExecutor.execute(expressionNode);

        var variableId = (SymTabEntry) variableNode.getAttribute(ID);
        variableId.setAttribute(DATA_VALUE, result);

        ++executionCount;

        return null;
    }
}
