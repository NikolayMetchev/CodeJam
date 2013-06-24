package com.metchevn.util.tree.redblack;

import com.metchevn.util.tree.binary.ModifiableBinaryTree;


public interface ModifiableRedBlackTree<T, N extends ModifiableRedBlackNode<T, N>> extends ImmutableRedBlackTree<T, N>, ModifiableBinaryTree<T,N>
{
  //empty
}