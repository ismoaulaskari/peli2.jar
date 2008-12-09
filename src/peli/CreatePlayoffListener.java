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
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("CREATE")) {             
            if (playoffpane != null) {
                String size = source;
                if (source == null) {
                    size = "2"; //default
                }
                System.out.println(size);
                playoffpane.addTab("Play" + size, TournamentGUI.createPlayoffPanel(Integer.parseInt(size)));
            }
        } else {
            this.source = ae.getActionCommand(); //size?
        }
    }
}


