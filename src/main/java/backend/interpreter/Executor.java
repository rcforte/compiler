package backend.interpreter;

import backend.Backend;
import intermediate.ICode;
import intermediate.SymTabStack;
import message.Message;
import message.MessageType;

public class Executor extends Backend {
    @Override
    public void process(ICode icode, SymTabStack symtab) {
        long t0 = System.currentTimeMillis();
        long t1 = System.currentTimeMillis();
        float elapsed = (t1 - t0) / 1000f;
        int executionCount = 0;
        int runtimeErrors = 0;
        sendMessage(new Message(MessageType.INTERPRETER_SUMMARY, new Number[]{executionCount, runtimeErrors, elapsed}));
    }
}
