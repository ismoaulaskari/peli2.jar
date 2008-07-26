/*
 * Main.java
 *
 * Created on 3 November 2007, 02:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package launcherfd;

import javax.swing.JFrame;

/**
 *
 * @author aulaskar
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main(String conffile) {        
        
        JFrame base = new JFrame("Launcher application");
        LauncherPanel lp = new LauncherPanel(conffile);
        base.add(lp);
        base.pack();
        base.setVisible(true);
    }

    
    /** Run main launcher program */
    public static void main(String[] args) {
        
        if(args.length < 1) {
            System.err.println("Usage: java LauncherPanel conffile");
            System.exit(-1);
        }
        
        new Main(args[0]);
        
    }
}
