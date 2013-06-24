package com.metchevn.codejam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;

public abstract class Solver implements AutoCloseable
{
  private final Reader m_reader;
  private final StreamTokenizer m_st;
  private final Writer m_writer;
  protected final boolean m_log;
  
  public Solver(InputStream is, OutputStream os)
  {
    this(is, os, false);
  }
  
  public Solver(InputStream is, OutputStream os, boolean log)
  {
    m_log = log;
    m_reader = new BufferedReader(new InputStreamReader(is));
    m_writer = new BufferedWriter(new OutputStreamWriter(os));
    m_st = new StreamTokenizer(m_reader);
    m_st.eolIsSignificant(false);
    m_st.parseNumbers();
  }
  
  public void solve() throws Exception
  {
    long cases = parseInt();
    for(long i = 1 ; i <= cases; i++)
    {
      String ans = solve(i);
      String str = "Case #" + i + ": " + ans+"\n";
      m_writer.write(str);
      if (m_log)
      {
        System.out.print(str);
      }
    }
  }
  
  protected abstract String solve(long caseNumber) throws Exception;

  public int parseInt() throws IOException
  {
    int token = m_st.nextToken();
    if (token != StreamTokenizer.TT_NUMBER)
    {
      throw new RuntimeException("Not a number " +m_st.sval);
    }
    return (int) m_st.nval;
  }

  
  public long parseLong() throws IOException
  {
    int token = m_st.nextToken();
    if (token != StreamTokenizer.TT_NUMBER)
    {
      throw new RuntimeException("Not a number " +m_st.sval);
    }
    return (long) m_st.nval;
  }

  @Override
  public void close() throws IOException 
  {
    m_writer.close();
    m_reader.close();
  }

  protected static StringBuilder toString(int[] solve)
  {
    StringBuilder ans = new StringBuilder(Integer.toString(solve[0]));
    for (int i = 1; i < solve.length; i++)
    {
      ans.append(" ");
      ans.append(Integer.toString(solve[i]));
    }
    return ans;
  }
}
