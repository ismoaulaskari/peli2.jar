/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTabbedPane;

/**
 *
 * @author aulaskar
 */
public class CreatePlayoffListener implements ActionListener {

    private String source;
    private JTabbedPane playoffpane;

    public CreatePlayoffListener(JTabbedPane playoffpane) {
        this.playoffpane = playoffpane;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("CREATE") || ae.getActionCommand().equals("CREATENEXT")) {
            if (this.playoffpane != null) {
                String size = source;
                if (this.source == null) {
                    size = "2"; //default
                }
                if (ae.getActionCommand().equals("CREATE")) {
                    if(! TournamentGUI.warnCreatePlayoff()) return; //confirm overwrite of playoffs
                    
                    this.playoffpane.removeAll();
                }
                this.playoffpane.addTab("Play" + size, TournamentGUI.createPlayoffPanel(Integer.parseInt(size)));
            }
        } else {
            this.source = ae.getActionCommand(); //size?
        }
    }

    public void setSource(String command) {
        this.source = command;
    }
}


