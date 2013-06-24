package com.metchevn.util.tree.binary;


public abstract class AbstractBinaryTreeNode<T, N extends AbstractBinaryTreeNode<T, N>> implements ModifiableBinaryTreeNode<T, N>
{
  private N m_left;
  private N m_right;
  private N m_parent;

  private T m_payload;
  
  private int m_depth = 0;
  
  public AbstractBinaryTreeNode(T payload)
  {
    m_payload = payload;
  }
  
  @Override
  public T getPayload()
  {
    return m_payload;
  }
  
  @Override
  public N getLeft()
  {
    return m_left;
  }

  @Override
  public N getRight()
  {
    return m_right;
  }
  
  @Override
  public N getParent()
  {
    return m_parent;
  }
  
  @Override
  public void setLeft(N left)
  {
    m_left = left;
    if (left != null)
    {
      left.setDepth(m_depth + 1);
    }
  }
  
  @Override
  public void setRight(N right)
  {
    m_right = right;
    if (right != null)
    {
      right.setDepth(m_depth + 1);
    }
  }
  
  @Override
  public void setParent(N parent)
  {
    setDepth(parent == null ? 0 : parent.getDepth() + 1);
    m_parent = parent;
  }
  
  @Override
  public String toString()
  {
    return m_payload.toString();
  }
  
  @Override
  public void setPayLoad(T payload)
  {
    m_payload = payload;
  }
  
  @Override
  public int getDepth()
  {
    return m_depth;
  }
  
  void setDepth(int depth)
  {
    if (m_depth != depth)
    {
      if (m_left != null)
      {
        m_left.setDepth(depth + 1);
      }
      if (m_right != null)
      {
        m_right.setDepth(depth + 1);
      }
      m_depth = depth;
    }
  }
  
  @Override
  public boolean isLeaf()
  {
    return m_left == null && m_right == null;
  }
}
