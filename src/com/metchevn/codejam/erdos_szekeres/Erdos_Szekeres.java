package com.metchevn.codejam.erdos_szekeres;

import com.metchevn.codejam.Solver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Erdos_Szekeres extends Solver
{
  public Erdos_Szekeres(InputStream is, OutputStream os)
  {
    super(is, os);
  }

  @SuppressWarnings("resource")
  public static void main(String[] args) throws Exception
  {
    long start = System.currentTimeMillis();
    try
    (
      InputStream in = args.length >= 1 ? new FileInputStream(args[0]) :  System.in;
      OutputStream out = args.length == 2 ? new FileOutputStream(args[1]) : System.out;
      Erdos_Szekeres solver = new Erdos_Szekeres(in, out)
    )
    {
      solver.solve();
    }
    System.out.println("Done in " + (System.currentTimeMillis() - start) + " milliseconds");
  }

  @Override
  protected String solve(long caseNumber) throws Exception
  {
    int N = parseInt();
    int[] A = new int[N];
    int[] B = new int[N];
    int[] X = new int[N];

    for (int i = 0; i < N; i++)
    {
      A[i] = parseInt();
    }
    boolean found1 = false;
    for (int i = 0; i < N; i++)
    {
      B[i] = parseInt();
      if (!found1 && B[i] == 1)
      {
        found1 = true;
        X[i] = 1;
      }
    }
//    System.out.println();
//    System.out.println("Case #" + caseNumber +": " +toString(A));
//    System.out.println("Case #" + caseNumber +": " +toString(B));
    Mathser mathser = new Mathser(N, A, B, X);
    int[] solve = mathser.solve(2);
    StringBuilder ans = toString(solve);
    return ans.toString();
  }

  static class Mathser
  {
    final int N;
    final int[] A;
    final int[] B;
    final int[] X;
    private Mathser(int n, int[] a, int[] b, int[] x)
    {
      N = n;
      A = a;
      B = b;
      X = x;
    }
    public int[] solve(int value)
    {
      //System.out.println("Solving " + value);
      if (value > N) return X;
      
      for(int i = 0; i < N; i++)
      {
        if (X[i] == 0 && value >= Math.max(A[i], B[i]))
        {
          boolean valid = true;
          for(int j = 0; j<i;j++)
          {
            if (A[j] == A[i] && X[j] != 0)
            {
              valid = false;
              break;
            }
            if (B[j] == B[i] && X[j] == 0)
            {
              valid = false;
              break;
            }
          }
          if (!valid)
          {
            continue;
          }
          
          for (int j = i+1; j < N;j++)
          {
            if (A[j] == A[i] && X[j] == 0)
            {
              valid = false;
              break;
            }
            if (B[j] == B[i] && X[j] != 0)
            {
              valid = false;
              break;
            }
          }
          if (!valid)
          {
            continue;
          }
          
          valid = set(i, value);
          if (valid)
          {
            int[] solve = solve(value + 1);
            if (solve != null)
            {
              return solve;
            }
          }
          unset(i);
        }
      }
      
      return null;
    }
    
    private boolean set(int i, int value)
    {
      X[i] = value;
      
      int maxA = 0;
      for (int j = 0; j < i; j++)
      {
        if (X[j] != 0)
        {
          maxA = Math.max(maxA, A[j]);
        }
      }

      int maxB = 0;
      for (int j = i+1; j < N; j++)
      {
        if (X[j] != 0)
        {
          maxB = Math.max(maxB, B[j]);
        }
      }
      
      return maxA + 1 == A[i] && maxB + 1 == B[i];
    }
    
    private void unset(int i)
    {
      X[i] = 0;
    }
  }
}
