package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PlayerComparator.java

import java.io.Serializable;
import java.util.Comparator;

/**
 * compare players just by rank(ing)
 * @author aulaskar
 *
 */
public class PlayerComparator
    implements Comparator, Serializable
{

    public PlayerComparator()
    {
    }

    public int compare(Object obj, Object obj1)
    {
        int i = ((Player)obj).getRank();
        int j = ((Player)obj1).getRank();
        return i - j;
    }
}
