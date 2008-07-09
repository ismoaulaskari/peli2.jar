package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Mutual.java

import java.util.*;

/** handle individual matches for each player
 * 
 * @author aulaskar
 *
 */
public class Mutual
{

    Mutual(TreeSet treeset)
    {
        players = new Hashtable();
        String s;
        for(Iterator iterator = treeset.iterator(); iterator.hasNext(); players.put(s, new OpponentTable()))
            s = ((Player)iterator.next()).getName();

    }

    public void insert(Match match)
    {
        try
        {
            OpponentTable opponenttable = (OpponentTable)players.get(match.home());
            OpponentTable opponenttable1 = (OpponentTable)players.get(match.visitor());
            opponenttable.put(match.visitor(), match);
            opponenttable1.put(match.home(), match);
        }
        catch(NullPointerException nullpointerexception) { }
    }

    public String getResult(String s, String s1)
    {
        OpponentTable opponenttable = (OpponentTable)players.get(s);
        return opponenttable.getResult(s1);
    }

    public Enumeration getNativeResult(String s, String s1)
    {
        OpponentTable opponenttable = (OpponentTable)players.get(s);
        return opponenttable.getNativeResult(s1);
    }

    public int size() {
    	return players.size();
    }
    
    private Hashtable players;
}
