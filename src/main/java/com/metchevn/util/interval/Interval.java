package com.metchevn.util.interval;

import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Interval<T extends Comparable<T>> {
  private final Ordering<T> NATURAL_ORDERING = Ordering.natural();
  private final T m_from;
  private final T m_to;
  private final boolean m_fromInclusive;
  private final boolean m_toInclusive;

  public Interval(T from, T to) {
    this(from, true, to, false);
  }

  public Interval(T from, boolean fromInclusive, T to, boolean toInclusive) {
    m_from = from;
    m_fromInclusive = fromInclusive;
    m_to = to;
    m_toInclusive = toInclusive;
    if (from == null || to == null || !isAfterFrom(to) || !isBeforeTo(from)) {
      throw new IllegalArgumentException("From must be less than to and they must both be non-null, from=" + from + " to =" + to);
    }
  }

  public T getFrom() {
    return m_from;
  }

  public T getTo() {
    return m_to;
  }

  public boolean overlaps(Interval<? extends T> other) {
    return overlaps(other.m_from, other.m_fromInclusive, other.m_to, other.m_toInclusive);
  }

  public boolean overlaps(T from, T to) {
    return overlaps(from, true, to, false);
  }

  public boolean overlaps(T from, boolean fromInclusive, T to, boolean toInclusive) {
    boolean isAfter = isAfter(to, from, fromInclusive);
    boolean isBefore = isBefore(from, to, toInclusive);
    if (from == null || to == null || !isAfter || !isBefore) {
      throw new IllegalArgumentException("From must be less than to and they must both be non-null, from=" + from + " to =" + to);
    }
    boolean afterFrom = isAfterFrom(to, m_fromInclusive & toInclusive);
    boolean beforeTo = isBeforeTo(from, m_toInclusive && fromInclusive);
    return afterFrom && beforeTo;
  }

  public boolean isWithin(T point) {
    return isAfterFrom(point) && isBeforeTo(point);
  }

  public boolean isAfterFrom(T point) {
    return isAfterFrom(point, m_fromInclusive);
  }

  public boolean isAfterFrom(T point, boolean inclusive) {
    return isAfter(point, m_from, inclusive);
  }

  boolean isAfter(T a, T b, boolean inclusive) {
    int cmp = a.compareTo(b);
    return inclusive ? cmp >= 0 : cmp > 0;
  }

  public boolean isBeforeTo(T point) {
    return isBeforeTo(point, m_toInclusive);
  }

  public boolean isBeforeTo(T point, boolean inclusive) {
    return isBefore(point, m_to, inclusive);
  }

  boolean isBefore(T a, T b, boolean inclusive) {
    int cmp = a.compareTo(b);
    return inclusive ? cmp <= 0 : cmp < 0;
  }

  public Interval<T> intersection(Interval<? extends T> other) {
    return intersection(other.m_from, other.m_fromInclusive, other.m_to, other.m_toInclusive);
  }

  public Interval<T> intersection(T otherFrom, T otherTo) {
    return intersection(otherFrom, true, otherTo, false);
  }

  public Interval<T> intersection(T otherFrom, boolean otherFromInclusive, T otherTo, boolean otherToInclusive) {
    if (!overlaps(otherFrom, otherFromInclusive, otherTo, otherToInclusive)) {
      return null;
    }
    boolean fromInclusive;
    if (m_from.equals(otherFrom)) {
      fromInclusive = m_fromInclusive && otherFromInclusive;
    } else {
      fromInclusive = isAfter(m_from, otherFrom, false) ? m_fromInclusive : otherFromInclusive;
    }
    boolean toInclusive;
    if (m_to.equals(otherTo)) {
      toInclusive = m_toInclusive && otherToInclusive;
    } else {
      toInclusive = isBefore(m_to, otherTo, false) ? m_toInclusive : otherToInclusive;
    }

    T maxFrom = NATURAL_ORDERING.max(m_from, otherFrom);
    T minTo = NATURAL_ORDERING.min(m_to, otherTo);

    return new Interval<>(
            maxFrom,
            fromInclusive,
            minTo,
            toInclusive
    );
  }

  public boolean isFromInclusive() {
    return m_fromInclusive;
  }

  public boolean isToInclusive() {
    return m_toInclusive;
  }

  public List<Interval<T>> minus(Interval<T> other) {
    if (!overlaps(other)) {
      return Collections.singletonList(this);
    }
    boolean otherFromBefore = isBefore(other.m_from, m_from, other.m_fromInclusive && m_fromInclusive);
    boolean otherFromAfter = isAfter(other.m_from, m_from, other.m_fromInclusive && m_fromInclusive);
    boolean otherToBefore = isBefore(other.m_to, m_to, other.m_toInclusive && m_toInclusive);
    boolean otherToAfter = isAfter(other.m_to, m_to, other.m_toInclusive && m_toInclusive);

    //Case 1
    //     |-------|  this
    // |------|       other
    //        |----|  result
    if (otherFromBefore && otherToBefore) {
      return Collections.singletonList(new Interval<>(other.m_to, !other.m_toInclusive, m_to, m_toInclusive));
    }

    //Case 2
    //     |-------|     this
    // |---------------| other
    //                   result
    if (otherFromBefore && otherToAfter) {
      return Collections.emptyList();
    }

    //Case 3
    // |-------|     this
    //      |------| other
    // |----|        result
    if (otherFromAfter && otherToAfter) {
      return Collections.singletonList(new Interval<>(m_from, m_fromInclusive, other.m_from, !other.m_fromInclusive));
    }

    //Case 4
    // |------------|  this
    //      |---|      other
    // |----|   |---|  result
    if (otherFromAfter && otherToBefore) {
      return Arrays.asList(new Interval<>(m_from, m_fromInclusive, other.m_from, !other.m_fromInclusive),
              new Interval<>(other.m_to, !other.m_toInclusive, m_to, m_toInclusive));
    }

    throw new RuntimeException("Logic Error.. what did I miss?");
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((m_from == null) ? 0 : m_from.hashCode());
    result = prime * result + (m_fromInclusive ? 1231 : 1237);
    result = prime * result + ((m_to == null) ? 0 : m_to.hashCode());
    result = prime * result + (m_toInclusive ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Interval<?> other = (Interval<?>) obj;
    if (m_from == null) {
      if (other.m_from != null)
        return false;
    } else if (!m_from.equals(other.m_from))
      return false;
    if (m_fromInclusive != other.m_fromInclusive)
      return false;
    if (m_to == null) {
      if (other.m_to != null)
        return false;
    } else if (!m_to.equals(other.m_to))
      return false;
    if (m_toInclusive != other.m_toInclusive)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "[" + m_from + "," + m_fromInclusive + "," + m_to + "," + m_toInclusive + "]";
  }
}
