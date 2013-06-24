package com.metchevn.util.tree.binary;

import java.util.Comparator;
import java.util.Iterator;

public interface ImmutableBinaryTree<T, N extends ImmutableBinaryTreeNode<T>> extends Iterable<T>
{
  N getRoot();
  
  int size();
  
  Iterable<N> immutableNodeIterable();
  
  Iterator<N> immutableNodeIterator();

  Comparator<? super T> comparator();
}
