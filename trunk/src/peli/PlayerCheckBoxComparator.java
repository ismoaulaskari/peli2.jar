package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RegistrationGUI.java

import java.io.Serializable;
import java.util.Comparator;

/* for comparison in the choose players-screen */
class PlayerCheckBoxComparator
    implements Comparator, Serializable
{

    PlayerCheckBoxComparator()
    {
    }

    public int compare(Object obj, Object obj1)
    {
        int i = ((PlayerJCheckBox)obj).getRank();
        int j = ((PlayerJCheckBox)obj1).getRank();
        return i - j;
    }
}
