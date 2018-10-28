package com.metchevn.util.tree.redblack;

public class DefaultRedBlackNode<T> extends AbstractRedBlackNode<T, DefaultRedBlackNode<T>> 
{
  public DefaultRedBlackNode(T payload)
  {
    super(payload);
  }
}
