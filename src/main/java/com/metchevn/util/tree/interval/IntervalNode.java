package com.metchevn.util.tree.interval;

import com.google.common.collect.Ordering;
import com.metchevn.util.interval.Interval;
import com.metchevn.util.tree.redblack.AbstractRedBlackNode;

public class IntervalNode<T extends Comparable<T>, P extends Comparable<P>> extends AbstractRedBlackNode<Event<T,P>, IntervalNode<T,P>>
{
  private final Ordering<T> m_naturalOrdering = Ordering.natural();
  private Interval<T> m_minMaxInterval;
  
  protected IntervalNode(Event<T,P> payload)
  {
    super(payload);
    m_minMaxInterval = payload.getInterval();
  }
  
  @Override
  public void setLeft(IntervalNode<T,P> left)
  {
    super.setLeft(left);
    if (left != null)
    {
      updateMinMax(left);
    }
  }
  
  @Override
  public void setRight(IntervalNode<T,P> right)
  {
    super.setRight(right);
    if (right != null)
    {
      updateMinMax(right);
    }
  }
  
  @Override
  public void setParent(IntervalNode<T,P> parent)
  {
    super.setParent(parent);
    if (parent != null)
    {
      parent.updateMinMax(this);
    }
  }
  
  private void updateMinMax(IntervalNode<T, P> newChild)
  {
    updateMinMax(newChild.getMinMaxInterval());
  }

  private void updateMinMax(Interval<T> newChildMinMaxInterval)
  {
    Interval<T> newInterval = new Interval<>(
        m_naturalOrdering.min(m_minMaxInterval.getFrom(), newChildMinMaxInterval.getFrom()), 
        m_naturalOrdering.max(m_minMaxInterval.getTo(), newChildMinMaxInterval.getTo()));
    if (!m_minMaxInterval.equals(newInterval))
    {
      m_minMaxInterval = newInterval;
      IntervalNode<T,P> parent = getParent();
      if (parent != null)
      {
        parent.updateMinMax(newInterval);
      }
    }
  }
  
  public Interval<T> getMinMaxInterval()
  {
    return m_minMaxInterval;
  }
}
