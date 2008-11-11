package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MainWindow.java

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;

/** 
 * the startup screen with logo and buttons for new and old tournaments 
 * v1.1 disables accidental window closing
 * add version number
 * */
public class MainWindow extends JFrame
{

    private Locale locale;
    private ResourceBundle messages;
    private ResourceBundle keyCodes;
    
    MainWindow()
    {
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);
        keyCodes = ResourceBundle.getBundle("KeyCodeBundle", locale);*/
    	locale = Constants.getInstance().getLocale();
    	messages = Constants.getInstance().getMessages();
    	keyCodes = Constants.getInstance().getKeyCodes();
        setTitle(messages.getString("mainWindowHeader"));
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                //System.exit(0);
            }

        });
        Container container = getContentPane();
        JLabel jlabel = new JLabel(new ImageIcon(messages.getString("logoFile")));
        JButton jbutton = createButton(messages.getString("newButton"), messages.getString("newToolTip"), ((Integer)keyCodes.getObject("newMnemonic")).intValue(), new ImageIcon(messages.getString("newIconGifFile")), new NewActionListener(this));
        JButton jbutton1 = createButton(messages.getString("oldButton"), messages.getString("oldToolTip"), ((Integer)keyCodes.getObject("oldMnemonic")).intValue(), new ImageIcon(messages.getString("oldIconGifFile")), new OldActionListener(this));
        JButton jbutton2 = createButton(messages.getString("quitButton"), messages.getString("quitToolTip"), ((Integer)keyCodes.getObject("quitMnemonic")).intValue(), new ImageIcon(messages.getString("quitIconGifFile")), new QuitActionListener());
        JLabel version = new JLabel(System.getProperty("Peli.jarVersion"));
        JPanel jpanel = new JPanel();
        jpanel.add(jbutton);
        jpanel.add(jbutton1);
        jpanel.add(jbutton2);
        jpanel.add(version);
        container.add(jpanel, "South");
        container.add(jlabel, "Center");
    }

    private JButton createButton(String s, String s1, int i, ImageIcon imageicon, ActionListener actionlistener)
    {
        JButton jbutton = new JButton(s, imageicon);
        jbutton.setToolTipText(s1);
        jbutton.setMnemonic(i);
        jbutton.addActionListener(actionlistener);
        return jbutton;
    }

}
