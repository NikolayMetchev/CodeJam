package com.metchevn.util.tree.redblack;

import static com.metchevn.util.tree.redblack.Colour.BLACK;
import static com.metchevn.util.tree.redblack.Colour.RED;

import com.metchevn.util.tree.binary.AbstractBinaryTree;

import java.util.Comparator;

public abstract class AbstractRedBlackTree<T, N extends ModifiableRedBlackNode<T, N>> extends AbstractBinaryTree<T, N> implements ModifiableRedBlackTree<T, N>
{
  public AbstractRedBlackTree(Comparator<? super T> comparator)
  {
    super(comparator);
  }

  protected static <T, N extends ModifiableRedBlackNode<T, N>> boolean colorOf(N p)
  {
    return (p == null ? BLACK : p.getColour());
  }

  protected static <T, N extends ModifiableRedBlackNode<T, N>> void setColor(N p, boolean c)
  {
    if (p != null)
    {
      p.setColour(c);
    }
  }

  /** From CLR */
  @Override
  protected void fixAfterInsertion(N x)
  {
    x.setColour(RED);
    while (x != null && x != getRoot() && x.getParent().getColour() == RED)
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
    getRoot().setColour(BLACK);
  }

  /**
   * Delete node p, and then rebalance the tree.
   */
  @Override
  protected void deleteNode(N p)
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
        setRoot(replacement);
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
      setRoot(null);
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
    while (x != getRoot() && colorOf(x) == BLACK)
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
          x = getRoot();
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
          x = getRoot();
        }
      }
    }
    setColor(x, BLACK);
  }
}
