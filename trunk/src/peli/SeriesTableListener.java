package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SeriesTableListener.java

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SeriesTableListener
    implements ChangeListener
{

    SeriesTableListener(SeriesTableModel seriestablemodel)
    {
        stm = seriestablemodel;
    }

    public void stateChanged(ChangeEvent changeevent)
    {
        stm.update();
    }

    private SeriesTableModel stm;
}
