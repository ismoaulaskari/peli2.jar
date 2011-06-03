package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RunTournament.java

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Main class for invocation of tournament result program Peli.jar
 * Originally created by Hannu Pajunen,
 * feature-/bugfix update 1.1 by Ismo Aulaskari
 * v1.1 added arguments for opening a file from the command line
 * and a quick restart option "java -jar tournamentX.tnmt restart"
 * v1.15 headless mode to generate html from command line
 * v1.18 copy results from basic group to final group from command line
 * v1.20.1 more advanced previous group functionality
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
                    System.err.println("Usage: java -jar peli2.jar [tournamentfile.tnmt] [headless|pgcopy from.tnmt|pfgenerate from.tnmt finalgroupsize]");
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
                                PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(args[0])));
                                output.print(mixedTnmt);
                                output.flush();
                                output.close();
                            } catch (FileNotFoundException fe) {
                                System.err.println("Error " + fe);
                                System.exit(1);
                            } catch (IOException ie) {
                                System.err.println("Error " + ie);
                                System.exit(1);
                            }
                            System.exit(0);
                        }

                        if (args[1].equalsIgnoreCase("PFGENERATE")) {
                            System.err.println("Give final group size");
                            System.exit(1);
                        }
                    }

                    if (args.length == 4) {
                        //Make a group inheriting previous results and without pauses
                        //First the user creates normal final group, then we pgcopy results to it,
                        //remember those previous group results, generate the final group
                        //again in a special way without pauses and add the previous group results to it
                        if (args[1].equalsIgnoreCase("PFGENERATE")) {
                            int finalGroupSize = Integer.parseInt(args[3]);
                            if (finalGroupSize < 1) {
                                System.err.println("Bad final group size " + finalGroupSize);
                                System.exit(1);
                            }

                            File file = new File(args[2]);
                            System.setProperty("TournamentFileName", file.getName());
                            file = FileTools.canonize(file, ".tnmt");
                            if (!file.exists()) {
                                System.err.println("File " + file.getName() + " does not exist!");
                                System.exit(1);
                            }

                            //we need to get basic group standings but only the top
                            ArrayList<String> standings = null;
                            try {
                                Tournament tournament = new Tournament(file);
                                standings = tournament.getStandingsForPreviousToFinalGroupGeneration(finalGroupSize); //print, autoflush
                            } catch (IOException ex) {
                                System.err.print("Error " + ex);
                            } catch (FileFormatException ex) {
                                System.err.print("Error " + ex);
                            }

                            //we create an empty, normal final group
                            TreeSet<Player> treeset = new TreeSet<Player>(new PlayerComparator());
                            int rank = 0;
                            for (String name : standings) {
                                treeset.add(new Player(rank++, name));
                            }
                            Tournament normalFinalGroup = new Tournament(1, treeset);
                            file = new File(System.getProperty("TournamentFileArgs"));
                            System.setProperty("TournamentFileName", file.getName());
                            file = FileTools.canonize(file, ".tnmt");
                            if (file.exists()) {
                                System.err.println("File " + file.getName() + " already exists and we will not overwrite it!");
                                System.exit(1);
                            }
                            try {
                                PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                                normalFinalGroup.save(output, 2); //print tnmt, autoflush
                                output.flush();
                                output.close();

                                //get previous group results to normal final group
                                PreviousGroupCopier pgCopier = new PreviousGroupCopier();
                                List<String> oldGroupTnmt = FileTools.readFileAsList(args[2]);
                                List<String> newGroupTnmt = FileTools.readFileAsList(args[0]);
                                //get pg results in memory
                                String mixedTnmt = pgCopier.copyResultsFromPreviousGroup(oldGroupTnmt, newGroupTnmt);
                                output = new PrintWriter(new BufferedWriter(new FileWriter("pg" + args[0])));
                                output.print(mixedTnmt);
                                output.flush();
                                output.close();

                                //then we can create the pauseless final group
                                System.setProperty("PFGenerate", "true");
                                Tournament pauselessFinalGroup = new Tournament(1, treeset);
                                int players = pauselessFinalGroup.getOverAllStandings().size();
                                output = new PrintWriter(new BufferedWriter(new FileWriter(args[0])));
                                pauselessFinalGroup.save(output, 2); //print tnmt, autoflush
                                output.flush();
                                output.close();
                                System.setProperty("PFGenerate", "false");
                                //how do we get only the n/2 first rounds? let's do it in the copier

                                //then add those pg results to the pauseless group
                                List<String> finalGroupWithPgTnmt = FileTools.readFileAsList("pg" + args[0]);
                                List<String> pauselessFinalGroupTnmt = FileTools.readFileAsList(args[0]);
                                //get pg results in memory
                                String pauselessFinalGroupWithPgTnmt = pgCopier.copyResultsFromPreviousGroup(finalGroupWithPgTnmt, pauselessFinalGroupTnmt, players);
                                output = new PrintWriter(new BufferedWriter(new FileWriter(args[0])));
                                output.print(pauselessFinalGroupWithPgTnmt);
                                output.flush();
                                output.close();

                            } catch (FileNotFoundException fe) {
                                System.err.println("Error " + fe);
                                System.exit(1);
                            } catch (IOException ie) {
                                System.err.println("Error " + ie);
                                System.exit(1);
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
