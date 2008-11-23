package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NewActionListener.java

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Create a new tournament
 * v1.1 now with commandline arguments
 * @author aulaskar
 *
 */
public class NewActionListener
    implements ActionListener
{

    private Locale locale;
    private ResourceBundle messages;
    private MainWindow s;
    private Container contents;
    private File saveFile;
    private File playerNameFile;

    public void actionPerformed(ActionEvent actionevent)
    {
    	if(System.getProperty("TournamentFileArgs") != null) {
    		saveFile = new File(System.getProperty("TournamentFileArgs"));
    		saveFile = FileTools.canonize(saveFile, ".tnmt");
    	} 
    	else {
    		saveFile = FileTools.askFileName(messages.getString("newFileQuestion"), contents, new TournamentFileFilter());
    		saveFile = FileTools.canonize(saveFile, ".tnmt");
    	}
        playerNameFile = FileTools.askFileName(messages.getString("playerNameFileQuestion"), contents, new PlayerNameFileFilter());
        contents.removeAll();
        contents.add(new RegistrationGUI(s, playerNameFile, saveFile));
        contents.validate();
        s.pack();
        Tools.center(s);
        contents.repaint();
    }

    NewActionListener(MainWindow mainwindow)
    {
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);*/
    	locale = Constants.getInstance().getLocale();
    	messages = Constants.getInstance().getMessages();
        s = mainwindow;
        contents = s.getContentPane();
    }

}
