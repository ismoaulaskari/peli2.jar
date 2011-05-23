package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RunTournament.java

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Main class for invocation of tournament result program Peli.jar
 * Originally created by Hannu Pajunen,
 * feature-/bugfix update 1.1 by Ismo Aulaskari
 * v1.1 added arguments for opening a file from the command line
 * and a quick restart option "java -jar tournamentX.tnmt restart"
 * v1.15 headless mode to generate html from command line
 * HACK HACK HACK away.. 
 * @author aulaskar
 *
 */
public class RunTournament {

    public RunTournament() {
    }

    public static void main(String args[]) {
        boolean headless = false;

        /* added to open a tnmt from commandline*/
        try {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("/h")) {
                    System.err.println("Usage: java -jar peli2.jar [tournamentfile.tnmt] [headless|pgcopy from.tnmt]");
                    System.exit(1);
                }

                System.setProperty("TournamentFileArgs", args[0]);
                
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("HEADLESS")) {
                        headless = true;
                    }
                } else {
                    /* copy previous group results to current group */
                    if (args.length == 3) {
                        if (args[1].equalsIgnoreCase("PGCOPY")) {
                            PreviousGroupCopier pgCopier = new PreviousGroupCopier();
                            try {
                                List<String> oldGroupTnmt = FileTools.readFileAsList(args[2]);
                                List<String> newGroupTnmt = FileTools.readFileAsList(args[0]);
                                String mixedTnmt = pgCopier.copyResultsFromPreviousGroup(oldGroupTnmt, newGroupTnmt);
                                System.out.println(mixedTnmt);
                            } catch (FileNotFoundException fe) {
                                System.err.println("Error " + fe);
                            } catch (IOException ie) {
                                System.err.println("Error " + ie);
                            }
                            System.exit(0);
                        }
                    }                    
                }
            }
        } catch (SecurityException e) {
            System.err.println("Setting properties from commandline args was denied.");
        }


        if (headless) { //limited headless operation
            File file = new File(System.getProperty("TournamentFileArgs"));
            System.setProperty("TournamentFileName", file.getName());
            file = FileTools.canonize(file, ".tnmt");
            if (!file.exists()) {
                System.err.println("File " + file.getName() + " does not exist!");
                System.exit(1);
            }
            try {
                Tournament tournament = new Tournament(file);
                PrintWriter output = new PrintWriter(System.out, true);
                tournament.save(output, 3); //print html, autoflush
                output.flush();
                output.close();
            } catch (IOException ex) {
                System.err.print("Error " + ex);
            } catch (FileFormatException ex) {
                System.err.print("Error " + ex);
            }
        } else { //normal operation with Swing gui
            MainWindow mainwindow = new MainWindow();
            mainwindow.pack();
            Tools.center(mainwindow);
            mainwindow.setVisible(true);

            if (args.length == 2) {
                /* if restart is called, open specified old tournament and go straight to the GUI */
                if (args[1].equalsIgnoreCase("RESTART")) {
                    new OldActionListener(mainwindow).actionPerformed(new ActionEvent(mainwindow, ActionEvent.ACTION_FIRST, "restart"));
                }
            }
        }
    }
}
