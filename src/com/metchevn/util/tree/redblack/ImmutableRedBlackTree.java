package com.metchevn.util.tree.redblack;

import java.util.Iterator;


public interface ImmutableRedBlackTree<T> extends Iterable<T>
{
  ImmutableRedBlackNode<T> getRoot();
  
  int size();
  
  Iterable<ImmutableRedBlackNode<T>> immutableNodeIterable();
  
  Iterator<ImmutableRedBlackNode<T>> immutableNodeIterator();
}
