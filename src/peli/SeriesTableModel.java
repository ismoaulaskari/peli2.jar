package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SeriesTableModel.java

import javax.swing.table.AbstractTableModel;

/** The seriestable in the Tournament GUI 
 *  Propertyfile?
 */
public class SeriesTableModel extends AbstractTableModel
{
    final String columnNames[] = {
        "#", "Pelaaja", "O", "V", "T", "H", "tm", "", "pm", "me", 
        "p"
    };
    private Division division;
    private SeriesTable seriesTable;

    SeriesTableModel(Division division1)
    {
        division = division1;
        seriesTable = division1.getSeriesTable();
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public int getRowCount()
    {
        return seriesTable.size();
    }

    public String getColumnName(int i)
    {
        return columnNames[i];
    }

    public Object getValueAt(int i, int j)
    {
        SeriesTableEntry seriestableentry = seriesTable.elementAt(i);
        switch(j)
        {
        case 0: // '\0'
            return "" + (i + 1);

        case 1: // '\001'
            return seriestableentry.getName();

        case 2: // '\002'
            return "" + seriestableentry.getGames();

        case 3: // '\003'
            return "" + seriestableentry.getWins();

        case 4: // '\004'
            return "" + seriestableentry.getTies();

        case 5: // '\005'
            return "" + seriestableentry.getLosses();

        case 6: // '\006'
            return "" + seriestableentry.getScored();

        case 7: // '\007'
            return "-";

        case 8: // '\b'
            return "" + seriestableentry.getYielded();

        case 9: // '\t'
            return "(" + seriestableentry.goalDifference() + ")";

        case 10: // '\n'
            return "" + seriestableentry.getPoints();
        }
        return "x";
    }

    public Class getColumnClass(int i)
    {
        return getValueAt(0, i).getClass();
    }

    public boolean isCellEditable(int i, int j)
    {
        return false;
    }

    public void update()
    {
        seriesTable = division.getSeriesTable();
        fireTableDataChanged();
    }

}
