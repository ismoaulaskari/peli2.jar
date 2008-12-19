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
public class CreatePlayoffListener implements ActionListener {

    private boolean firstRun;
    private String source;
    private JTabbedPane playoffpane;

    public CreatePlayoffListener(JTabbedPane playoffpane) {
        this.playoffpane = playoffpane;
        this.firstRun = true;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("CREATE") || ae.getActionCommand().equals("CREATENEXT")) {
            if (this.playoffpane != null) {
                String size = source;
                if (this.source == null) {
                    size = "8"; //default
                }
                if (ae.getActionCommand().equals("CREATE")) {
                    if((! firstRun) && (! TournamentGUI.warnCreatePlayoff())) return; //confirm overwrite of playoffs
                    
                    TournamentGUI.newPlayoffpane(this.playoffpane);
                }
                
                JPanel jpanel = TournamentGUI.createPlayoffPanel(Integer.parseInt(size));
                if(jpanel == null) {
                    return;
                }
                
                this.firstRun = false;
                this.playoffpane.addTab("Play" + size, jpanel);
                this.playoffpane.setSelectedIndex(playoffpane.getTabCount() - 1);
            }
        } else {
            this.source = ae.getActionCommand(); //size?
        }
    }

    public void setSource(String command) {
        this.source = command;
    }
}


