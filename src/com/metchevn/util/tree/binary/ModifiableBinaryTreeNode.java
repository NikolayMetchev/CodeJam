package com.metchevn.util.tree.binary;

public interface ModifiableBinaryTreeNode<T, N extends ModifiableBinaryTreeNode<T, N>> extends ImmutableBinaryTreeNode<T>
{
  @Override
  N getLeft();

  @Override
  N getRight();

  @Override
  N getParent();

  void setPayLoad(T payload);

  void setParent(N parent);

  void setRight(N right);

  void setLeft(N left);
}