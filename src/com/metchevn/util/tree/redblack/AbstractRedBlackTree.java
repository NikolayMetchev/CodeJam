package com.metchevn.util.tree.redblack;

import static com.metchevn.util.tree.redblack.Colour.BLACK;
import static com.metchevn.util.tree.redblack.Colour.RED;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class AbstractRedBlackTree<T, N extends ModifiableRedBlackNode<T, N>> extends AbstractSet<T> implements NavigableSet<T>, ModifiableRedBlackTree<T, N>
{
  private final class NodeIterable<M extends ImmutableRedBlackNode<T>> implements Iterable<M>
  {
    private final boolean m_mutable;
    public NodeIterable(boolean mutable)
    {
      m_mutable = mutable;
    }
    @Override
    public Iterator<M> iterator()
    {
      return new Iterator<M>()
      {
        private N m_node;

        @Override
        public boolean hasNext()
        {
          if (m_node == null)
          {
            return m_root != null;
          }
          return successor(m_node) != null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public M next()
        {
          m_node = m_node == null ? getFirstNode() : successor(m_node);
          if (m_node == null)
          {
            throw new NoSuchElementException();
          }
          return (M) m_node;
        }

        @Override
        public void remove()
        {
          if (!m_mutable)
          {
            throw new IllegalStateException("Immutable iterator");
          }
          if (m_node == null)
          {
            throw new IllegalStateException();
          }
          N predecessor = predecessor(m_node);
          deleteNode(m_node);
          m_node = predecessor;
        }
      };
    }
  }
  
  private final Comparator<? super T> m_comparator;
  private N m_root;
  private int m_size = 0;
  private final Function<ImmutableRedBlackNode<T>, T> GET_PAYLOAD = new Function<ImmutableRedBlackNode<T>, T>()
  {
    @Override
    public T apply(ImmutableRedBlackNode<T> input)
    {
      return input.getPayload();
    }
  };

  public AbstractRedBlackTree(Comparator<? super T> comparator)
  {
    m_comparator = comparator;
  }

  private static <T, N extends ModifiableRedBlackNode<T, N>> boolean colorOf(N p)
  {
    return (p == null ? BLACK : p.getColour());
  }

  private static <T, N extends ModifiableRedBlackNode<T, N>> N parentOf(N p)
  {
    return (p == null ? null : p.getParent());
  }

  private static <T, N extends ModifiableRedBlackNode<T, N>> void setColor(N p, boolean c)
  {
    if (p != null)
    {
      p.setColour(c);
    }
  }

  private static <T, N extends ModifiableRedBlackNode<T, N>> N leftOf(N p)
  {
    return (p == null) ? null : p.getLeft();
  }

  private static <T, N extends ModifiableRedBlackNode<T, N>> N rightOf(N p)
  {
    return (p == null) ? null : p.getRight();
  }

  /** From CLR */
  private void rotateLeft(N p)
  {
    if (p != null)
    {
      N r = p.getRight();
      p.setRight(r.getLeft());
      if (r.getLeft() != null)
      {
        r.getLeft().setParent(p);
      }
      r.setParent(p.getParent());
      if (p.getParent() == null)
      {
        m_root = r;
      }
      else if (p.getParent().getLeft() == p)
      {
        p.getParent().setLeft(r);
      }
      else
      {
        p.getParent().setRight(r);
      }
      r.setLeft(p);
      p.setParent(r);
    }
  }

  /** From CLR */
  private void rotateRight(N p)
  {
    if (p != null)
    {
      N l = p.getLeft();
      p.setLeft(l.getRight());
      if (l.getRight() != null)
      {
        l.getRight().setParent(p);
      }
      l.setParent(p.getParent());
      if (p.getParent() == null)
      {
        m_root = l;
      }
      else if (p.getParent().getRight() == p)
      {
        p.getParent().setRight(l);
      }
      else
      {
        p.getParent().setLeft(l);
      }
      l.setRight(p);
      p.setParent(l);
    }
  }

  /** From CLR */
  private void fixAfterInsertion(N x)
  {
    x.setColour(RED);
    while (x != null && x != m_root && x.getParent().getColour() == RED)
    {
      if (parentOf(x) == leftOf(parentOf(parentOf(x))))
      {
        N y = rightOf(parentOf(parentOf(x)));
        if (colorOf(y) == RED)
        {
          setColor(parentOf(x), BLACK);
          setColor(y, BLACK);
          setColor(parentOf(parentOf(x)), RED);
          x = parentOf(parentOf(x));
        }
        else
        {
          if (x == rightOf(parentOf(x)))
          {
            x = parentOf(x);
            rotateLeft(x);
          }
          setColor(parentOf(x), BLACK);
          setColor(parentOf(parentOf(x)), RED);
          rotateRight(parentOf(parentOf(x)));
        }
      }
      else
      {
        N y = leftOf(parentOf(parentOf(x)));
        if (colorOf(y) == RED)
        {
          setColor(parentOf(x), BLACK);
          setColor(y, BLACK);
          setColor(parentOf(parentOf(x)), RED);
          x = parentOf(parentOf(x));
        }
        else
        {
          if (x == leftOf(parentOf(x)))
          {
            x = parentOf(x);
            rotateRight(x);
          }
          setColor(parentOf(x), BLACK);
          setColor(parentOf(parentOf(x)), RED);
          rotateLeft(parentOf(parentOf(x)));
        }
      }
    }
    m_root.setColour(BLACK);
  }

  public static <T, N extends ModifiableRedBlackNode<T, N>> N successor(N t)
  {
    if (t == null)
    {
      return null;
    }
    else if (t.getRight() != null)
    {
      N p = t.getRight();
      while (p.getLeft() != null)
      {
        p = p.getLeft();
      }
      return p;
    }
    else
    {
      N p = t.getParent();
      N ch = t;
      while (p != null && ch == p.getRight())
      {
        ch = p;
        p = p.getParent();
      }
      return p;
    }
  }

  public static <T, N extends ModifiableRedBlackNode<T, N>> N predecessor(N t)
  {
    if (t == null)
    {
      return null;
    }
    else if (t.getLeft() != null)
    {
      N p = t.getLeft();
      while (p.getRight() != null)
      {
        p = p.getRight();
      }
      return p;
    }
    else
    {
      N p = t.getParent();
      N ch = t;
      while (p != null && ch == p.getLeft())
      {
        ch = p;
        p = p.getParent();
      }
      return p;
    }
  }

  public N getFirstNode()
  {
    N p = m_root;
    if (p != null)
    {
      while (p.getLeft() != null)
      {
        p = p.getLeft();
      }
    }
    return p;
  }

  public N getLastNode()
  {
    N p = m_root;
    if (p != null)
    {
      while (p.getRight() != null)
      {
        p = p.getRight();
      }
    }
    return p;
  }

  /**
   * Delete node p, and then rebalance the tree.
   */
  private void deleteNode(N p)
  {
    m_size--;
    // If strictly internal, copy successor's element to p and then make p
    // point to successor.
    if (p.getLeft() != null && p.getRight() != null)
    {
      N s = successor(p);
      p.setPayLoad(s.getPayload());
      p = s;
    } // p has 2 children
    // Start fixup at replacement node, if it exists.
    N replacement = (p.getLeft() != null ? p.getLeft() : p.getRight());
    if (replacement != null)
    {
      // Link replacement to parent
      replacement.setParent(p.getParent());
      if (p.getParent() == null)
        m_root = replacement;
      else if (p == p.getParent().getLeft())
        p.getParent().setLeft(replacement);
      else
        p.getParent().setRight(replacement);
      // Null out links so they are OK to use by fixAfterDeletion.
      p.setLeft(null);
      p.setRight(null);
      p.setParent(null);
      // Fix replacement
      if (p.getColour() == BLACK)
        fixAfterDeletion(replacement);
    }
    else if (p.getParent() == null)
    { // return if we are the only node.
      m_root = null;
    }
    else
    { // No children. Use self as phantom replacement and unlink.
      if (p.getColour() == BLACK)
        fixAfterDeletion(p);
      if (p.getParent() != null)
      {
        if (p == p.getParent().getLeft())
          p.getParent().setLeft(null);
        else if (p == p.getParent().getRight())
          p.getParent().setRight(null);
        p.setParent(null);
      }
    }
  }

  /** From CLR */
  private void fixAfterDeletion(N x)
  {
    while (x != m_root && colorOf(x) == BLACK)
    {
      if (x == leftOf(parentOf(x)))
      {
        N sib = rightOf(parentOf(x));
        if (colorOf(sib) == RED)
        {
          setColor(sib, BLACK);
          setColor(parentOf(x), RED);
          rotateLeft(parentOf(x));
          sib = rightOf(parentOf(x));
        }
        if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK)
        {
          setColor(sib, RED);
          x = parentOf(x);
        }
        else
        {
          if (colorOf(rightOf(sib)) == BLACK)
          {
            setColor(leftOf(sib), BLACK);
            setColor(sib, RED);
            rotateRight(sib);
            sib = rightOf(parentOf(x));
          }
          setColor(sib, colorOf(parentOf(x)));
          setColor(parentOf(x), BLACK);
          setColor(rightOf(sib), BLACK);
          rotateLeft(parentOf(x));
          x = m_root;
        }
      }
      else
      { // symmetric
        N sib = leftOf(parentOf(x));
        if (colorOf(sib) == RED)
        {
          setColor(sib, BLACK);
          setColor(parentOf(x), RED);
          rotateRight(parentOf(x));
          sib = leftOf(parentOf(x));
        }
        if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK)
        {
          setColor(sib, RED);
          x = parentOf(x);
        }
        else
        {
          if (colorOf(leftOf(sib)) == BLACK)
          {
            setColor(rightOf(sib), BLACK);
            setColor(sib, RED);
            rotateLeft(sib);
            sib = leftOf(parentOf(x));
          }
          setColor(sib, colorOf(parentOf(x)));
          setColor(parentOf(x), BLACK);
          setColor(leftOf(sib), BLACK);
          rotateRight(parentOf(x));
          x = m_root;
        }
      }
    }
    setColor(x, BLACK);
  }
  
  public class ModifiedNode
  {
    private final N m_node;
    private final boolean m_modified;
    private ModifiedNode(boolean newNode, N node)
    {
      m_node = node;
      m_modified = newNode;
    }
    
    public N getNode()
    {
      return m_node;
    }
    
    public boolean isModified()
    {
      return m_modified;
    }
  }

  public ModifiedNode put(T value)
  {
    N node = construct(value);
    if (m_root == null)
    {
      m_size = 1;
      m_root = node;
      return new ModifiedNode(true, node);
    }
    N parent = m_root;
    N t = m_root;
    int cmp = 0;
    while (t != null)
    {
      parent = t;
      cmp = compare(node, t);
      if (cmp == 0)
      {
        return new ModifiedNode(false, parent);
      }
      if (cmp < 0)
      {
        t = t.getLeft();
      }
      else
      {
        t = t.getRight();
      }
    }
    node.setParent(parent);
    if (cmp < 0)
    {
      parent.setLeft(node);
    }
    else
    {
      parent.setRight(node);
    }
    fixAfterInsertion(node);
    m_size++;
    return new ModifiedNode(true, node);
  }

  @Override
  public boolean addAll(Collection<? extends T> values)
  {
    boolean modified = false;
    for (T payload : values)
    {
      if (add(payload))
      {
        modified = true;
      }
    }
    return modified;
  }
  
  @Override
  public boolean add(T e)
  {
    ModifiedNode put = put(e);
    return put.isModified();
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public boolean remove(Object payLoad)
  {
    return delete((T)payLoad).isModified();
  }

  public ModifiedNode delete(T value)
  {
    N node = getNode(value);
    if (node != null)
    {
      deleteNode(node);
      return new ModifiedNode(true, node);
    }
    return new ModifiedNode(false, node);
  }

  public N getNode(T value)
  {
    if (m_root == null)
    {
      return null;
    }
    N t = m_root;
    while (t != null)
    {
      int cmp = compare(value, t);
      if (cmp == 0)
      {
        return t;
      }
      if (cmp < 0)
      {
        t = t.getLeft();
      }
      else
      {
        t = t.getRight();
      }
    }
    return null;
  }

  public N getCeilingNode(T payload)
  {
    N p = m_root;
    while (p != null)
    {
      int cmp = compare(payload, p.getPayload());
      if (cmp < 0)
      {
        if (p.getLeft() != null)
          p = p.getLeft();
        else
          return p;
      }
      else if (cmp > 0)
      {
        if (p.getRight() != null)
        {
          p = p.getRight();
        }
        else
        {
          N parent = p.getParent();
          N ch = p;
          while (parent != null && ch == parent.getRight())
          {
            ch = parent;
            parent = parent.getParent();
          }
          return parent;
        }
      }
      else
        return p;
    }
    return null;
  }

  public N getFloorNode(T payload)
  {
    N p = m_root;
    while (p != null)
    {
      int cmp = compare(payload, p.getPayload());
      if (cmp > 0)
      {
        if (p.getRight() != null)
          p = p.getRight();
        else
          return p;
      }
      else if (cmp < 0)
      {
        if (p.getLeft() != null)
        {
          p = p.getLeft();
        }
        else
        {
          N parent = p.getParent();
          N ch = p;
          while (parent != null && ch == parent.getLeft())
          {
            ch = parent;
            parent = parent.getParent();
          }
          return parent;
        }
      }
      else
        return p;
    }
    return null;
  }

  public N getHigherNode(T payLoad)
  {
    N p = m_root;
    while (p != null)
    {
      int cmp = compare(payLoad, p.getPayload());
      if (cmp < 0)
      {
        if (p.getLeft() != null)
          p = p.getLeft();
        else
          return p;
      }
      else
      {
        if (p.getRight() != null)
        {
          p = p.getRight();
        }
        else
        {
          N parent = p.getParent();
          N ch = p;
          while (parent != null && ch == parent.getRight())
          {
            ch = parent;
            parent = parent.getParent();
          }
          return parent;
        }
      }
    }
    return null;
  }

  public N getLowerNode(T payload)
  {
    N p = m_root;
    while (p != null)
    {
      int cmp = compare(payload, p.getPayload());
      if (cmp > 0)
      {
        if (p.getRight() != null)
          p = p.getRight();
        else
          return p;
      }
      else
      {
        if (p.getLeft() != null)
        {
          p = p.getLeft();
        }
        else
        {
          N parent = p.getParent();
          N ch = p;
          while (parent != null && ch == parent.getLeft())
          {
            ch = parent;
            parent = parent.getParent();
          }
          return parent;
        }
      }
    }
    return null;
  }

  private N pollFirstNode()
  {
    N p = getFirstNode();
    if (p != null)
      deleteNode(p);
    return p;
  }

  private N pollLastNode()
  {
    N p = getLastNode();
    if (p != null)
      deleteNode(p);
    return p;
  }

  private int compare(N a, N b)
  {
    return compare(a.getPayload(), b);
  }

  public int compare(T a, N b)
  {
    return m_comparator.compare(a, b.getPayload());
  }

  public int compare(T a, T b)
  {
    return m_comparator.compare(a, b);
  }

  @Override
  public Iterator<T> iterator()
  {
    return Iterables.transform(new NodeIterable<>(false), GET_PAYLOAD).iterator();
  }
  
  @Override
  public Iterator<T> modifiableIterator()
  {
    return Iterables.transform(new NodeIterable<>(true), GET_PAYLOAD).iterator();
  }

  @Override
  public Iterable<ImmutableRedBlackNode<T>> immutableNodeIterable()
  {
    return new NodeIterable<>(false);
  }

  @Override
  public Iterator<ImmutableRedBlackNode<T>> immutableNodeIterator()
  {
    return new NodeIterable<>(false).iterator();
  }

  @Override
  public Iterable<N> nodeIterable()
  {
    return new NodeIterable<>(true);
  }

  @Override
  public Iterator<N> nodeIterator()
  {
    return new NodeIterable<N>(true).iterator();
  }
  
  @Override
  public int size()
  {
    return m_size;
  }

  @Override
  public void clear()
  {
    m_size = 0;
    m_root = null;
  }

  protected abstract N construct(T payload);

  @Override
  public Comparator<? super T> comparator()
  {
    return m_comparator;
  }

  @Override
  public T first()
  {
    N firstNode = getFirstNode();
    return firstNode == null ? null : firstNode.getPayload();
  }

  @Override
  public T last()
  {
    N lastNode = getLastNode();
    return lastNode == null ? null : lastNode.getPayload();
  }

  @Override
  public T lower(T payload)
  {
    N lowerNode = getLowerNode(payload);
    return lowerNode == null ? null : lowerNode.getPayload();
  }

  @Override
  public T floor(T payload)
  {
    N floorNode = getFloorNode(payload);
    return floorNode == null ? null : floorNode.getPayload();
  }

  @Override
  public T ceiling(T payload)
  {
    N ceilingNode = getCeilingNode(payload);
    return ceilingNode == null ? null : ceilingNode.getPayload();
  }

  @Override
  public T higher(T payload)
  {
    N higherNode = getHigherNode(payload);
    return higherNode == null ? null : higherNode.getPayload();
  }

  @Override
  public T pollFirst()
  {
    N pollFirstNode = pollFirstNode();
    return pollFirstNode == null ? null : pollFirstNode.getPayload();
  }

  @Override
  public T pollLast()
  {
    N pollLastNode = pollLastNode();
    return pollLastNode == null ? null : pollLastNode.getPayload();
  }

  @Override
  public NavigableSet<T> descendingSet()
  {
    return asTreeSet(Ordering.from(m_comparator).<T> reverse());
  }

  public TreeSet<T> asTreeSet()
  {
    return asTreeSet(m_comparator);
  }

  public TreeSet<T> asTreeSet(Comparator<? super T> comparator)
  {
    TreeSet<T> treeSet = new TreeSet<>(comparator);
    treeSet.addAll(this);
    return treeSet;
  }

  @Override
  public Iterator<T> descendingIterator()
  {
    return descendingSet().iterator();
  }

  @Override
  public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive)
  {
    return asTreeSet().subSet(fromElement, fromInclusive, toElement, toInclusive);
  }

  @Override
  public NavigableSet<T> headSet(T toElement, boolean inclusive)
  {
    return asTreeSet().headSet(toElement, inclusive);
  }

  @Override
  public NavigableSet<T> tailSet(T fromElement, boolean inclusive)
  {
    return asTreeSet().tailSet(fromElement, inclusive);
  }

  @Override
  public SortedSet<T> subSet(T fromElement, T toElement)
  {
    return subSet(fromElement, true, toElement, false);
  }

  @Override
  public SortedSet<T> headSet(T toElement)
  {
    return headSet(toElement, false);
  }

  @Override
  public SortedSet<T> tailSet(T fromElement)
  {
    return tailSet(fromElement, true);
  }
  
  /* (non-Javadoc)
   * @see com.metchevn.util.tree.redblack.ModifiableRedBlackTree#getRoot()
   */
  @Override
  public N getRoot()
  {
    return m_root;
  }
}
