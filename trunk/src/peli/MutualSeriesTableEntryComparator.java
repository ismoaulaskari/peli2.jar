package peli;

import java.io.Serializable;
import java.util.Comparator;

/** Comparison of series table
 * that has mutual ordering before overall ordering 
 * @author aulaskar
 *
 */
public class MutualSeriesTableEntryComparator
    implements Comparator, Serializable
{

    public MutualSeriesTableEntryComparator()
    {
    }

    public int compare(Object obj, Object obj1)
    {
        MutualSeriesTableEntry seriestableentry = (MutualSeriesTableEntry)obj;
        MutualSeriesTableEntry seriestableentry1 = (MutualSeriesTableEntry)obj1;
        if(seriestableentry.getGames() == 0 && seriestableentry1.getGames() != 0)
            return 1;
        if(seriestableentry1.getGames() == 0 && seriestableentry.getGames() != 0)
            return -1;
        if(seriestableentry1.getPoints() != seriestableentry.getPoints())
            return seriestableentry1.getPoints() - seriestableentry.getPoints();
        if(seriestableentry1.goalDifference() != seriestableentry.goalDifference())
            return seriestableentry1.goalDifference() - seriestableentry.goalDifference();
        if(seriestableentry1.getScored() != seriestableentry.getScored())
            return seriestableentry1.getScored() - seriestableentry.getScored();
        if(seriestableentry1.getOverallEntry() != null && seriestableentry.getOverallEntry() != null) {
            SeriesTableEntryComparator tmp = new SeriesTableEntryComparator();
        	return tmp.compare(seriestableentry.getOverallEntry(), seriestableentry1.getOverallEntry());
        }
        else
            return seriestableentry.getName().compareTo(seriestableentry1.getName());
    }
}


