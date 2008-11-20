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
public class TournamentGUI extends JPanel
{

    TournamentGUI(MainWindow mainwindow, Tournament tournament1, File file)
    {
        System.setProperty("TournamentFileName", file.getName()); //hack, throws e
        //System.setProperty("TemplateTitle", file.getName()); 
        tournament = tournament1;
        themainwindow = mainwindow; //hack
        createThis(mainwindow, file);
//      add line to fix dangerous close-button:
        mainwindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    TournamentGUI(MainWindow mainwindow, int i, TreeSet treeset, File file)
    {
        System.setProperty("TournamentFileName", file.getName()); //hack, throws e
        //System.setProperty("TemplateTitle", file.getName().substring(0, file.getName().indexOf('.'))); 
        SaveTracker.isSaved = false;
        tournament = new Tournament(i, treeset);
        themainwindow = mainwindow; //hack
        createThis(mainwindow, file);
//      add line to fix dangerous close-button:
        mainwindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    private void createThis(MainWindow mainwindow, File file)
    {
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

    private JToolBar createToolBar(final Component frame, Tournament tournament1, File file)
    {
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
        	public void actionPerformed(ActionEvent actionevent)
            {
                if(SaveTracker.isSaved) {
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
                	}
                	catch (Throwable e) {
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
                switch(i)
                {
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
        jbutton.setMnemonic(((Integer)keyCodes.getObject("mHtmlMnemonic")).intValue());
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
        jbutton.setMnemonic(((Integer)keyCodes.getObject("htmlMnemonic")).intValue());
        jbutton.setToolTipText(messages.getString("htmlToolTip"));
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 3, frame));
        jtoolbar.add(jbutton);
        imageicon = new ImageIcon(messages.getString("saveAllIconGifFile"));
        jbutton = new JButton(messages.getString("saveButton"), imageicon);
        jbutton.setMnemonic(((Integer)keyCodes.getObject("saveMnemonic")).intValue());
        jbutton.setToolTipText(messages.getString("saveToolTip"));
        jbutton.addActionListener(new SaveActionListener(file, tournament1, 2, frame));
        jtoolbar.add(jbutton);


       	
        imageicon = new ImageIcon(messages.getString("quitIconGifFile"));
        jbutton = new JButton(messages.getString("quitButton"), imageicon);
        jbutton.setToolTipText(messages.getString("quitToolTip"));
        jbutton.setMnemonic(((Integer)keyCodes.getObject("quitMnemonic")).intValue());
        jbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                if(SaveTracker.isSaved)
                    System.exit(0);
                Object aobj[] = {
                    TournamentGUI.messages.getString("doNotQuit"), TournamentGUI.messages.getString("doQuit")
                };
                int i = JOptionPane.showOptionDialog(frame, TournamentGUI.messages.getString("areYouSure") + "\n" + TournamentGUI.messages.getString("youDidNotSave"), TournamentGUI.messages.getString("reallyQuit"), 0, 3, null, aobj, aobj[0]);
                switch(i)
                {
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

    private static JPanel createComboBoxPane(final JPanel cards)
    {
        JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3), BorderFactory.createLineBorder(Color.black)), " " + messages.getString("chooseGroup") + " "));
        JComboBox jcombobox = new JComboBox(tournament.getDivisionTitles());
        jcombobox.setEditable(false);
        jcombobox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent itemevent)
            {
                CardLayout cardlayout = (CardLayout)cards.getLayout();
                cardlayout.show(cards, (String)itemevent.getItem());
            }

        });
        jpanel.add(jcombobox);
        return jpanel;
    }

    private static JPanel createPlayoffTable() {
        JPanel jpanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        jpanel.add(new JTextField("foo"), c);
        jpanel.add(new JTextField("bar"), c);
        jpanel.add(new JTextField("bal"), c);
        
        return jpanel;
    }
    
    private static JPanel createMatchListTable(int i)
    {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BoxLayout(jpanel, 1));
        Division division = tournament.getDivision(i);
        for(int j = 0; j < division.getNumberOfRounds(); j++)
        {
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

    private static void setRoundTableRenderers(TableColumnModel tablecolumnmodel, int i)
    {
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

    private static void setSeriesTableRenderers(TableColumnModel tablecolumnmodel)
    {
        for(int i = 0; i < tablecolumnmodel.getColumnCount(); i++)
        {
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

    private static JPanel createDivisionCards()
    {
        JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3), BorderFactory.createLineBorder(Color.black)), " " + messages.getString("selectedGroup") + " "));
        jpanel.setLayout(new CardLayout());
        int i = tournament.size();
        JTabbedPane ajtabbedpane[] = new JTabbedPane[i];
        for(int j = 0; j < i; j++)
            ajtabbedpane[j] = new JTabbedPane();

        for(int k = 0; k < i; k++)
        {
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
            if(System.getProperty("TournamentShowMutualTableTab").equalsIgnoreCase("true")) {
            	JPanel jpanel2 = new JPanel();
            	JLabel jarea = new JLabel(division.createMutualTable()); 
            	jarea.setFont(new Font("Times", 0, 10));
            	jpanel2.setLayout(new BorderLayout());
            	jpanel2.add(jarea,"Center");
            	JScrollPane columnScrollPane = new JScrollPane(jpanel2);
            	columnScrollPane.setSize(new Dimension(jpanel2.getSize()));
            	ajtabbedpane[k].addTab(messages.getString("finalTables"), columnScrollPane);
            }
            
            //playoff inside a tab
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
        }

        String as[] = tournament.getDivisionTitles();
        for(int l = 0; l < i; l++)
            jpanel.add(ajtabbedpane[l], as[l]);

        return jpanel;
    }

    private static Locale locale;
    private static ResourceBundle messages;
    private static ResourceBundle keyCodes;
    private static Tournament tournament;
    private static MainWindow themainwindow; //hack
    private static DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {

        public void setValue(Object obj)
        {
            setHorizontalAlignment(2);
            setText((String)obj);
        }

    };
    private static DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {

        public void setValue(Object obj)
        {
            setHorizontalAlignment(0);
            setText((String)obj);
        }

    };

    static 
    {
//        locale = new Locale(new String("fi"), new String("FI"));
//        messages = ResourceBundle.getBundle("Messages", locale);
//        keyCodes = ResourceBundle.getBundle("KeyCodeBundle", locale);
    	locale = Constants.getInstance().getLocale();
    	messages = Constants.getInstance().getMessages();
    	keyCodes = Constants.getInstance().getKeyCodes();
    }

}
