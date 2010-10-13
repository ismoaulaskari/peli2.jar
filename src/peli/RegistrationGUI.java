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
    private static TreeSet originalNames = new TreeSet(new PlayerCheckBoxComparator()); //unfiltered playernamelist
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

        ButtonGroup buttongroup = new ButtonGroup();
        RadioListener radiolistener = new RadioListener();
        JPanel topJPanel = new JPanel();
        topJPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        topJPanel.setLayout(new BoxLayout(topJPanel, 0));
        topJPanel.add(new JLabel(messages.getString("choosePlayers")));
        topJPanel.add(Box.createHorizontalGlue());
        topJPanel.add(new JLabel(messages.getString("rounds") + ":"));
        topJPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        int rounds = 0;
        if (rules.containsKey("maxRounds")) {
            try {
                rounds = Integer.parseInt(rules.getString("maxRounds"));
            } catch (NumberFormatException ne) {
                rounds = 4;
            }
        }
        for (int i = 1; i <= rounds; i++) {
            JRadioButton jradiobutton = new JRadioButton(String.valueOf(i));
            jradiobutton.setActionCommand(String.valueOf(i));
            jradiobutton.setSelected(true);
            buttongroup.add(jradiobutton);
            jradiobutton.addActionListener(radiolistener);
            topJPanel.add(jradiobutton);
            topJPanel.add(Box.createRigidArea(new Dimension(10, 0)));
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

        JLabel selectByLetterLabel = new JLabel(messages.getString("selectByLetterField"));
        selectByLetterLabel.setToolTipText(messages.getString("selectByLetterToolTip"));
        final JTextField selectByLetterField = new JTextField(1);
        selectByLetterField.setToolTipText(messages.getString("selectByLetterToolTip"));
        selectByLetterField.addKeyListener(new KeyListener() {
            /*
            public void actionPerformed(ActionEvent actionevent) {
            if(selectByLetterField.)
            for (Iterator iterator = RegistrationGUI.names.iterator(); iterator.hasNext();) {
            PlayerJCheckBox playerjcheckbox = (PlayerJCheckBox) iterator.next();
            if (playerjcheckbox.isSelected()) {
            playerjcheckbox.setSelected(false);
            } else {
            playerjcheckbox.setSelected(true);
            }
            }
            //playersLabel.setText(counter != 1 ? messages.getString("players") : messages.getString("player"));
            mainPanel.revalidate();
            mainPanel.repaint();
            }
             */

            public void keyTyped(KeyEvent arg0) {
                String filter = null;
                char typed = arg0.getKeyChar();
                if(typed < 97 || typed > 122) {
                    filter = "";
                }
                else {
                    filter = String.valueOf(typed);
                }

                System.err.println("keytyped");
                if (RegistrationGUI.originalNames.isEmpty() && !RegistrationGUI.names.isEmpty()) {
                    RegistrationGUI.originalNames.addAll(RegistrationGUI.names);
                    System.err.println("new originalnames");
                }

                if (filter.matches("^\\s*$") || filter.length() < 1) {
                    if (!RegistrationGUI.originalNames.isEmpty()) {
                        RegistrationGUI.names.clear();
                        RegistrationGUI.names.addAll(RegistrationGUI.originalNames);
                        System.err.println("restore names");
                    }
                }

                if (filter.matches("^\\S+$") && filter.length() > 0) {
                    System.err.println("filter " + filter + " " + Integer.valueOf(typed));
                    for (Iterator iterator = RegistrationGUI.names.iterator(); iterator.hasNext();) {
                        PlayerJCheckBox playerjcheckbox = (PlayerJCheckBox) iterator.next();
                        if (playerjcheckbox.getText().matches("^\\S+\\s+\\S+\\s+" + filter)) {
                            System.err.println("match " + filter);
                        } else {
                            mainPanel.remove(playerjcheckbox);
                            iterator.remove();
                        }
                    }
                }
                mainPanel.revalidate();
                mainPanel.repaint();
            }

            public void keyPressed(KeyEvent arg0) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void keyReleased(KeyEvent arg0) {
                //throw new UnsupportedOperationException("Not supported yet.");
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

        JPanel middleJPanel = new JPanel();
        middleJPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        middleJPanel.setLayout(new BoxLayout(middleJPanel, 0));
        middleJPanel.add(selectByLetterLabel);
        middleJPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        middleJPanel.add(Box.createHorizontalGlue());
        middleJPanel.add(selectByLetterField);

        JPanel bottomJPanel = new JPanel();
        bottomJPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        bottomJPanel.setLayout(new BoxLayout(bottomJPanel, 0));
        bottomJPanel.add(new JLabel(messages.getString("newPlayerPrompt")));
        bottomJPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomJPanel.add(Box.createHorizontalGlue());
        bottomJPanel.add(newPlayerName);
        bottomJPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomJPanel.add(new JLabel(messages.getString("totalOf") + " "));
        bottomJPanel.add(counterField);
        bottomJPanel.add(playersLabel);
        bottomJPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        bottomJPanel.add(Box.createHorizontalGlue());
        bottomJPanel.add(jbutton);

        topJPanel.add(selectAllButton);
        add(topJPanel, "North");
        add(middleJPanel, "West");
        add(jscrollpane, "Center");
        add(bottomJPanel, "South");
    }
}
