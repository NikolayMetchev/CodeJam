package com.metchevn.util.tree.binary;

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

public abstract class AbstractBinaryTree<T, N extends ModifiableBinaryTreeNode<T, N>> extends AbstractSet<T> implements ModifiableBinaryTree<T, N>, NavigableSet<T>
{
  private final class NodeIterable implements Iterable<N>
  {
    private final boolean m_mutable;
    public NodeIterable(boolean mutable)
    {
      m_mutable = mutable;
    }
    @Override
    public Iterator<N> iterator()
    {
      return new Iterator<N>()
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
        public N next()
        {
          m_node = m_node == null ? getFirstNode() : successor(m_node);
          if (m_node == null)
          {
            throw new NoSuchElementException();
          }
          return m_node;
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
  protected int m_size = 0;
  private final Function<ImmutableBinaryTreeNode<T>, T> GET_PAYLOAD = new Function<ImmutableBinaryTreeNode<T>, T>()
  {
    @Override
    public T apply(ImmutableBinaryTreeNode<T> input)
    {
      return input.getPayload();
    }
  };

  public AbstractBinaryTree(Comparator<? super T> comparator)
  {
    m_comparator = comparator;
  }

  protected static <T, N extends ModifiableBinaryTreeNode<T, N>> N parentOf(N p)
  {
    return (p == null ? null : p.getParent());
  }
  
  protected static <T, N extends ModifiableBinaryTreeNode<T, N>> N leftOf(N p)
  {
    return (p == null) ? null : p.getLeft();
  }

  protected static <T, N extends ModifiableBinaryTreeNode<T, N>> N rightOf(N p)
  {
    return (p == null) ? null : p.getRight();
  }
  
  /** From CLR */
  protected void rotateLeft(N p)
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
  protected void rotateRight(N p)
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

  public static <T, N extends ModifiableBinaryTreeNode<T, N>> N successor(N t)
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

  public static <T, N extends ModifiableBinaryTreeNode<T, N>> N predecessor(N t)
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
  
  @Override
  public void setRoot(N root)
  {
    m_root = root;
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
  
  protected abstract void fixAfterInsertion(N node);

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
  
  protected abstract void deleteNode(N node);

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
    return Iterables.transform(new NodeIterable(false), GET_PAYLOAD).iterator();
  }
  
  @Override
  public Iterator<T> modifiableIterator()
  {
    return Iterables.transform(new NodeIterable(true), GET_PAYLOAD).iterator();
  }

  @Override
  public Iterable<N> immutableNodeIterable()
  {
    return new NodeIterable(false);
  }

  @Override
  public Iterator<N> immutableNodeIterator()
  {
    return new NodeIterable(false).iterator();
  }

  @Override
  public Iterable<N> nodeIterable()
  {
    return new NodeIterable(true);
  }

  @Override
  public Iterator<N> nodeIterator()
  {
    return new NodeIterable(true).iterator();
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
  
  @Override
  public N getRoot()
  {
    return m_root;
  }
}
