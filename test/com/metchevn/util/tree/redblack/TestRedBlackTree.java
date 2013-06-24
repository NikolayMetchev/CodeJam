package com.metchevn.util.tree.redblack;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class TestRedBlackTree
{
  @Test
  public void testInsertions()
  {
    ModifiableRedBlackTree<String,DefaultRedBlackNode<String>> tree = new DefaultRedBlackTree<>(Ordering.<String>natural());
    tree.add("H");
    Assert.assertThat(tree.size(), is(1));
    tree.add("A");
    Assert.assertThat(tree.size(), is(2));
    tree.add("D");
    Assert.assertThat(tree.size(), is(3));
    tree.add("Z");
    Assert.assertThat(tree.size(), is(4));
    tree.add("B");
    Assert.assertThat(tree.size(), is(5));
    tree.add("C");
    Assert.assertThat(tree.size(), is(6));
    tree.add("J");
    Assert.assertThat(tree.size(), is(7));
    tree.add("G");
    Assert.assertThat(tree.size(), is(8));
    tree.add("I");
    Assert.assertThat(tree.size(), is(9));
    tree.add("E");
    Assert.assertThat(tree.size(), is(10));
    tree.add("F");
    Assert.assertThat(tree.size(), is(11));
    Assert.assertThat(Joiner.on("").join(tree), is("ABCDEFGHIJZ"));
  }
  
  @Test
  public void testRemovals()
  {
    ModifiableRedBlackTree<Character,DefaultRedBlackNode<Character>> tree = new DefaultRedBlackTree<>(Ordering.<Character>natural());
    tree.addAll(Arrays.asList('A','B','C','D','E','F','G','H','I','J'));
    Assert.assertThat(Joiner.on("").join(tree), is("ABCDEFGHIJ"));
    Assert.assertThat(tree.size(), is(10));
    remove(tree, 'D', "ABCEFGHIJ");
    remove(tree, 'G', "ABCEFHIJ");
    remove(tree, 'A', "BCEFHIJ");
    remove(tree, 'H', "BCEFIJ");
    remove(tree, 'E', "BCFIJ");
    remove(tree, 'F', "BCIJ");
    remove(tree, 'B', "CIJ");
    remove(tree, 'C', "IJ");
    remove(tree, 'I', "J");
    remove(tree, 'J', "");
  }
  
  @Test
  public void testRemoveViaIterator()
  {
    ModifiableRedBlackTree<Character,DefaultRedBlackNode<Character>> tree = new DefaultRedBlackTree<>(Ordering.<Character>natural());
    tree.addAll(Arrays.asList('A','B','C','D','E','F','G','H','I','J'));
    Iterator<Character> iterator = tree.modifiableIterator();
    remove(tree, iterator, "BCDEFGHIJ");
    remove(tree, iterator, "CDEFGHIJ");
    remove(tree, iterator, "DEFGHIJ");
    remove(tree, iterator, "EFGHIJ");
    remove(tree, iterator, "FGHIJ");
    remove(tree, iterator, "GHIJ");
    remove(tree, iterator, "HIJ");
    remove(tree, iterator, "IJ");
    remove(tree, iterator, "J");
    remove(tree, iterator, "");
    Assert.assertFalse(iterator.hasNext());
  }
  
  public static void remove(ModifiableRedBlackTree<Character,?> tree, Iterator<Character> iterator, String contentsAfterRemove)
  {
    Assert.assertTrue(iterator.hasNext());
    iterator.next();
    iterator.remove();
    assertThat(Joiner.on("").join(tree), is(contentsAfterRemove));
  }

  public static void remove(ModifiableRedBlackTree<Character,?> tree, char removeChar, String contentsAfterRemove)
  {
    tree.remove(removeChar);
    assertThat(Joiner.on("").join(tree), is(contentsAfterRemove));
    assertThat(tree.size(), is(contentsAfterRemove.length()));
  }
  
  @Test
  public void testDepth()
  {
    int size = 10001;
    ModifiableRedBlackTree<Integer, DefaultRedBlackNode<Integer>> tree = new DefaultRedBlackTree<>(Ordering.<Integer>natural());
    for(int i = 1; i <= size; i++)
    {
      tree.add(i);
      assertThat(tree.size(), is(i));
      int maxDepth = 0;
      int minDepth = Integer.MAX_VALUE;
      for (ImmutableRedBlackNode<Integer> node : tree.immutableNodeIterable())
      {
        if (node.isLeaf())
        {
          maxDepth = Math.max(node.getDepth(), maxDepth);
          minDepth = Math.min(node.getDepth(), minDepth);
        }
      }
      Assert.assertThat(maxDepth, lessThanOrEqualTo((minDepth * 2)+ 1));
    }
  }
  
  @Test
  public void testClear()
  {
    ModifiableRedBlackTree<Integer, DefaultRedBlackNode<Integer>> tree = new DefaultRedBlackTree<>(Ordering.<Integer>natural());
    int size = 401;
    for(int i = 1; i <= size; i++)
    {
      tree.add(i);
    }
    assertThat(tree.size(), is(size));
    tree.clear();
    assertThat(tree.size(), is(0));
    Assert.assertFalse(tree.iterator().hasNext());
    Assert.assertFalse(tree.immutableNodeIterator().hasNext());
  }

  public static void printDepth(AbstractRedBlackTree<?,?> tree)
  {
    for (ImmutableRedBlackNode<?> node : tree.immutableNodeIterable())
    {
      System.out.println(node.getPayload() + " = " + node.getDepth() + " parent = " + node.getParent());
    }
    System.out.println("--");
  }
}
