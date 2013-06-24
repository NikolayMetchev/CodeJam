package com.metchevn.util.tree.interval;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import com.metchevn.util.tree.redblack.TestRedBlackTree;

import java.util.Collection;

import org.junit.Test;

public class TestIntervalTree
{
  @Test
  public void testIntervals()
  {
    IntervalTree<Integer, String> tree = new IntervalTree<>();
    Event<Integer, String> a = new Event<>(1, 3, "A");
    Event<Integer, String> b = new Event<>(2, 10, "B");
    Event<Integer, String> c = new Event<>(5, 10, "C");
    Event<Integer, String> d = new Event<>(100, 101, "D");
    Event<Integer, String> e = new Event<>(102, 103, "E");
    Event<Integer, String> f = new Event<>(5, 10, "F");
    tree.add(a);
    tree.add(b);
    tree.add(c);
    tree.add(d);
    tree.add(e);
    tree.add(f);
    TestRedBlackTree.printDepth(tree);
    testContainment(tree.intersections(7), b, c, f);
    testContainment(tree.intersections(0));
    testContainment(tree.intersections(100), d);
    testContainment(tree.intersections(1000));
    testContainment(tree.overlaps(0, 105),a,b,c,d,e,f);
    testContainment(tree.overlaps(104, 105));
    testContainment(tree.overlaps(7, 11), b, c, f);
  }

  @SafeVarargs
  public static <T> void testContainment(Collection<T> collection, T... expected)
  {
    assertThat(collection, hasSize(expected.length));
    assertThat(collection, hasItems(expected));
  }
}