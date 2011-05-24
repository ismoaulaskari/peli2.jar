package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TournamentGUI.java
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/** 
 * The layout of the main GUI as seen during tournaments
 * v1.1 fixed the window-close button,
 * added a restart button for getting mutuals (which reserves some more memory),
 * added a full tournament results tab,
 * and a button for saving final standings of the tournament
 * v.1.9 playoff-tab, placement matches tab, unsaved-notification
 * v.1.11 show bronze game and support creating placement matches
 * @author aulaskar
 *
 */
public class TournamentGUI extends JPanel {



    TournamentGUI(MainWindow mainwindow, Tournament tournament1, File file) {
        System.setProperty("TournamentFileName", file.getName()); //hack, throws e
        //System.setProperty("TemplateTitle", file.getName()); 
        tournament = tournament1;
        themainwindow = mainwindow; //hack
        createThis(mainwindow, file);
//      add line to fix dangerous close-button:
        mainwindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    TournamentGUI(MainWindow mainwindow, int i, TreeSet treeset, File file) {
        System.setProperty("TournamentFileName", file.getName()); //hack, throws e
        //System.setProperty("TemplateTitle", file.getName().substring(0, file.getName().indexOf('.'))); 
        SaveTracker.setIsSaved(false);
        tournament = new Tournament(i, treeset);
        themainwindow = mainwindow; //hack
        createThis(mainwindow, file);
//      add line to fix dangerous close-button:
        mainwindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    private void createThis(MainWindow mainwindow, File file) {
        mainwindow.setTitle(messages.getString("mainWindowHeader"));
        Container container = mainwindow.getContentPane();
        JToolBar jtoolbar = createToolBar(container, tournament, file);
        container.add(jtoolbar, "North");
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLineBorder(Color.black)));
        setLayout(new BorderLayout());
        JPanel jpanel = createDivisionCards();
        add(jpanel, "Center");
        add(createComboBoxPane(jpanel), "North");
    }

