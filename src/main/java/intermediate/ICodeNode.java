package intermediate;

import java.util.List;

public interface ICodeNode
{
   public ICodeNode getParent();

   public ICodeNodeType getType();

   public ICodeNode addChild(ICodeNode node);

   public List<ICodeNode> getChildren();

   public void setAttribute(ICodeKey key, Object value);

   public Object getAttribute(ICodeKey key);

   public ICodeNode copy();
}
