package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RunTournament.java

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

/**
 * Main class for invocation of tournament result program Peli.jar
 * Originally created by Hannu Pajunen,
 * feature-/bugfix update 1.1 by Ismo Aulaskari
 * v1.1 added arguments for opening a file from the command line
 * and a quick restart option "java -jar tournamentX.tnmt restart"
 * HACK HACK HACK away.. 
 * @author aulaskar
 *
 */
public class RunTournament
{

    public RunTournament()
    {
    }

    public static void main(String args[])
    {
    	
        MainWindow mainwindow = new MainWindow();
        mainwindow.pack();
        Tools.center(mainwindow);
        mainwindow.setVisible(true);
        
        /* added to open a tnmt from commandline*/
    	try {       		
          if(args.length > 0)
    		System.setProperty("TournamentFileArgs", args[0]);
    	} catch (SecurityException e) {
    		System.err.println("Setting properties from commandline args was denied.");
    	}
    	
        /* if restart is called, open specified old tournament and go straight to the GUI */
        if(args.length == 2)
        	if(args[1].equalsIgnoreCase("RESTART"))
        		new OldActionListener(mainwindow).actionPerformed(new ActionEvent(mainwindow, ActionEvent.ACTION_FIRST, "restart"));
        
    }
}
