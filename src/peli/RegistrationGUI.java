package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RegistrationGUI.java
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;

/**
 * The screen where you pick the players for the tournament
 * This is the source of the Ala-kojola bug but it's fixed now:
 * v1.1 A last name can be Ala-kojola or Ala-Kojola
 * v1.9 choose all-button
 * v1.16 You can configure more than 4 round-robins from Rules.properties
 * @TODO filtteröi pelaajalistaa tyyliin turnausilmoN.pl vapaavalintaisella suffixilla
 * @author aulaskar
 *
 */
public class RegistrationGUI extends JPanel {

    private Locale locale;
    private ResourceBundle messages;
    private ResourceBundle rules;
    private MainWindow mainWindow;
    private Container mainWindowContents;
    private static TreeSet names = new TreeSet(new PlayerCheckBoxComparator());
    private int counter;
    private int rank;

    private void popUpErrorMessage(String s) {
        JOptionPane.showMessageDialog(this, s, messages.getString("duplicateErrorMessageHeader"), 2, null);
    }


    RegistrationGUI(MainWindow mainwindow, File file, File file1) {
        super(new BorderLayout());
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);*/
        locale = Constants.getInstance().getLocale();
        messages = Constants.getInstance().getMessages();
        rules = Constants.getInstance().getRules();
        counter = 0;
        rank = 0;
        mainWindow = mainwindow;
        mainWindowContents = mainwindow.getContentPane();
        mainWindow.setTitle(messages.getString("registrationWindowHeader"));
        final JLabel counterField = new JLabel("0");
        final JLabel playersLabel = new JLabel(" " + messages.getString("players"));
        final ItemListener cbl = new ItemListener() {

            public void itemStateChanged(ItemEvent itemevent) {
                if (itemevent.getStateChange() == 2) {
                    --counter;
                } else {
                    ++counter;
                }
                counterField.setText(counter + " ");
                playersLabel.setText(counter != 1 ? messages.getString("players") : messages.getString("player"));
            }
        };
        for (LineReader linereader = new LineReader(file.getName()); linereader.hasNext();) {
            String s = Tools.fixName((String) linereader.next());
            PlayerJCheckBox playerjcheckbox = new PlayerJCheckBox(++rank, s);
            playerjcheckbox.addItemListener(cbl);
            if (names.contains(playerjcheckbox)) {
                popUpErrorMessage(s + " " + messages.getString("moreThanOnce") + " " + file.getName() + ".");
            } else {
                names.add(playerjcheckbox);
            }
        }

