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
import javax.swing.JOptionPane;

/**
 * handle the main GUI toolbar buttons
 * @author aulaskar
 *
 */
public class SaveActionListener
    implements ActionListener
{

    private Tournament tournament;
    private File file;
    private int what;
    private Component frame;
    private ResourceBundle messages;

    SaveActionListener(File file1, Tournament tournament1, int i, Component component)
    {
        tournament = tournament1;
        file = file1;
        what = i;
        frame = component;
        messages = Constants.getInstance().getMessages();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s;
        switch(what)
        {
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
            
        default:
            s = "";
            break;
        }
        try
        {
            PrintWriter printwriter = new PrintWriter(new BufferedWriter(new FileWriter(file.getName() + s)));
            tournament.save(printwriter, what);
            printwriter.close();
            popUpMessage(getTargetType(what) + " " + messages.getString("wasSaved") + " " + file.getName() + s, frame);
            if(what == 2)
                SaveTracker.isSaved = true;
        }
        catch(IOException ioexception) { }
    }

    private String getTargetType(int i)
    {
        switch(i)
        {
        case 0: // '\0'
            return messages.getString("seriesTable");

        case 1: // '\001'
            return messages.getString("programme");

        case 2: // '\002'
            return messages.getString("tournamentStatus");

        case 3: // '\003'
            return messages.getString("tournamentWebPage");
        }
        return "";
    }

    private void popUpMessage(String s, Component component)
    {
        JOptionPane.showMessageDialog(component, s, messages.getString("fileCreated"), 1, null);
    }

}
