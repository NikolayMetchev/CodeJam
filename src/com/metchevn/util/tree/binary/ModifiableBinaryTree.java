package com.metchevn.util.tree.binary;

import java.util.Collection;
import java.util.Iterator;

public interface ModifiableBinaryTree<T, N extends ModifiableBinaryTreeNode<T, N>> extends ImmutableBinaryTree<T, N>
{
  void setRoot(N root);
  
  boolean add(T e);
  
  boolean addAll(Collection<? extends T> values);
  
  boolean remove(Object payLoad);
  
  void clear();
  
  Iterator<T> modifiableIterator();
  
  Iterable<N> nodeIterable();
  
  Iterator<N> nodeIterator();
}
