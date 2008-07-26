package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RadioListener.java

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RadioListener
    implements ActionListener
{

    public RadioListener()
    {
        times = 1;
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        times = Integer.parseInt(actionevent.getActionCommand());
    }

    public int getTimes()
    {
        return times;
    }

    private int times;
}