    private JToolBar createToolBar(final Component frame, Tournament tournament1, File file) {
        JToolBar jtoolbar = new JToolBar();
        JButton jbutton = null;
        JButton restartbutton = null;
        ImageIcon imageicon = null;

        /* start ugly hack: quick restart button to get results with mutual ordering */
        imageicon = new ImageIcon(messages.getString("printIconGifFile"));
        restartbutton = new JButton(messages.getString("restartButton"), imageicon);
        restartbutton.setToolTipText(messages.getString("restartToolTip"));
        //restartbutton.setMnemonic(((Integer)keyCodes.getObject("restartMnemonic")).intValue());
        restartbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent) {
                if (SaveTracker.isSaved()) {
                    //cleanup and restart                	                	
                    themainwindow.dispose();
                    tournament = null;
                    themainwindow = null;
                    locale = null;
                    //messages = null;
                    //keyCodes = null;
                    leftRenderer = null;
                    try {
                        super.finalize(); //huh                                
                    } catch (Throwable e) {
                        e.printStackTrace();		//handle exception?
                    }

                    System.runFinalization();
                    System.gc();
                    String[] restartargs = new String[2];
                    restartargs[0] = System.getProperty("TournamentFileName");
                    restartargs[1] = "restart";
                    RunTournament.main(restartargs); //restart the whole program in the same JVM
                    return;
                }

                Object aobj[] = {
                    TournamentGUI.messages.getString("doNotQuit"), TournamentGUI.messages.getString("doQuit")
                };
                int i = JOptionPane.showOptionDialog(frame, TournamentGUI.messages.getString("areYouSure") + "\n" + TournamentGUI.messages.getString("youDidNotSave"), TournamentGUI.messages.getString("reallyQuit"), 0, 3, null, aobj, aobj[0]);
                switch (i) {
                    case 0: // '\0'
                        return;

                    case 1: // '\001'
                        //System.exit(0); //you would never want to exit here
                        break;
                }
            }
        });
        jtoolbar.add(restartbutton);
        /* end ugly hack */

        //save progamme-button
        imageicon = new ImageIcon(messages.getString("printIconGifFile"));
        jbutton = new JButton(messages.getString("mHtmlButton"), imageicon);
        jbutton.setToolTipText(messages.getString("mHtmlToolTip"));
        jbutton.setMnemonic(((Integer) keyCodes.getObject("mHtmlMnemonic")).intValue());
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 1, frame));
        jtoolbar.add(jbutton);

        //savematchesbyplayer-button
        imageicon = new ImageIcon(messages.getString("printIconGifFile"));
        jbutton = new JButton(messages.getString("bHtmlButton"), imageicon);
        jbutton.setToolTipText(messages.getString("bHtmlToolTip"));
        //jbutton.setMnemonic(((Integer) keyCodes.getObject("mHtmlMnemonic")).intValue());
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 5, frame));
        jtoolbar.add(jbutton);

        /*//no more confusing html-seriestable
        imageicon = new ImageIcon(messages.getString("printIconGifFile"));
        jbutton = new JButton(messages.getString("sHtmlButton"), imageicon);
        jbutton.setToolTipText(messages.getString("sHtmlToolTip"));
        jbutton.setMnemonic(((Integer)keyCodes.getObject("sHtmlMnemonic")).intValue());
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 0, frame));
        jtoolbar.add(jbutton);
         */
        //new: final standings button
        imageicon = new ImageIcon(messages.getString("printIconGifFile"));
        jbutton = new JButton(messages.getString("iHtmlButton"), imageicon);
        jbutton.setToolTipText(messages.getString("iHtmlToolTip"));
        //jbutton.setMnemonic(((Integer)keyCodes.getObject("iHtmlMnemonic")).intValue());
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 4, frame));
        jtoolbar.add(jbutton);

        //save html-button
        imageicon = new ImageIcon(messages.getString("printIconGifFile"));
        jbutton = new JButton(messages.getString("htmlButton"), imageicon);
        jbutton.setMnemonic(((Integer) keyCodes.getObject("htmlMnemonic")).intValue());
        jbutton.setToolTipText(messages.getString("htmlToolTip"));
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 3, frame));
        jtoolbar.add(jbutton);

        //save tournament-button
        imageicon = new ImageIcon(messages.getString("saveAllIconGifFile"));
        jbutton = new JButton(SaveTracker.getIsSavedLogo() + messages.getString("saveButton"), imageicon);
        SaveTracker.setRegisteredSaveButton(jbutton);
        jbutton.setMnemonic(((Integer) keyCodes.getObject("saveMnemonic")).intValue());
        jbutton.setToolTipText(messages.getString("saveToolTip"));
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 2, frame));
        jtoolbar.add(jbutton);

        imageicon = new ImageIcon(messages.getString("quitIconGifFile"));
        jbutton = new JButton(messages.getString("quitButton"), imageicon);
        jbutton.setToolTipText(messages.getString("quitToolTip"));
        jbutton.setMnemonic(((Integer) keyCodes.getObject("quitMnemonic")).intValue());
        jbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent) {
                if (SaveTracker.isSaved()) {
                    System.exit(0);
                }
                Object aobj[] = {
                    TournamentGUI.messages.getString("doNotQuit"), TournamentGUI.messages.getString("doQuit")
                };
                int i = JOptionPane.showOptionDialog(frame, TournamentGUI.messages.getString("areYouSure") + "\n" + TournamentGUI.messages.getString("youDidNotSave"), TournamentGUI.messages.getString("reallyQuit"), 0, 3, null, aobj, aobj[0]);
                switch (i) {
                    case 0: // '\0'
                        return;

                    case 1: // '\001'
                        System.exit(0);
                        break;
                }
            }
        });
        jtoolbar.add(jbutton);
        return jtoolbar;
    }
    /*
    private static JPanel createJListPane(final JPanel cards) {
    JPanel jpanel = new JPanel();
    
    String[] divTitles = tournament.getDivisionTitles();
    Vector vector = new Vector(divTitles.length + 1);
    for (int i = 0; i < divTitles.length; i++) {
    vector.add(divTitles[i]);
    }
    vector.add(messages.getString("playoff"));
    
    JList jlist = new JList(vector);
    jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jlist.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    jlist.setVisibleRowCount(3);
    JScrollPane listScroller = new JScrollPane(jlist);
    
    jlist.addListSelectionListener(new ListSelectionListener() {
    
    public void valueChanged(ListSelectionEvent e) {
    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
    
    int firstIndex = e.getFirstIndex();
    int lastIndex = e.getLastIndex();
    boolean isAdjusting = e.getValueIsAdjusting();
    //                output.append("Event for indexes " + firstIndex + " - " + lastIndex + "; isAdjusting is " + isAdjusting + "; selected indexes:");
    
    if (lsm.isSelectionEmpty()) {
    //output.append(" <none>");
    } else {
    // Find out which indexes are selected.
    //lsm.
    int minIndex = lsm.getMinSelectionIndex();
    int maxIndex = lsm.getMaxSelectionIndex();
    for (int i = minIndex; i <= maxIndex; i++) {
    if (lsm.isSelectedIndex(i)) {
    //      output.append(" " + i);
    }
    }
    }
    //output.append(newline);
    }
    
    public void itemStateChanged(ItemEvent itemevent) {
    CardLayout cardlayout = (CardLayout) cards.getLayout();
    cardlayout.show(cards, (String) itemevent.getItem());
    }
    });
    jpanel.add(jlist);
    jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3), BorderFactory.createLineBorder(Color.black)), " " + messages.getString("chooseGroup") + " "));
    return jpanel;
    }
     */

    private static JPanel createComboBoxPane(final JPanel cards) {
        JPanel jpanel = new JPanel();

        String[] divTitles = tournament.getDivisionTitles();
        Vector vector = new Vector(divTitles.length + 1);
        for (int i = 0; i < divTitles.length; i++) {
            vector.add(divTitles[i]);
        }
        vector.add(messages.getString("playoff"));
        vector.add(messages.getString("placementMatches"));

        JComboBox jcombobox = new JComboBox(vector);
        jcombobox.setEditable(false);
//        jcombobox.setPreferredSize(jcombobox.getMinimumSize());
//        jcombobox.setMaximumRowCount(3);
        jcombobox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent itemevent) {
                CardLayout cardlayout = (CardLayout) cards.getLayout();
                cardlayout.show(cards, (String) itemevent.getItem());
            }
        });
        jpanel.add(jcombobox);
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3), BorderFactory.createLineBorder(Color.black)), " " + messages.getString("chooseGroup") + " "));
        return jpanel;
    }

    private static JPanel createMatchListTable(int i) {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, 1));
        Division division = tournament.getDivision(i);
        for (int j = 0; j < division.getNumberOfRounds(); j++) {
            JPanel jpanel1 = new JPanel();
            jpanel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            Round round = division.getRound(j);
            RoundTableModel roundtablemodel = new RoundTableModel(round);
            JTable jtable = new JTable(roundtablemodel);
            jtable.setShowVerticalLines(false);
            jtable.setShowHorizontalLines(false);
            jtable.setRowSelectionAllowed(false);
            jtable.setColumnSelectionAllowed(false);
            JTableHeader jtableheader = jtable.getTableHeader();
            jtableheader.setReorderingAllowed(false);
            setRoundTableRenderers(jtable.getColumnModel(), j + 1);
            jpanel1.setLayout(new BorderLayout());
            jpanel1.add(jtable.getTableHeader(), "North");
            jpanel1.add(jtable, "Center");
            jpanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6), BorderFactory.createLineBorder(Color.black)), "  " + messages.getString("round") + " " + (j + 1) + ".    " + messages.getString("allowed_prefixes") + ": " + messages.getString("overtime") + ", " + messages.getString("disqualified") + ", " + messages.getString("walkover")));
            jpanel.add(jpanel1);
        }

        return jpanel;
    }

    private static void setPlayoffTableRenderers(TableColumnModel tablecolumnmodel) {
        //placement
        TableColumn tablecolumn = tablecolumnmodel.getColumn(0);
        tablecolumn.setCellRenderer(playoffRenderer);
        tablecolumn.setPreferredWidth(50);
        //names                        
        tablecolumn = tablecolumnmodel.getColumn(1);
        tablecolumn.setPreferredWidth(150);
        tablecolumn.setMaxWidth(150);
        tablecolumn.setCellRenderer(playoffRenderer);
        //wins
        tablecolumn = tablecolumnmodel.getColumn(2);
        tablecolumn.setCellRenderer(playoffRenderer);
        tablecolumn.setPreferredWidth(30);

        //games
        for (int xx = 0; xx < Constants.getMAXMATCHES(); xx++) {
            tablecolumn = tablecolumnmodel.getColumn(xx + 3);
            tablecolumn.setCellRenderer(playoffRenderer);
            tablecolumn.setPreferredWidth(50);
        }

    }

    private static void setPlacementMatchTableRenderers(TableColumnModel tablecolumnmodel) {
        //placement
        TableColumn tablecolumn = tablecolumnmodel.getColumn(0);
        tablecolumn.setCellRenderer(playoffRenderer);
        tablecolumn.setPreferredWidth(50);
        //names                        
        tablecolumn = tablecolumnmodel.getColumn(1);
        tablecolumn.setPreferredWidth(150);
        tablecolumn.setMaxWidth(150);
        tablecolumn.setCellRenderer(playoffRenderer);
        //wins
        tablecolumn = tablecolumnmodel.getColumn(2);
        tablecolumn.setCellRenderer(playoffRenderer);
        tablecolumn.setPreferredWidth(30);

        //games
        for (int xx = 0; xx < Constants.getMAXMATCHES(); xx++) {
            tablecolumn = tablecolumnmodel.getColumn(xx + 3);
            tablecolumn.setCellRenderer(playoffRenderer);
            tablecolumn.setPreferredWidth(50);
        }

    }

    private static void setRoundTableRenderers(TableColumnModel tablecolumnmodel, int i) {
        TableColumn tablecolumn = tablecolumnmodel.getColumn(0);
        tablecolumn.setCellRenderer(centerRenderer);
        tablecolumn.setPreferredWidth(40);
        tablecolumn.setMaxWidth(50);
        tablecolumn = tablecolumnmodel.getColumn(1);
        tablecolumn.setPreferredWidth(150);
        tablecolumn = tablecolumnmodel.getColumn(2);
        tablecolumn.setPreferredWidth(150);
        tablecolumn = tablecolumnmodel.getColumn(3);
        tablecolumn.setCellRenderer(centerRenderer);
        tablecolumn.setPreferredWidth(40);
        tablecolumn.setMaxWidth(50);
    }

    private static void setSeriesTableRenderers(TableColumnModel tablecolumnmodel) {
        for (int i = 0; i < tablecolumnmodel.getColumnCount(); i++) {
            TableColumn tablecolumn = tablecolumnmodel.getColumn(i);
            tablecolumn.setCellRenderer(centerRenderer);
            tablecolumn.setPreferredWidth(30);
            tablecolumn.setMaxWidth(50);
        }

        TableColumn tablecolumn1 = tablecolumnmodel.getColumn(1);
        tablecolumn1.setCellRenderer(leftRenderer);
        tablecolumn1.setPreferredWidth(200);
        tablecolumn1.setMaxWidth(250);
        tablecolumn1 = tablecolumnmodel.getColumn(7);
        tablecolumn1.setPreferredWidth(5);
        tablecolumn1.setMaxWidth(10);
    }

    /**
     * should be added to playoff-tabs
     * @param maxPlayers
     * @return
     */
    private static JButton createNextRoundButton(int maxPlayers) {
        JButton nextButton = new JButton(messages.getString("nextPlayoffRound"));
        ((CreatePlayoffListener) createlistener).setSource(String.valueOf(maxPlayers));
        nextButton.setActionCommand("CREATENEXT");
        nextButton.addActionListener(createlistener);

        return nextButton;
    }

    public static boolean playoffsFinished() {
        return tournament.isPlayoffRoundFinished();
    }

    public static void warnUnfinishedPlayoff()
    {
        JOptionPane.showMessageDialog(null, messages.getString("playoffMustEndInWinner"), messages.getString("unfinishedPlayoff"), 2, null);
    }

    /**
     * warn about replacing playoffs with new ones
     * @return
     */
    public static boolean warnCreatePlayoff() {
        boolean ok = false;
        Object aobj[] = {
            TournamentGUI.messages.getString("doNotReplacePlayoff"), TournamentGUI.messages.getString("doReplacePlayoff")
        };
        int i = JOptionPane.showOptionDialog(null, TournamentGUI.messages.getString("reallyReplacePlayoff") + "\n" + TournamentGUI.messages.getString("playoffsExist"), TournamentGUI.messages.getString("replacePlayoffs"), 0, 3, null, aobj, aobj[0]);
        switch (i) {
            case 0: // '\0'
                ok = false;
                break;
            case 1: // '\001'                
                ok = true;
                tournament.clearPlayoffs();
                break;
        }

        return ok;
    }

    /**
     * warn about replacing playoffs with new ones
     * @return
     */
    public static boolean warnCreatePlacementMatches() {
        boolean ok = false;
        Object aobj[] = {
            TournamentGUI.messages.getString("doNotReplace"), TournamentGUI.messages.getString("doReplace")
        };
        int i = JOptionPane.showOptionDialog(null, TournamentGUI.messages.getString("reallyReplacePlacementMatches") + "\n" + TournamentGUI.messages.getString("playoffsExist"), TournamentGUI.messages.getString("replacePlayoffs"), 0, 3, null, aobj, aobj[0]);
        switch (i) {
            case 0: // '\0'
                ok = false;
                break;
            case 1: // '\001'
                ok = true;
                tournament.clearPlacementMatches();
                break;
        }

        return ok;
    }

    /**
     * warn about replacing playoffs with new ones
     * @return
     */
    public static boolean warnCreateBronzeMatch() {
        boolean ok = false;
        Object aobj[] = {
            TournamentGUI.messages.getString("doNotReplace"), TournamentGUI.messages.getString("doReplace")
        };
        int i = JOptionPane.showOptionDialog(null, TournamentGUI.messages.getString("reallyReplaceBronzeMatch") + "\n" + TournamentGUI.messages.getString("playoffsExist"), TournamentGUI.messages.getString("replacePlayoffs"), 0, 3, null, aobj, aobj[0]);
        switch (i) {
            case 0: // '\0'
                ok = false;
                break;
            case 1: // '\001'
                ok = true;
                tournament.clearBronzeMatch();
                break;
        }

        return ok;
    }

    /**
     *  List for choosing playoff-size
     */
    public static JPanel createPlayoffSizeButtons(int maxPlayers) {
        ButtonGroup playoffSize = new ButtonGroup();
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        jpanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (int i = 1; maxPlayers / i > 1; i *= 2) {
            int tmp = maxPlayers / i;
            JRadioButton option = new JRadioButton(tmp + " " + messages.getString("players"));
            /*if (tmp == 8) {
            option.setSelected(true);
            }*/
            option.setActionCommand(String.valueOf(tmp));
            option.addActionListener(createlistener);
            playoffSize.add(option);
            jpanel.add(option);
            //special cases
            if (tmp == 16) {
                JRadioButton option2 = new JRadioButton(12 + " " + messages.getString("players"));
                option2.setActionCommand(String.valueOf(12));
                option2.addActionListener(createlistener);
                playoffSize.add(option2);
                jpanel.add(option2);
            } else {
                if (tmp == 8) {
                    JRadioButton option2 = new JRadioButton(6 + " " + messages.getString("players"));
                    option2.setActionCommand(String.valueOf(6));
                    option2.addActionListener(createlistener);
                    playoffSize.add(option2);
                    jpanel.add(option2);
                }
            }

        }

        jpanel.add(Box.createRigidArea(new Dimension(5, 15)));
        JButton createDynamicButton = new JButton(messages.getString("createDynamicPlayoffs"));
        createDynamicButton.setActionCommand("CREATEDYNAMIC");
        createDynamicButton.setToolTipText(messages.getString("createDynamicPlayoffsTip"));
        createDynamicButton.addActionListener(createlistener);
        jpanel.add(createDynamicButton);

        jpanel.add(Box.createRigidArea(new Dimension(5, 15)));
        JButton createStaticButton = new JButton(messages.getString("createStaticPlayoffs"));
        createStaticButton.setActionCommand("CREATESTATIC");
        createStaticButton.setToolTipText(messages.getString("createStaticPlayoffsTip"));
        createStaticButton.addActionListener(createlistener);
        jpanel.add(createStaticButton);

        jpanel.add(Box.createRigidArea(new Dimension(5, 15)));
        JButton createRandomButton = new JButton(messages.getString("createRandomPlayoffs"));
        createRandomButton.setActionCommand("CREATERANDOM");
        createRandomButton.setToolTipText(messages.getString("createRandomPlayoffsTip"));
        createRandomButton.addActionListener(createlistener);
        jpanel.add(createRandomButton);

        return jpanel;
    }

    /**
     *  Buttons for initializing placementmatches, after the basic group is ready
     */
    public static JPanel createPlacementMatchButtons() {
        ButtonGroup placementmatchlaunchers = new ButtonGroup();
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        jpanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton bronzeButton = new JButton(messages.getString("createBronzeMatch"));
        bronzeButton.setActionCommand("CREATEBRONZE");
        bronzeButton.addActionListener(createplacementmatchlistener);
        placementmatchlaunchers.add(bronzeButton);
        jpanel.add(bronzeButton);
        jpanel.add(Box.createRigidArea(new Dimension(5, 15)));
        JButton createButton = new JButton(messages.getString("createPlacementMatches"));
        createButton.setActionCommand("CREATEPLACEMENT");
        createButton.addActionListener(createplacementmatchlistener);
        jpanel.add(createButton);
        jpanel.add(Box.createRigidArea(new Dimension(5, 15)));
        JLabel noticeLabel = new JLabel(messages.getString("placementMatchOrder"));
        jpanel.add(noticeLabel);

        return jpanel;
    }

    public static JTabbedPane getPlayoffpane() {

        return playoffpane;
    }

    /**
     * new playoffs, empty existing playoffpane
     * @param playoffpane
     */
    public static void newPlayoffpane(JTabbedPane playoffpane) {
        playoffpane.removeAll();
        playoffpane.addTab(messages.getString("newPlayoff"), TournamentGUI.createPlayoffSizeButtons(128));

    }

    /**
     * new placementmatches, empty existing
     * @param playoffpane
     */
    public static void newPlacementMatchPane(JTabbedPane playoffpane) {
        playoffpane.removeAll();
        playoffpane.addTab(messages.getString("newPlacementMatches"), TournamentGUI.createPlacementMatchButtons());

    }

    /**
     * playoff-tabs
     * @return
     */
    public static JPanel createPlayoff() {
        JPanel jpanel2 = new JPanel();
        jpanel2.setLayout(new BoxLayout(jpanel2, BoxLayout.Y_AXIS));
        jpanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));        
        playoffpane = new JTabbedPane();
        createlistener = new CreatePlayoffListener(playoffpane);
        newPlayoffpane(playoffpane);
        jpanel2.add(playoffpane);
        JScrollPane columnScrollPane = new JScrollPane(jpanel2); //@TODO skrollaus ei toimi
        columnScrollPane.setSize(new Dimension(jpanel2.getSize()));
        //ajtabbedpane[k].addTab(messages.getString("playoff"), columnScrollPane);

        //Set set = tournament.getPlayoffs().keySet(); //hmm? iterator?
        //ArrayList list = new ArrayList();
        //list.addAll(set);
        java.util.List list = tournament.getPlayoffsSortedKeySet();
        Collections.reverse(list);
        for (Object playoffnumber : list) {
            playoffpane.addTab(messages.getString("bestOf") + " " + (Integer) playoffnumber, createPlayoffPanel((Integer) playoffnumber, tournament.getSeedingModel()));
        }
        playoffpane.setSelectedIndex(playoffpane.getTabCount() - 1);

        return jpanel2;
    }

    /**
     * placementmatch-tabs
     * @return
     */
    public static JPanel createPlacementMatches() {
        JPanel jpanel2 = new JPanel();
        jpanel2.setLayout(new BoxLayout(jpanel2, BoxLayout.Y_AXIS));
        jpanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        placementmatchpane = new JTabbedPane();
        createplacementmatchlistener = new CreatePlacementMatchListener(placementmatchpane);
        newPlacementMatchPane(placementmatchpane);
        jpanel2.add(placementmatchpane);
        JScrollPane columnScrollPane = new JScrollPane(jpanel2);
        columnScrollPane.setSize(new Dimension(jpanel2.getSize()));

        if (tournament.isPlacementMatches()) {
            placementmatchpane = newPlacementMatches(placementmatchpane);
        }

        if (tournament.isBronzeMatch()) {
            placementmatchpane = newBronzeMatch(placementmatchpane);
        }

        return jpanel2;
    }

    public static JTabbedPane newPlacementMatches(JTabbedPane pane) {
        if (tournament.isPlacementMatches() || tournament.getNumberOfPlayoffs() > 0) {
            if (placementmatchestab != null) {
                pane.remove(placementmatchestab);
                placementmatchestab = null;
            }
            placementmatchestab = createPlacementMatchPanel(tournament.getStandingsNames(tournament.getStandings()).size(), tournament.getLargestPlayoff());
            if (placementmatchestab != null) {
                pane.addTab(messages.getString("placementMatches"), placementmatchestab);
            }
        }

        return pane;
    }

    public static JTabbedPane newBronzeMatch(JTabbedPane pane) {
        if (tournament.isBronzeMatch() || (tournament.isPlacementMatches() && tournament.getNumberOfPlayoffs() > 0)) {
            if (bronzematchtab != null) {
                pane.remove(bronzematchtab);
                bronzematchtab.setVisible(false);
                bronzematchtab = null;
            }
            bronzematchtab = createBronzeMatchPanel();
            if (bronzematchtab != null) {
                pane.addTab(messages.getString("bronzeMatch"), bronzematchtab);
            }
        }

        return pane;
    }

    /**
     * a playoff round
     * @param size
     * @return
     */
    public static JPanel createPlayoffPanel(int size, String seedingModel) {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //jpanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6), BorderFactory.createLineBorder(Color.black)), "  " + messages.getString("allowed_prefixes") + ": " + messages.getString("overtime") + ", " + messages.getString("walkover")));
        Playoff playoff = null;
        if (seedingModel.equals("CREATERANDOM")) {
            playoff = tournament.getPlayoffRandomSeed(size);
            tournament.setSeedingModel(seedingModel);
        } else {
            if (seedingModel.equals("CREATESTATIC")) {
                playoff = tournament.getPlayoffNoReseed(size);
                tournament.setSeedingModel(seedingModel);
            } else {
                if (seedingModel.equals("CREATEDYNAMIC")) {
                    playoff = tournament.getPlayoffWithReseed(size);
                    tournament.setSeedingModel(seedingModel);
                } else {
                    //default                    
                    playoff = tournament.getPlayoff(tournament.getSeedingModel(), size);
                }
            }
        }

        if (playoff == null) {
            //jpanel.add(new JLabel(messages.getString("areYouSure")));            
            return null;
        }
        PlayoffPair[] pairs = playoff.getPlayoffPairs();
        for (int x = 0; x < pairs.length; x++) {
            PlayoffPair pair = pairs[x];
            PlayoffPairTableModel pairmodel = new PlayoffPairTableModel(pair);
            JTable jtable2 = new JTable(pairmodel);
            setPlayoffTableRenderers(jtable2.getColumnModel());
            jtable2.setShowVerticalLines(false);
            jtable2.setShowHorizontalLines(false);
            jtable2.setRowSelectionAllowed(false);
            jtable2.setColumnSelectionAllowed(false);
            //jtable2.setForeground(jpanel.getForeground());
            jtable2.setBackground(jpanel.getBackground());
            JTableHeader playofftableheader = jtable2.getTableHeader();
            playofftableheader.setReorderingAllowed(false);
            if (x == 0) {
                jpanel.add(playofftableheader);
            }
            jpanel.add(Box.createRigidArea(new Dimension(5, 5)));
            jpanel.add(jtable2);
            pairmodel.fireTableDataChanged();
        }

        if (size > 2) {
            jpanel.add(Box.createRigidArea(new Dimension(5, 15)));
            int nextSize = size / 2;
            //special cases
            if (size == 6) {
                nextSize = 4;
            } else {
                if (size == 12) {
                    nextSize = 8;
                }
            }
            jpanel.add(createNextRoundButton(nextSize));
        }

        SaveTracker.setIsSaved(false);

        //scrollpanel has to added here for some reason
        JPanel outerpanel = new JPanel();
        JScrollPane playoffscrollpane = new JScrollPane(jpanel);
        //playoffscrollpane.setPreferredSize(new Dimension(640, 640));
        Dimension playofpanesize = playoffpane.getSize();
        double width = playofpanesize.getWidth();
        if (width == 0) {
            width = 720;
        }
        //playofpanesize.setSize(playofpanesize.getWidth(), 800); //witdht=0 when opening old playoff
        playofpanesize.setSize(width, 800);
        playoffscrollpane.setPreferredSize(playofpanesize);
        outerpanel.add(playoffscrollpane);

        return outerpanel;
    }

    public static JPanel createPlacementMatchPanel() {
        return createPlacementMatchPanel(tournament.getStandingsNames(tournament.getStandings()).size(), tournament.getLargestPlayoff());
    }

    /**
     * Placement matches possible for all players
     * @param size
     * @return
     */
    public static JPanel createPlacementMatchPanel(int size, int playoffSize) {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        //jpanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6), BorderFactory.createLineBorder(Color.black)), "  " + messages.getString("allowed_prefixes") + ": " + messages.getString("overtime") + ", " + messages.getString("walkover")));
        Playoff playoff = tournament.getPlacementMatches(size); //get a full playoffround of all-size players
        if (playoff == null) {
            //jpanel.add(new JLabel(messages.getString("areYouSure")));
            return null;
        }
        PlayoffPair[] pairs = playoff.getPlayoffPairs();
        if (pairs.length < 1) {
            return null;
        }

        for (int x = 0; x < pairs.length; x++) {
            if (x >= (playoffSize / 2) || pairs[x].getPlayedMatches() > 0) { //show the ones below the playoff-line or every pair that has games
                PlayoffPair pair = pairs[x];
                PlayoffPairTableModel pairmodel = new PlayoffPairTableModel(pair);
                JTable jtable2 = new JTable(pairmodel);
                setPlacementMatchTableRenderers(jtable2.getColumnModel());
                //jtable2.setShowGrid(true);
                //jtable2.setGridColor(Color.RED);
                jtable2.setShowVerticalLines(false);
                jtable2.setShowHorizontalLines(false);
                jtable2.setRowSelectionAllowed(false);
                jtable2.setColumnSelectionAllowed(false);
                //jtable2.setForeground(jpanel.getForeground());
                jtable2.setBackground(jpanel.getBackground());
                JTableHeader playofftableheader = jtable2.getTableHeader();
                playofftableheader.setReorderingAllowed(false);
                if (x == 0) {
                    jpanel.add(playofftableheader);
                }
                if (x == tournament.getLargestPlayoff() / 2) {
                    jpanel.add(Box.createRigidArea(new Dimension(15, 15)));
                }
                jpanel.add(Box.createRigidArea(new Dimension(5, 5)));
                jpanel.add(jtable2);
                pairmodel.fireTableDataChanged();
            }
        }
        //if (size > 2) {
        //    jpanel.add(Box.createRigidArea(new Dimension(5, 15)));
        //    jpanel.add(createNextRoundButton(size / 2));
        //}
        SaveTracker.setIsSaved(false);

        //scrollpanel has to added here for some reason
        JPanel outerpanel = new JPanel();
        JScrollPane playoffscrollpane = new JScrollPane(jpanel);
        //playoffscrollpane.setPreferredSize(new Dimension(640, 640));
        Dimension playofpanesize = placementmatchpane.getSize();
        double width = playofpanesize.getWidth();
        if (width == 0) {
            width = 720;
        }
        //playofpanesize.setSize(playofpanesize.getWidth(), 800);
        playofpanesize.setSize(width, 800);
        playoffscrollpane.setPreferredSize(playofpanesize);
        outerpanel.add(playoffscrollpane);

        return outerpanel;
    }

    public static JPanel createBronzeMatchPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
        jpanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Playoff playoff = tournament.getBronzeMatch(); //losers fight for bronze
        if (playoff == null) {
            //jpanel.add(new JLabel(messages.getString("areYouSure")));
            return null;
        }
        PlayoffPair[] pairs = playoff.getPlayoffPairs();
        for (int x = 0; x < pairs.length; x++) {
            PlayoffPair pair = pairs[x];
            PlayoffPairTableModel pairmodel = new PlayoffPairTableModel(pair);
            JTable jtable2 = new JTable(pairmodel);
            setPlacementMatchTableRenderers(jtable2.getColumnModel());
            //jtable2.setShowGrid(true);
            //jtable2.setGridColor(Color.RED);
            jtable2.setShowVerticalLines(false);
            jtable2.setShowHorizontalLines(false);
            jtable2.setRowSelectionAllowed(false);
            jtable2.setColumnSelectionAllowed(false);
            //jtable2.setForeground(jpanel.getForeground());
            jtable2.setBackground(jpanel.getBackground());
            JTableHeader playofftableheader = jtable2.getTableHeader();
            playofftableheader.setReorderingAllowed(false);
            if (x == 0) {
                jpanel.add(playofftableheader);
            }
            /*
            if (x == tournament.getLargestPlayoff() / 2) {
            jpanel.add(Box.createRigidArea(new Dimension(15, 15)));
            }
             */
            jpanel.add(Box.createRigidArea(new Dimension(5, 5)));
            jpanel.add(jtable2);
            pairmodel.fireTableDataChanged();
        }
        //if (size > 2) {
        //    jpanel.add(Box.createRigidArea(new Dimension(5, 15)));
        //    jpanel.add(createNextRoundButton(size / 2));
        //}
        SaveTracker.setIsSaved(false);

        return jpanel;
    }

    /**
     * each division, playoff and placementmatches has their own content
     * @return
     */
    private static JPanel createDivisionCards() {
        JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3), BorderFactory.createLineBorder(Color.black)), " " + messages.getString("selectedGroup") + " "));
        jpanel.setLayout(new CardLayout());
        int divisions_i = tournament.size();
        JTabbedPane ajtabbedpane[] = new JTabbedPane[divisions_i + 1];
        for (int j = 0; j < divisions_i; j++) {
            ajtabbedpane[j] = new JTabbedPane();
        }
        for (int k = 0; k < divisions_i; k++) {
            JScrollPane jscrollpane = new JScrollPane(createMatchListTable(k), 20, 31);
            jscrollpane.setPreferredSize(new Dimension(480, 540));
            ajtabbedpane[k].addTab(messages.getString("matches"), jscrollpane);
            JPanel jpanel1 = new JPanel();
            Division division = tournament.getDivision(k);
            SeriesTableModel seriestablemodel = new SeriesTableModel(division);
            JTable jtable = new JTable(seriestablemodel);
            setSeriesTableRenderers(jtable.getColumnModel());
            jtable.setShowVerticalLines(false);
            jtable.setShowHorizontalLines(false);
            jtable.setRowSelectionAllowed(false);
            jtable.setColumnSelectionAllowed(false);
            JTableHeader jtableheader = jtable.getTableHeader();
            jtableheader.setReorderingAllowed(false);
            jpanel1.setLayout(new BorderLayout());
            jpanel1.add(jtableheader, "North");
            jpanel1.add(jtable, "Center");
            jpanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6), BorderFactory.createLineBorder(Color.black)), "  " + messages.getString("seriesTable") + " / " + division.getTitle() + "  "));
            ajtabbedpane[k].addTab(messages.getString("seriesTable"), jpanel1);
            ajtabbedpane[k].addChangeListener(new SeriesTableListener(seriestablemodel));

            //html-mutualtable inside a tab
            if (System.getProperty("TournamentShowMutualTableTab").equalsIgnoreCase("true")) {
                JPanel jpanel2 = new JPanel();
                JLabel jarea = new JLabel(division.createMutualTable());
                jarea.setFont(new Font("Times", 0, 10));
                jpanel2.setLayout(new BorderLayout());
                jpanel2.add(jarea, "Center");
                JScrollPane columnScrollPane = new JScrollPane(jpanel2);
                columnScrollPane.setSize(new Dimension(jpanel2.getSize()));
                ajtabbedpane[k].addTab(messages.getString("finalTables"), columnScrollPane);
            }

        }

        //add each division
        String as[] = tournament.getDivisionTitles();
        for (int l = 0; l < divisions_i; l++) {
            jpanel.add(ajtabbedpane[l], as[l]);
        }

        //And playoffs too
        //if(System.getProperty("TournamentShowPlayoffTab").equalsIgnoreCase("true")) {
        if (1 == 1) {
            jpanel.add(createPlayoff(), messages.getString("playoff"));
        //jpanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6), BorderFactory.createLineBorder(Color.black)), "  " + "n. kierros"));            
        }
        //if(System.getProperty("TournamentShowPlayoffTab").equalsIgnoreCase("true")) {
        //initial placementmatchpanel for all players?
        if (1 == 1) {
            jpanel.add(createPlacementMatches(), messages.getString("placementMatches"));
        //jpanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6), BorderFactory.createLineBorder(Color.black)), "  " + "n. kierros"));            
        }

        return jpanel;
    }
    private static Locale locale;
    private static ResourceBundle messages;
    private static ResourceBundle keyCodes;
    private static Tournament tournament;
    private static MainWindow themainwindow; //hack
    private static JTabbedPane playoffpane;
    private static JTabbedPane placementmatchpane;
    private static Component bronzematchtab;
    private static Component placementmatchestab;
    private static ActionListener createlistener;
    private static ActionListener createplacementmatchlistener;
    private static DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {

        public void setValue(Object obj) {
            setHorizontalAlignment(2);
            setText((String) obj);
        }
    };
    private static DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {

        public void setValue(Object obj) {
            setHorizontalAlignment(0);
            setText((String) obj);
        }
    };
    static playoffRenderer playoffRenderer = new playoffRenderer();

    static class playoffRenderer extends DefaultTableCellRenderer {

        private int row,  col;

        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            // Save row and column information for use in setValue().
            this.row = row;
            this.col = column;

            // Allow superclass to return rendering component.
            return super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus,
                    row, column);
        }

        protected void setValue(Object obj) {
            // Allow superclass to set the value.
            super.setValue(obj);

            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.TOP);
            if ((this.col == 1 && this.row < 2) || (this.row == 0 && this.col > 2)) {
                //System.out.println(this.getClass().getName() + " " + this.getText());
                //setForeground (Color.white);
                setBackground(Color.white);
            } else {
                setBackground(UIManager.getColor("Panel.background"));
            //setBackground(Color.LIGHT_GRAY);
            }
            setText((String) obj);
        }
    }


    static {
//        locale = new Locale(new String("fi"), new String("FI"));
//        messages = ResourceBundle.getBundle("Messages", locale);
//        keyCodes = ResourceBundle.getBundle("KeyCodeBundle", locale);
        locale = Constants.getInstance().getLocale();
        messages = Constants.getInstance().getMessages();
        keyCodes = Constants.getInstance().getKeyCodes();
    }
}
