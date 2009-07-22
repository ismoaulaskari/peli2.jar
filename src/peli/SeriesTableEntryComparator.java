package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SeriesTableEntryComparator.java

import java.io.Serializable;
import java.util.Comparator;

/** Comparison of series table
 * by the goal difference after points scored 
 * @author aulaskar
 *
 */
public class SeriesTableEntryComparator
        implements Comparator, Serializable {

    public SeriesTableEntryComparator() {
    }

    public int compare(Object obj, Object obj1) {
        SeriesTableEntry seriestableentry = (SeriesTableEntry) obj;
        SeriesTableEntry seriestableentry1 = (SeriesTableEntry) obj1;
        if (seriestableentry.getGames() == 0 && seriestableentry1.getGames() != 0) {
            return 1;
        }
        if (seriestableentry1.getGames() == 0 && seriestableentry.getGames() != 0) {
            return -1;
        }
        if (seriestableentry1.getPoints() != seriestableentry.getPoints()) {
            return seriestableentry1.getPoints() - seriestableentry.getPoints();
        }
        if (seriestableentry1.goalDifference() != seriestableentry.goalDifference()) {
            return seriestableentry1.goalDifference() - seriestableentry.goalDifference();
        }
        if (seriestableentry1.getScored() != seriestableentry.getScored()) {
            return seriestableentry1.getScored() - seriestableentry.getScored();
        }
        if (seriestableentry.getRank() != seriestableentry1.getRank()) {
            return seriestableentry.getRank() - seriestableentry1.getRank();
        } else {
            return seriestableentry.getName().compareTo(seriestableentry1.getName());
        }
    }
}
