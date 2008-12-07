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
        SaveTracker.isSaved = false;
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
                if (SaveTracker.isSaved) {
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
                        e.printStackTrace();		// TODO: handle exception
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

        imageicon = new ImageIcon(messages.getString("printIconGifFile"));
        jbutton = new JButton(messages.getString("mHtmlButton"), imageicon);
        jbutton.setToolTipText(messages.getString("mHtmlToolTip"));
        jbutton.setMnemonic(((Integer) keyCodes.getObject("mHtmlMnemonic")).intValue());
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 1, frame));
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

        imageicon = new ImageIcon(messages.getString("printIconGifFile"));
        jbutton = new JButton(messages.getString("htmlButton"), imageicon);
        jbutton.setMnemonic(((Integer) keyCodes.getObject("htmlMnemonic")).intValue());
        jbutton.setToolTipText(messages.getString("htmlToolTip"));
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 3, frame));
        jtoolbar.add(jbutton);
        imageicon = new ImageIcon(messages.getString("saveAllIconGifFile"));
        jbutton = new JButton(messages.getString("saveButton"), imageicon);
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
                if (SaveTracker.isSaved) {
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

    private static JPanel createComboBoxPane(final JPanel cards) {
        JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3), BorderFactory.createLineBorder(Color.black)), " " + messages.getString("chooseGroup") + " "));
        JComboBox jcombobox = new JComboBox(tournament.getDivisionTitles());
        jcombobox.setEditable(false);
        jcombobox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent itemevent) {
                CardLayout cardlayout = (CardLayout) cards.getLayout();
                cardlayout.show(cards, (String) itemevent.getItem());
            }
        });
        jpanel.add(jcombobox);
        return jpanel;
    }

    /*
    private static JPanel createPlayoffTable() {
    //holds a pair of matches
    JPanel allPanel = new JPanel();
    allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
    allPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    for (int i = 0; i < 8; i++) {
    allPanel.add(createPlayoffPair());
    }
    
    return allPanel;
    }
    
    
    private static JPanel createPlayoffPair() {
    //holds everything
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    
    //holds a pair of matches
    JPanel pairPanel = new JPanel();
    pairPanel.setLayout(new BoxLayout(pairPanel, BoxLayout.Y_AXIS));
    pairPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    
    //holds name1 and win count1
    JPanel namePanel = new JPanel();
    namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
    namePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    
    //holds name2 and win count2
    JPanel name2Panel = new JPanel();
    name2Panel.setLayout(new BoxLayout(name2Panel, BoxLayout.X_AXIS));
    name2Panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    
    //holds scores
    JPanel scorePanel = new JPanel();
    scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.X_AXIS));
    scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    String name = "Tuomo Ala-Kojola";
    JTextField player = new JTextField(name);
    Dimension dz = player.getSize();
    player.setPreferredSize(dz);
    //        player.setMaximumSize(dz);
    player.setMinimumSize(dz);
    namePanel.add(player);
    
    String name2 = "Ismo Lahtinen";
    JTextField player2 = new JTextField(name2);
    player2.setPreferredSize(dz);
    //        player2.setMaximumSize(dz);
    player2.setMinimumSize(dz);
    name2Panel.add(player2);
    
    namePanel.add(new JLabel("4"));
    name2Panel.add(new JLabel("3"));
    
    namePanel.setMaximumSize(new Dimension(270, 40));
    name2Panel.setMaximumSize(new Dimension(270, 40));
    
    //seven results
    scorePanel.add(Box.createRigidArea(new Dimension(5, 5)));
    for (int i = 0; i < 7; i++) {
    JTextField jt = new JTextField("        ");
    Dimension dj = jt.getSize();
    jt.setPreferredSize(dj);
    //            jt.setMaximumSize(jt.getSize());
    jt.setMinimumSize(dj);
    scorePanel.add(jt).setBounds(jt.getBounds());
    }
    
    //tyhjää väliin
    scorePanel.add(Box.createRigidArea(new Dimension(5, 5)));
    scorePanel.setMaximumSize(new Dimension(540, 50));
    
    //namesPanel.add(Box.createHorizontalGlue());
    pairPanel.add(Box.createRigidArea(new Dimension(10, 10)));
    pairPanel.add(namePanel);
    pairPanel.add(name2Panel);
    pairPanel.add(Box.createRigidArea(new Dimension(10, 10)));
    pairPanel.setMaximumSize(new Dimension(320, 100));
    bottomPanel.add(pairPanel);
    bottomPanel.add(scorePanel);
    
    return bottomPanel;
    }*/
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
            jpanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6), BorderFactory.createLineBorder(Color.black)), "  " + messages.getString("round") + " " + (j + 1) + "  "));
            jpanel.add(jpanel1);
        }

        return jpanel;
    }

    private static void setPlayoffTableRenderers(TableColumnModel tablecolumnmodel) {
        //names                        
        TableColumn tablecolumn = tablecolumnmodel.getColumn(0);
        tablecolumn.setPreferredWidth(150);
        tablecolumn.setMaxWidth(150);
        tablecolumn.setCellRenderer(playoffRenderer);
        //wins
        tablecolumn = tablecolumnmodel.getColumn(1);
        tablecolumn.setCellRenderer(playoffRenderer);
        tablecolumn.setPreferredWidth(30);

        //games
        for (int xx = 0; xx < 7; xx++) {
            tablecolumn = tablecolumnmodel.getColumn(xx + 2);
            tablecolumn.setCellRenderer(playoffRenderer);
            tablecolumn.setPreferredWidth(50);
        }
    //tablecolumn = tablecolumnmodel.getColumn(3);
    //tablecolumn.setCellRenderer(centerRenderer);
    //tablecolumn.setPreferredWidth(40);
    //tablecolumn.setMaxWidth(50);
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

    private static JPanel createDivisionCards() {
        JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3), BorderFactory.createLineBorder(Color.black)), " " + messages.getString("selectedGroup") + " "));
        jpanel.setLayout(new CardLayout());
        int i = tournament.size();
        JTabbedPane ajtabbedpane[] = new JTabbedPane[i];
        for (int j = 0; j < i; j++) {
            ajtabbedpane[j] = new JTabbedPane();
        }
        for (int k = 0; k < i; k++) {
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

            //playoff inside a tab
            /*
            //if(System.getProperty("TournamentShowPlayoffTab").equalsIgnoreCase("true")) {
            if(1==1) {
            //JPanel jpanel2 = new JPanel();
            JPanel jpanel2 = createPlayoffTable();
            //JLabel jarea = new JLabel(tournament.getFormattedStandings()); 
            //jarea.setFont(new Font("Times", 0, 10));
            //jpanel2.setLayout(new BorderLayout());
            //jpanel2.add(jarea,"Center");
            JScrollPane columnScrollPane = new JScrollPane(jpanel2);
            columnScrollPane.setSize(new Dimension(jpanel2.getSize()));
            ajtabbedpane[k].addTab(messages.getString("playoff"), columnScrollPane);
            }
             */

            //if(System.getProperty("TournamentShowPlayoffTab").equalsIgnoreCase("true")) {
            if (1 == 1) {
                JPanel jpanel2 = new JPanel();                
                jpanel2.setLayout(new BoxLayout(jpanel2, BoxLayout.Y_AXIS));
                jpanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                Playoff playoff = tournament.getPlayoff();
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
                    JTableHeader playofftableheader = jtable2.getTableHeader();
                    //jtable2.setForeground(jpanel2.getForeground());
                    jtable2.setBackground(jpanel2.getBackground());
                    playofftableheader.setReorderingAllowed(false);                    
                    if (x == 0) {
                        jpanel2.add(playofftableheader);
                    }
                    jpanel2.add(Box.createRigidArea(new Dimension(5, 5)));
                    jpanel2.add(jtable2);
                    
                }
                JScrollPane columnScrollPane = new JScrollPane(jpanel2);
                columnScrollPane.setSize(new Dimension(jpanel2.getSize()));
                ajtabbedpane[k].addTab(messages.getString("playoff"), columnScrollPane);
            jpanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6), BorderFactory.createLineBorder(Color.black)), "  " + "n. kierros"));
            //ajtabbedpane[k].addTab(messages.getString("seriesTable"), jpanel2);
            //ajtabbedpane[k].addChangeListener(new SeriesTableListener(seriestablemodel)); //?
            }
        }

        String as[] = tournament.getDivisionTitles();
        for (int l = 0; l < i; l++) {
            jpanel.add(ajtabbedpane[l], as[l]);
        }
        return jpanel;
    }
    private static Locale locale;
    private static ResourceBundle messages;
    private static ResourceBundle keyCodes;
    private static Tournament tournament;
    private static MainWindow themainwindow; //hack
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
            if ((this.col == 0 && this.row < 2) || (this.row == 0 && this.col > 1)) {
                //System.out.println(this.getClass().getName() + " " + this.getText());
                //setForeground (Color.white);
                setBackground(Color.white);
            } else {
                setBackground(UIManager.getColor ("Panel.background"));
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
