package com.metchevn.util.tree.redblack;

import java.util.Collection;
import java.util.Iterator;


public interface ModifiableRedBlackTree<T, N extends ModifiableRedBlackNode<T, N>> extends ImmutableRedBlackTree<T>
{
  @Override
  N getRoot();
  
  boolean add(T e);
  
  boolean addAll(Collection<? extends T> values);
  
  boolean remove(Object payLoad);
  
  void clear();
  
  Iterable<N> nodeIterable();

  Iterator<N> nodeIterator();
  
  Iterator<T> modifiableIterator();
}