        //number of rounds?
        /*JRadioButton jradiobutton = new JRadioButton("1");
        jradiobutton.setActionCommand("1");
        jradiobutton.setSelected(true);
        JRadioButton jradiobutton1 = new JRadioButton("2");
        jradiobutton1.setActionCommand("2");
        jradiobutton1.setSelected(true);
        JRadioButton jradiobutton2 = new JRadioButton("3");
        jradiobutton2.setActionCommand("3");
        jradiobutton2.setSelected(true);
        JRadioButton jradiobutton3 = new JRadioButton("4");
        jradiobutton3.setActionCommand("4");
        jradiobutton3.setSelected(true);*/
        ButtonGroup buttongroup = new ButtonGroup();
        /*buttongroup.add(jradiobutton);
        buttongroup.add(jradiobutton1);
        buttongroup.add(jradiobutton2);
        buttongroup.add(jradiobutton3);*/
        RadioListener radiolistener = new RadioListener();
        /*jradiobutton.addActionListener(radiolistener);
        jradiobutton1.addActionListener(radiolistener);
        jradiobutton2.addActionListener(radiolistener);
        jradiobutton3.addActionListener(radiolistener);*/
        JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        jpanel.setLayout(new BoxLayout(jpanel, 0));
        jpanel.add(new JLabel(messages.getString("choosePlayers")));
        jpanel.add(Box.createHorizontalGlue());
        jpanel.add(new JLabel(messages.getString("rounds") + ":"));
        jpanel.add(Box.createRigidArea(new Dimension(30, 0)));
        /*jpanel.add(jradiobutton);
        jpanel.add(Box.createRigidArea(new Dimension(10, 0)));
        jpanel.add(jradiobutton1);
        jpanel.add(Box.createRigidArea(new Dimension(10, 0)));
        jpanel.add(jradiobutton2);
        jpanel.add(Box.createRigidArea(new Dimension(10, 0)));
        jpanel.add(jradiobutton3);*/
        int rounds = 0;
        if(rules.containsKey("maxRounds")) {
            try {
                rounds = Integer.parseInt(rules.getString("maxRounds"));
            }
            catch (NumberFormatException ne) {
                rounds = 4;
            }
        }
        for(int i = 1; i <= rounds; i++) {
            JRadioButton jradiobutton = new JRadioButton(String.valueOf(i));
            jradiobutton.setActionCommand(String.valueOf(i));
            jradiobutton.setSelected(true);
            buttongroup.add(jradiobutton);
            jradiobutton.addActionListener(radiolistener);
            jpanel.add(jradiobutton);
            jpanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        final JPanel mainPanel = new JPanel();
        javax.swing.border.Border border = BorderFactory.createEmptyBorder(15, 30, 15, 30);
        mainPanel.setBorder(border);
        mainPanel.setLayout(new GridLayout(0, 4, 10, 5));
        for (Iterator iterator = names.iterator(); iterator.hasNext(); mainPanel.add((PlayerJCheckBox) iterator.next()));
        JScrollPane jscrollpane = new JScrollPane(mainPanel, 20, 31);
        jscrollpane.setPreferredSize(new Dimension(200, 350));
        final JTextField newPlayerName = new JTextField(30);
        newPlayerName.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent) {
                String s1 = Tools.fixName(newPlayerName.getText());
                newPlayerName.setText("");
                if (s1.equals("")) {
                    return;
                }
                PlayerJCheckBox playerjcheckbox1 = new PlayerJCheckBox(++rank, s1, true);
                if (RegistrationGUI.names.contains((PlayerJCheckBox) playerjcheckbox1)) {
                    popUpErrorMessage(s1 + " " + messages.getString("duplicateEntry") + ".");
                } else {
                    playerjcheckbox1.addItemListener(cbl);
                    RegistrationGUI.names.add((PlayerJCheckBox) playerjcheckbox1);
                    counterField.setText(++counter + " ");
                    playersLabel.setText(counter != 1 ? messages.getString("players") : messages.getString("player"));
                    mainPanel.add(playerjcheckbox1);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                }
            }
        });

        JButton selectByLetterButton = new JButton(messages.getString("selectByLetterButton"));
        selectByLetterButton.setToolTipText(messages.getString("selectByLetterToolTip"));
        selectByLetterButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent) {
                for (Iterator iterator = RegistrationGUI.names.iterator(); iterator.hasNext();) {
                    /*PlayerJCheckBox playerjcheckbox = (PlayerJCheckBox) iterator.next();
                    if (playerjcheckbox.isSelected()) {
                        playerjcheckbox.setSelected(false);
                    } else {
                        playerjcheckbox.setSelected(true);
                    }*/
                }
                playersLabel.setText(counter != 1 ? messages.getString("players") : messages.getString("player"));
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });

        JButton selectAllButton = new JButton(messages.getString("selectAllButton"));
        selectAllButton.setToolTipText(messages.getString("selectAllToolTip"));
        selectAllButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent) {
                for (Iterator iterator = RegistrationGUI.names.iterator(); iterator.hasNext();) {
                    PlayerJCheckBox playerjcheckbox = (PlayerJCheckBox) iterator.next();
                    if (playerjcheckbox.isSelected()) {
                        playerjcheckbox.setSelected(false);                        
                    } else {
                        playerjcheckbox.setSelected(true);                        
                    }
                }
                playersLabel.setText(counter != 1 ? messages.getString("players") : messages.getString("player"));
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });

        JButton jbutton = new JButton(messages.getString("readyButton"));
        jbutton.setToolTipText(messages.getString("readyToolTip"));
        jbutton.addActionListener(new ReadyActionListener(mainWindow, names, file1, radiolistener));
        JPanel jpanel1 = new JPanel();
        jpanel1.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        jpanel1.setLayout(new BoxLayout(jpanel1, 0));
        jpanel1.add(new JLabel(messages.getString("newPlayerPrompt")));
        jpanel1.add(Box.createRigidArea(new Dimension(10, 0)));
        jpanel1.add(Box.createHorizontalGlue());
        jpanel1.add(newPlayerName);
        jpanel1.add(Box.createRigidArea(new Dimension(10, 0)));
        jpanel1.add(new JLabel(messages.getString("totalOf") + " "));
        jpanel1.add(counterField);
        jpanel1.add(playersLabel);
        jpanel1.add(Box.createRigidArea(new Dimension(20, 0)));
        jpanel1.add(Box.createHorizontalGlue());
        jpanel1.add(jbutton);
        jpanel.add(selectByLetterButton);
        jpanel.add(selectAllButton);
        add(jpanel, "North");
        add(jscrollpane, "Center");
        add(jpanel1, "South");
    }
}
