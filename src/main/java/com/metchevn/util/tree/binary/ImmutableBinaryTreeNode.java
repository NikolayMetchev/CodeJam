package com.metchevn.util.tree.binary;

public interface ImmutableBinaryTreeNode<T>
{
  T getPayload();

  ImmutableBinaryTreeNode<T> getLeft();

  ImmutableBinaryTreeNode<T> getRight();

  ImmutableBinaryTreeNode<T> getParent();

  boolean isLeaf();
  
  int getDepth();
}