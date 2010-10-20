package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ReadyActionListener.java

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;

public class ReadyActionListener
    implements ActionListener
{
    private MainWindow mainWindow;
    private Container contents;
    private TreeSet players;
    private TreeSet playerCheckBoxes;
    private File saveFile;
    private RadioListener radioListener;

    public void actionPerformed(ActionEvent actionevent)
    {
        for(Iterator iterator = playerCheckBoxes.iterator(); iterator.hasNext();)
        {
            PlayerJCheckBox playerjcheckbox = (PlayerJCheckBox)iterator.next();
            if(playerjcheckbox.isSelected())
                players.add(new Player(playerjcheckbox.getRank(), Tools.limitNameToTwoParts(playerjcheckbox.getText())));
        }

        int i = radioListener.getTimes();
        contents.removeAll();
        contents.add(new TournamentGUI(mainWindow, i, players, saveFile), "Center");
        contents.validate();
        mainWindow.pack();
        Tools.center(mainWindow);
        contents.repaint();
    }

    ReadyActionListener(MainWindow mainwindow, TreeSet treeset, File file, RadioListener radiolistener)
    {
        players = new TreeSet(new PlayerComparator());
        mainWindow = mainwindow;
        contents = mainwindow.getContentPane();
        playerCheckBoxes = treeset;
        saveFile = file;
        radioListener = radiolistener;
    }

}
