/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author aulaskar
 */
public class CreatePlacementMatchListener implements ActionListener {

    private boolean plFirstRun;
    private boolean brFirstRun;
//    private String source;
    private JTabbedPane playoffpane;

    public CreatePlacementMatchListener(JTabbedPane playoffpane) {
        this.playoffpane = playoffpane;
        this.plFirstRun = true;
        this.brFirstRun = true;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("CREATEPLACEMENT") || ae.getActionCommand().equals("CREATEBRONZE")) {
            if (this.playoffpane != null) {
                JTabbedPane jpane = null;
                //String message = null;
                if (ae.getActionCommand().equals("CREATEPLACEMENT")) {
                    if ((!plFirstRun) && (!TournamentGUI.warnCreatePlacementMatches())) {
                        return; //confirm overwrite of playoffs
                    }                    //@TODO when loading saved tournament, firstrun should be off
                    //TournamentGUI.newPlacementMatchPane(this.playoffpane);
                    //jpanel = TournamentGUI.createPlacementMatchPanel();
                  //  message = Constants.getMessages().getString("placementMatches");
                    jpane = TournamentGUI.newPlacementMatches(this.playoffpane);
                    this.plFirstRun = false;
                } else if (ae.getActionCommand().equals("CREATEBRONZE")) {
                    if ((!brFirstRun) && (!TournamentGUI.warnCreateBronzeMatch())) {
                        return; //confirm overwrite of playoffs
                    }                    //@TODO when loading saved tournament, firstrun should be off
                    //TournamentGUI.newPlacementMatchPane(this.playoffpane);
                    //jpanel = TournamentGUI.createBronzeMatchPanel();
                    //message = Constants.getMessages().getString("bronzeMatch");
                    jpane = TournamentGUI.newBronzeMatch(this.playoffpane);
                    this.brFirstRun = false;
                }

                if (jpane == null) {
                    return;
                }

                this.playoffpane = jpane;
                //this.playoffpane.addTab(message, jpanel);
                this.playoffpane.setSelectedIndex(playoffpane.getTabCount() - 1);
            }
        } else {
            //           this.source = ae.getActionCommand(); //size?
        }
    }
}


