package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RegistrationGUI.java

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

class LineReader
    implements Iterator
{

    LineReader(String s)
    {
        hasNext = false;
        try
        {
            in = new BufferedReader(new FileReader(s));
            currentLine = getNextNonEmptyLine(in);
            if(currentLine != null)
                hasNext = true;
        }
        catch(Exception exception) { }
    }

    private String getNextNonEmptyLine(BufferedReader bufferedreader)
    {
        try
        {
            for(String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine())
            {
                s = s.trim();
                if(!s.equals(""))
                    return s;
            }

        }
        catch(Exception exception) { }
        return null;
    }

    public boolean hasNext()
    {
        return hasNext;
    }

    public Object next()
    {
        String s = currentLine;
        try
        {
            currentLine = getNextNonEmptyLine(in);
            if(currentLine == null)
            {
                hasNext = false;
                in.close();
            }
        }
        catch(Exception exception)
        {
            hasNext = false;
        }
        return s;
    }

    public void remove()
    {
    }

    private BufferedReader in;
    private String currentLine;
    private boolean hasNext;
}
