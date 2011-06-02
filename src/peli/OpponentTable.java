package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Mutual.java

import java.util.*;

/**
 * Routines for the keeping and display of mutual matches 
 * (keskinäiset ottelut)
 * @author aulaskar
 *
 */
class OpponentTable
{
    
    private Hashtable<String,Vector<Match>> opponentTable;
    
    OpponentTable()
    {
        opponentTable = new Hashtable<String,Vector<Match>>();
    }

    public void put(String s, Match match)
    {
        if(!opponentTable.containsKey(s))
            opponentTable.put(s, new Vector<Match>());
        Vector<String> vector = (Vector)opponentTable.get(s);
        if(s.equals(match.home()))
        {
            vector.add(match.getResultInverted());
            return;
        }
        if(s.equals(match.visitor()))
            vector.add(match.getResult());
    }

    public String getResult(String s)
    {
        if(!opponentTable.containsKey(s))
            return "&nbsp;";
        Vector<String> vector = (Vector)opponentTable.get(s);
        Enumeration<String> enumeration = vector.elements();
        String s1 = "";
        do
        {
            s1 = s1 + (String)enumeration.nextElement();
            if(enumeration.hasMoreElements())
                s1 = s1 + "<BR>";
            else
                return s1;
        } while(true);
    }

    //for mutual ordering
    public Enumeration<String> getNativeResult(String s)
    {
        if(!opponentTable.containsKey(s))
            return null;
        Vector<String> vector = (Vector)opponentTable.get(s);
        Enumeration<String> enumeration = vector.elements();

        return enumeration;
    }



}
