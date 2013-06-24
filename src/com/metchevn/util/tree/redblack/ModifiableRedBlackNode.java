package com.metchevn.util.tree.redblack;

import com.metchevn.util.tree.binary.ModifiableBinaryTreeNode;

public interface ModifiableRedBlackNode<T, N extends ModifiableRedBlackNode<T, N>> extends ImmutableRedBlackNode<T>, ModifiableBinaryTreeNode<T, N> 
{
  void setColour(boolean colour);
}