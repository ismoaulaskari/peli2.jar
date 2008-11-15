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
public class RoundTableModel extends AbstractTableModel
{
    private ResourceBundle messages = Constants.getInstance().getMessages();
    
    final String columnNames[] = {        
        messages.getString("table"), messages.getString("homeTeam"),
        messages.getString("awayTeam"), messages.getString("result")
    };
    private Round round;

    RoundTableModel(Round round1)
    {
        round = round1;
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public int getRowCount()
    {
        return round.getNumberOfMatches();
    }

    public String getColumnName(int i)
    {
        return columnNames[i];
    }

    public Object getValueAt(int i, int j)
    {
        Match match = round.getMatch(i);
        switch(j)
        {
        case 0: // '\0'
            return "" + (i + 1);

        case 1: // '\001'
            return match.home();

        case 2: // '\002'
            return match.visitor();

        case 3: // '\003'
            if(isDummyMatch(i))
                return "xxx";
            else
                return match.getResult();
        }
        return "";
    }

    public Class getColumnClass(int i)
    {
        return getValueAt(0, i).getClass();
    }

    public boolean isCellEditable(int i, int j)
    {
        if(j < 3)
            return false;
        return !isDummyMatch(i);
    }

    private boolean isDummyMatch(int i)
    {
        Match match = round.getMatch(i);
        String s = match.home();
        String s1 = match.visitor();
        return s.equals("X") || s1.equals("X");
    }

    public void setValueAt(Object obj, int i, int j)
    {
        if(j != 3)
            return;
        SaveTracker.isSaved = false;
        Match match = round.getMatch(i);
        SeriesTableEntry seriestableentry = round.getDivision().getSeriesTableEntry(match.home());
        SeriesTableEntry seriestableentry1 = round.getDivision().getSeriesTableEntry(match.visitor());
        if(match.isOver())
        {
            seriestableentry.cancelMatch(match);
            seriestableentry1.cancelMatch(match);
        }
        match.setResult((String)obj);
        seriestableentry.updateWith(match);
        seriestableentry1.updateWith(match);
        fireTableCellUpdated(i, j);
    }

}
