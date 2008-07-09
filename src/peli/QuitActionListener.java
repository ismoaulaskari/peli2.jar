package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   QuitActionListener.java

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuitActionListener
    implements ActionListener
{

    public QuitActionListener()
    {
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        System.exit(0);
    }
}
