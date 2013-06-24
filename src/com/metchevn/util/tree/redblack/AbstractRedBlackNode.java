package com.metchevn.util.tree.redblack;

import static com.metchevn.util.tree.redblack.Colour.BLACK;

import com.metchevn.util.tree.binary.AbstractBinaryTreeNode;

public abstract class AbstractRedBlackNode<T, N extends AbstractRedBlackNode<T,N>> extends AbstractBinaryTreeNode<T, N> implements ModifiableRedBlackNode<T, N>
{
  private boolean m_colour = BLACK;
  
  protected AbstractRedBlackNode(T payload)
  {
    super(payload);
  }

  @Override
  public boolean getColour()
  {
    return m_colour;
  }

  @Override
  public void setColour(boolean colour)
  {
    m_colour = colour;
  }
}
