package intermediate.icodeimpl;

import intermediate.ICodeFactory;
import intermediate.ICodeKey;
import intermediate.ICodeNode;
import intermediate.ICodeNodeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ICodeNodeImpl
        extends HashMap<ICodeKey, Object>
        implements ICodeNode {
    private ICodeNodeType type;
    private ICodeNode parent;
    private List<ICodeNode> children = new ArrayList<>();

    public ICodeNodeImpl(ICodeNodeType type) {
        this.type = type;
    }

    @Override
    public ICodeNode getParent() {
        return parent;
    }

    @Override
    public ICodeNodeType getType() {
        return type;
    }

    @Override
    public ICodeNode addChild(ICodeNode node) {
        if (node != null) {
            children.add(node);
            var c = (ICodeNodeImpl) node;
            c.parent = this;
        }
        return node;
    }

    @Override
    public List<ICodeNode> getChildren() {
        return children;
    }

    @Override
    public void setAttribute(ICodeKey key, Object value) {
        put(key, value);
    }

    @Override
    public Object getAttribute(ICodeKey key) {
        return get(key);
    }

    @Override
    public ICodeNode copy() {
        var copy = ICodeFactory.createICodeNode(this.type);
        for (var entry : entrySet()) {
            copy.setAttribute(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
