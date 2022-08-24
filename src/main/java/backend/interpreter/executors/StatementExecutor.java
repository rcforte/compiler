package backend.interpreter.executors;

import backend.interpreter.Executor;
import backend.interpreter.RuntimeErrorCode;
import intermediate.ICodeNode;
import intermediate.icodeimpl.ICodeNodeTypeImpl;
import message.Message;

import static intermediate.icodeimpl.ICodeKeyImpl.LINE;
import static message.MessageType.SOURCE_LINE;

public class StatementExecutor extends Executor {
    public StatementExecutor(Executor parent) {
        super(parent);
    }

    public Object execute(ICodeNode node) {
        sendSourceLineMessage(node);

        var nodeType = (ICodeNodeTypeImpl) node.getType();
        switch (nodeType) {
            case COMPOUND:
                return new CompoundExecutor(this).execute(node);
            case ASSIGN:
                return new AssignmentExecutor(this).execute(node);
            case NO_OP:
                return null;

            default: {
                errorHandler.flag(node, RuntimeErrorCode.UNIMPLEMENTED_FEATURE, this);
                return null;
            }
        }
    }

    private void sendSourceLineMessage(ICodeNode node) {
        var lineNumber = node.getAttribute(LINE);
        if (lineNumber != null) sendMessage(new Message(SOURCE_LINE, new Object[]{lineNumber, "<No line>"}));
    }
}
