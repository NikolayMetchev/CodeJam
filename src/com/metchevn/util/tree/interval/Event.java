package com.metchevn.util.tree.interval;

import com.metchevn.util.interval.Interval;

public class Event<T extends Comparable<T>, P extends Comparable<P>>
{
  private final Interval<T> m_interval;
  private final P m_payload;
  
  public Event(T from, T to, P payload)
  {
    this(new Interval<>(from, to), payload);
  }
  
  public Event(Interval<T> interval, P payload)
  {
    m_payload = payload;
    m_interval = interval;
  }

  public Interval<T> getInterval()
  {
    return m_interval;
  }

  public P getPayload()
  {
    return m_payload;
  }

  @Override
  public String toString()
  {
    return m_interval + " = " + m_payload;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((m_interval == null) ? 0 : m_interval.hashCode());
    result = prime * result + ((m_payload == null) ? 0 : m_payload.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Event<?,?> other = (Event<?,?>) obj;
    if (m_interval == null)
    {
      if (other.m_interval != null)
        return false;
    }
    else if (!m_interval.equals(other.m_interval))
      return false;
    if (m_payload == null)
    {
      if (other.m_payload != null)
        return false;
    }
    else if (!m_payload.equals(other.m_payload))
      return false;
    return true;
  }
}
