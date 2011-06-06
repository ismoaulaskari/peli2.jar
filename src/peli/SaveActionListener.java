package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SaveActionListener.java

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ResourceBundle;
import java.util.Timer;
import javax.swing.JOptionPane;

/**
 * handle the main GUI toolbar buttons
 * @author aulaskar
 *
 */
public class SaveActionListener
        implements ActionListener {

    private Tournament tournament;
    private File file;
    private int what;
    private Component frame;
    private ResourceBundle messages;
    private ResourceBundle rules;
    private LiveResults liveResults;
    private boolean doLiveResults = false;
    private Timer timer = null;

    SaveActionListener(File file1, Tournament tournament1, int i, Component component) {
        tournament = tournament1;
        file = file1;
        what = i;
        frame = component;
        messages = Constants.getInstance().getMessages();
        rules = Constants.getInstance().getRules();
        if (rules.getString("postLiveResultsToWeb").equalsIgnoreCase("true") && what == 2) {
            this.doLiveResults = true;
            this.timer = new Timer();
        }

    }

    public void actionPerformed(ActionEvent actionevent) {
        String s;
        switch (what) {
            case 0: // '\0'
                s = messages.getString("table.html");
                break;

            case 1: // '\001'
                s = messages.getString("matches.html");
                break;

            case 2: // '\002'
                s = "";
                break;

            case 3: // '\003'
                s = messages.getString(".html");
                break;

            case 4:
                s = messages.getString("standings.txt");
                break;

            case 5:
                s = messages.getString("byplayer.html");
                break;

//            case 6:
//                s = messages.getString("divisionstandings.txt");
//                break;

            default:
                s = "";
                break;
        }
        try {
            PrintWriter printwriter = new PrintWriter(new BufferedWriter(new FileWriter(file.getName() + s)));
            tournament.save(printwriter, what);
            printwriter.close();
            String savedMessage = getTargetType(what) + " " + messages.getString("wasSaved") + " " + file.getName() + s;
            if (what == 2) {
                SaveTracker.setIsSaved(true);
                //liveresults
                if (doLiveResults) {                    
                    StringWriter stringwriter = new StringWriter();
                    PrintWriter sprintwriter = new PrintWriter(stringwriter, true);
                    Tournament tmpTournament = null;
                    if(rules.getString("lowMem").equalsIgnoreCase("TRUE")) {
                        tmpTournament = tournament;
                    }
                    else {
                        try {
                            tmpTournament = new Tournament(file);
                        } catch (FileFormatException ex) {
                           System.err.print("Can't send live results, try option lowMem=true Error " + ex);
                        }
                    }
                    tmpTournament.save(sprintwriter, 3); //print html, autoflush
                    liveResults = new LiveResults(file.getName() + ".html", stringwriter.toString());
                    timer.schedule(liveResults, 0);
                    sprintwriter.close();
                    //byplayer results next:
                    StringWriter stringwriter2 = new StringWriter();
                    PrintWriter sprintwriter2 = new PrintWriter(stringwriter2, true);
                    tmpTournament.save(sprintwriter2, 5); //print html, autoflush
                    liveResults = new LiveResults(file.getName() + ".byplayer.html", stringwriter2.toString());
                    timer.schedule(liveResults, 5);
                    sprintwriter2.close();
                    Thread.yield();
                    savedMessage += System.getProperty("line.separator") + messages.getString("liveResultsSentTo") + " " + rules.getString("websiteUrl");
                }
            }
            popUpMessage(savedMessage, frame);
        } catch (IOException ioexception) {
            System.err.print(ioexception);
        }
    }

    private String getTargetType(int i) {
        switch (i) {
            case 0: // '\0'
                return messages.getString("seriesTable");

            case 1: // '\001'
                return messages.getString("programme");

            case 2: // '\002'
                return messages.getString("tournamentStatus");

            case 3: // '\003'
                return messages.getString("tournamentWebPage");

            case 4:
                return messages.getString("tournamentStandings");

            case 5:
                return messages.getString("programmeByPlayer");

        }
        return "";
    }

    private void popUpMessage(String s, Component component) {
        JOptionPane.showMessageDialog(component, s, messages.getString("fileCreated"), 1, null);
    }
}
