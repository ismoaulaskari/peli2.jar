package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Tools.java

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;

public class Tools
{

    public Tools()
    {
    }

    public static String readSuffix(BufferedReader bufferedreader, String s)
        throws FileFormatException, IOException
    {
        String s1 = bufferedreader.readLine();
        if(s1.startsWith(s))
            return s1.substring(s1.indexOf(':') + 1);
        else
            throw new FileFormatException();
    }

    public static boolean readString(BufferedReader bufferedreader, String s)
        throws IOException
    {
        String s1 = bufferedreader.readLine();
        return s1.equals(s);
    }

    public static String format(String s, int i)
    {
        int j = s.length();
        if(j > i)
            return s.substring(0, i);
        for(int k = j; k < i; k++)
            s = s + " ";

        return s;
    }

    public static String format(int i, int j)
    {
        String s = "" + i;
        int k = s.length();
        if(k > j)
            return s;
        for(int l = k; l < j; l++)
            s = " " + s;

        return s;
    }

    public static void center(Component component)
    {
        Dimension dimension = component.getToolkit().getScreenSize();
        Dimension dimension1 = component.getSize();
        component.setLocation((dimension.width - dimension1.width) / 2, (dimension.height - 30 - dimension1.height) / 2);
    }

    public static int parseIntAfter(String s, String s1)
        throws FileFormatException
    {
        try
        {
            if(!s1.startsWith(s))
                throw new FileFormatException();
            else
                return Integer.parseInt(s1.substring(s.length()));
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new FileFormatException();
        }
        catch(NullPointerException nullpointerexception)
        {
            throw new FileFormatException();
        }
    }

    public static String parseStringAfter(String s, String s1)
        throws FileFormatException
    {
        try
        {
            if(!s1.startsWith(s))
                throw new FileFormatException();
            else
                return s1.substring(s.length());
        }
        catch(NullPointerException nullpointerexception)
        {
            throw new FileFormatException();
        }
    }

    public static String makeInitials(String s)
    {
        String s1 = s.substring(0, 1) + ".";
        int i = s.indexOf(" ");
        if(i > 0)
            s1 = s1 + s.substring(i + 1, i + 2) + ".";
        return s1;
    }

    public static String fixName(String s) {
        String s1 = s.trim();
        int i = s1.indexOf(' ');
        if (i < 0) {
            return capitalize(s1.trim(), true);
        } else {
            String s2 = s1.substring(0, i).trim();
            String s3 = s1.substring(i + 1).trim();
            return capitalize(s2, true) + " " + capitalize(s3, false);
        }
    }

    public static String getNumber(String s) {
        s = s.replaceAll("[\\D]", "");

        return s;
    }

    /*fixed A-K bug  */
    public static String capitalize(String s, boolean strict) {
        //StringBuffer stringbuffer = new StringBuffer(s.toLowerCase());
        StringBuffer stringbuffer = new StringBuffer(s);
        stringbuffer.replace(0, 1, s.substring(0, 1).toUpperCase());
        int i = s.indexOf('-');
        if (strict == true && i > 0) //strict by aulaskar
        {
            stringbuffer = stringbuffer.replace(i + 1, i + 2, s.substring(i + 1, i + 2).toUpperCase());
        }
        return stringbuffer.toString();
    }

}
