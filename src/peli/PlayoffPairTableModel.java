package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RoundTableModel.java
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/** 
 * Gui table where you type in the results
 * Put words in a propertyfile.
 * @author aulaskar
 *
 */
public class PlayoffPairTableModel extends AbstractTableModel {

    private ResourceBundle messages = Constants.getInstance().getMessages();
    final String columnNames[] = {
        messages.getString("playerName"), messages.getString("playoffWins"),
        "", messages.getString("playoffResults"), "", "", "", "", ""
    };
    private PlayoffPair playoffpair;
    //private String homeTeam;
    //private String awayTeam;
    //private ArrayList matches;
    PlayoffPairTableModel(PlayoffPair playoffpair) {
        this.playoffpair = playoffpair;
//        this.homeTeam = playoffpair.getHomeTeam();
//        this.awayTeam = playoffpair.getAwayTeam();
//        this.matches = playoffpair.getMatches();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return 2;
    }

    public String getColumnName(int i) {
        return columnNames[i];
    }

    public Object getValueAt(int i, int j) {
        Match match = (Match) playoffpair.getMatches().get(i);
        switch (i) {
            case 0:
                if (j == 0) {
                    return playoffpair.getHomeTeam();
                } else {
                    return playoffpair.getAwayTeam();
                }

            case 1:
                if (j == 0) {
                    return playoffpair.getHomeWins();
                } else {
                    return playoffpair.getAwayWins();
                }
        }

        if (j == 0) {
            if (isDummyMatch(i)) {
                return "xxx";
            } else {
                return match.getResult();
            }
        } else {
            return "";
        }
    }

    public Class getColumnClass(int i) {
        return getValueAt(0, i).getClass();
    }

    public boolean isCellEditable(int i, int j) {
        if (j > 1) {
            return false;
        }
        return !isDummyMatch(i);
    }

    private boolean isDummyMatch(int i) {
        Match match = (Match) playoffpair.getMatches().get(i);
        String s = match.home();
        String s1 = match.visitor();
        return s.equals("X") || s1.equals("X");
    }

    public void setValueAt(Object obj, int i, int j) {
        if (j > 1 || i < 0) {
            return;
        }
        SaveTracker.isSaved = false;

        if (i == 0) {
            if (j == 0) {
                this.playoffpair.setHomeTeam((String) obj);
            } else if (j == 1) {
                this.playoffpair.setAwayTeam((String) obj);
            }
        } else if (i == 1) {
            return;
        } else if (i > 1 && j == 0) {
            Match match = (Match) playoffpair.getMatches().get(i);
            //SeriesTableEntry seriestableentry = round.getDivision().getSeriesTableEntry(match.home());
            //SeriesTableEntry seriestableentry1 = round.getDivision().getSeriesTableEntry(match.visitor());
            if (match.isOver()) {
                //seriestableentry.cancelMatch(match);
                //seriestableentry1.cancelMatch(match);
            }
            match.setResult((String) obj);
            //survive on direct references?
            //seriestableentry.updateWith(match);
            //seriestableentry1.updateWith(match);
            this.playoffpair.updateWins();
        }

        fireTableCellUpdated(i, j);
    }

    public void finalize() {
        this.playoffpair = null;
    }
}
