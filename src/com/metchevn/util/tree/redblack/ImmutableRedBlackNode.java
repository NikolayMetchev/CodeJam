package com.metchevn.util.tree.redblack;

import com.metchevn.util.tree.binary.ImmutableBinaryTreeNode;

public interface ImmutableRedBlackNode<T> extends ImmutableBinaryTreeNode<T>
{
  boolean getColour();
}