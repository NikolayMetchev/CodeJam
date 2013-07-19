package com.metchevn.codejam.cheaters;

import com.metchevn.codejam.Solver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Cheaters extends Solver
{
  private static boolean DEBUG = true;

  public Cheaters(InputStream is, OutputStream os)
  {
    super(is, os, DEBUG);
  }

  public static void main(String[] args) throws Exception
  {
    long start = System.currentTimeMillis();
    try
    (
      InputStream in = args.length >= 1 ? new FileInputStream(args[0]) :  System.in;
      OutputStream out = args.length == 2 ? new FileOutputStream(args[1]) : System.out;
      Cheaters solver = new Cheaters(in, out)
    )
    {
      solver.solve();
    }
    System.out.println("Done in " + (System.currentTimeMillis() - start) + " milliseconds");

  }

  @Override
  protected String solve(long caseNumber) throws Exception
  {
    long B = parseLong();
    int N = parseInt();
    if (DEBUG) System.out.println(B + " " + N);
    long[] bets = new long[N];
    long minBet = Long.MAX_VALUE;
    int numberOfMinBets = 0;
    for(int i = 0; i < N;i++)
    {
      bets[i] = parseLong();
      if (DEBUG)  System.out.print(bets[i]+ " ");
      if (bets[i] < minBet)
      {
        numberOfMinBets = 1;
        minBet = bets[i];
      }
      else if (minBet == bets[i])
      {
        numberOfMinBets++;
      }
    }
    if (DEBUG) System.out.println();
    
    int remainingSlots = 37 - N;
    long actualBet;
    double actualNumberOfSlots;
    double winningSlots;
    double extraCost = 0;
    if (remainingSlots == 0)
    {
      actualBet = B / numberOfMinBets;
      actualNumberOfSlots = B / actualBet;
      winningSlots = numberOfMinBets;
    }
    else
    {
      if (minBet == 1)
      {
        if (B - numberOfMinBets >= remainingSlots)
        {
          B -= numberOfMinBets;
          minBet++;
          extraCost = numberOfMinBets;
        }
      }
      long maxBet = Math.max(B / remainingSlots, 1);
      actualBet = minBet == 1 ? 1 : Math.min(maxBet, minBet - 1);
      long maxNumberOfSlots = B / actualBet;
      actualNumberOfSlots = Math.min(remainingSlots, maxNumberOfSlots);
      winningSlots = minBet == actualBet ? remainingSlots + numberOfMinBets : remainingSlots;
    }
    return Double.toString(Math.max(actualBet * actualNumberOfSlots * (36d / winningSlots - 1) - extraCost,0d));
  }
}
