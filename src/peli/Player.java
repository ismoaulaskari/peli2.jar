package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Player.java

/** 
 * a player has only a name and rank 
 */
public class Player
{

    private int rank;
    private String name;

    Player(int i, String s)
    {
        rank = 0;
        name = "";
        rank = i;
        name = s;
    }

    public int getRank()
    {
        return rank;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return name;
    }

    public boolean lt(PlayerJCheckBox playerjcheckbox)
    {
        return rank < playerjcheckbox.getRank();
    }

    public boolean gt(PlayerJCheckBox playerjcheckbox)
    {
        return rank > playerjcheckbox.getRank();
    }

    public boolean eq(PlayerJCheckBox playerjcheckbox)
    {
        return rank == playerjcheckbox.getRank();
    }

}
