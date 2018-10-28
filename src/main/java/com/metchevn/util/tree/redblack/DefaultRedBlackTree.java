package com.metchevn.util.tree.redblack;

import java.util.Comparator;

public class DefaultRedBlackTree<T> extends AbstractRedBlackTree<T, DefaultRedBlackNode<T>>
{
  public DefaultRedBlackTree(Comparator<T> comparator)
  {
    super(comparator);
  }

  @Override
  protected DefaultRedBlackNode<T> construct(T payload)
  {
    return new DefaultRedBlackNode<>(payload);
  }
}
