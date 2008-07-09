package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OldActionListener.java

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Open an existing tournament
 * v1.1 added filenames from commandline
 * @author aulaskar
 *
 */
public class OldActionListener
    implements ActionListener
{

    public void actionPerformed(ActionEvent actionevent)
    {
    	if(System.getProperty("TournamentFileArgs") != null) {
    		file = new File(System.getProperty("TournamentFileArgs"));
    		file = FileTools.canonize(file, ".tnmt");
    	} 
    	else {
    		file = FileTools.askFileName("Uuden turnaustiedoston nimi?", contents, new TournamentFileFilter());
    		file = FileTools.canonize(file, ".tnmt");
    	}
    		
        if(!file.exists())
        {
            popUpMessage("Tiedostoa " + file.getName() + " ei ole!", "No such file", mainWindow);
            return;
        }
        try
        {
            Tournament tournament = new Tournament(file);
            contents.removeAll();
            contents.add(new TournamentGUI(mainWindow, tournament, file), "Center");
            contents.validate();
            mainWindow.pack();
            Tools.center(mainWindow);
            contents.repaint();
        }
        catch(IOException ioexception)
        {
            popUpMessage("Tiedoston " + file.getName() + " avaamisessa on jokin ongelma!", "File open error", mainWindow);
        }
        catch(FileFormatException fileformatexception)
        {
            popUpMessage("Tiedosto " + file.getName() + " on viallinen.", "File Format Error", mainWindow);
        }
    }

    OldActionListener(MainWindow mainwindow)
    {
        mainWindow = mainwindow;
        contents = mainWindow.getContentPane();
    }

    private void popUpMessage(String s, String s1, Component component)
    {
        JOptionPane.showMessageDialog(component, s, s1, 0, null);
    }

    private MainWindow mainWindow;
    private Container contents;
    private File file;
}
