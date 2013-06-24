package com.metchevn.util.tree.interval;

import com.google.common.collect.ComparisonChain;
import com.metchevn.util.interval.Interval;
import com.metchevn.util.tree.redblack.AbstractRedBlackTree;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;

public class IntervalTree<T extends Comparable<T>, P extends Comparable<P>> extends AbstractRedBlackTree<Event<T, P>, IntervalNode<T,P>>
{
  public static Comparator<Event<?,?>> EVENT_COMPARATOR = new Comparator<Event<?, ?>>()
  {
    @Override
    public int compare(Event<?, ?> e1, Event<?, ?> e2)
    {
      Interval<?> i1 = e1.getInterval();
      Interval<?> i2 = e2.getInterval();
      return ComparisonChain.start()
          .compare(i1.getFrom(), i2.getFrom())
          .compare(i1.getTo(), i2.getTo())
          .compare(e1.getPayload(), e2.getPayload())
          .result();
    }
  };
  
  public IntervalTree()
  {
    super(EVENT_COMPARATOR);
  }

  @Override
  protected IntervalNode<T,P> construct(Event<T,P> payload)
  {
    return new IntervalNode<>(payload);
  }
  
  public NavigableSet<Event<T,P>> overlaps(T from, T to)
  {
    return overlaps(new Interval<>(from, to));
  }
  
  public NavigableSet<Event<T,P>> overlaps(T from , boolean fromInclusive, T to, boolean toInclusive)
  {
    return overlaps(new Interval<>(from, fromInclusive, to, toInclusive));
  }
  
  public NavigableSet<Event<T,P>> overlaps(Interval<? extends T> interval)
  {
    NavigableSet<Event<T, P>> ans = new TreeSet<>(EVENT_COMPARATOR);
    IntervalNode<T, P> root = getRoot();
    overlaps(root, interval, ans);
    return ans;
  }
  
  private void overlaps(IntervalNode<T, P> node, Interval<? extends T> interval, NavigableSet<Event<T, P>> ans)
  {
    if (node != null && node.getMinMaxInterval().overlaps(interval))
    {
      Event<T, P> payload = node.getPayload();
      if (payload.getInterval().overlaps(interval))
      {
        ans.add(payload);
      }
      overlaps(node.getLeft(), interval, ans);
      overlaps(node.getRight(), interval, ans);
    }
  }

  public NavigableSet<Event<T,P>> intersections(T point)
  {
    NavigableSet<Event<T, P>> ans = new TreeSet<>(EVENT_COMPARATOR);
    IntervalNode<T, P> root = getRoot();
    intersections(root, point, ans);
    return ans;
  }

  private void intersections(IntervalNode<T, P> node, T point, NavigableSet<Event<T, P>> ans)
  {
    if (node != null && node.getMinMaxInterval().isWithin(point))
    {
      Event<T, P> payload = node.getPayload();
      if (payload.getInterval().isWithin(point))
      {
        ans.add(payload);
      }
      intersections(node.getLeft(),point, ans);
      intersections(node.getRight(),point, ans);
    }
  }
  
}
