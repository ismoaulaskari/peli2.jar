package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SeriesTableEntry.java

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * data item in series table
 * v1.1 added changeable points system from propertiesfile
 * File seems to contain a 24-char limit for a player name
 * @author aulaskar
 *
 */
public class SeriesTableEntry extends Player
{

    SeriesTableEntry(String s)
    {
    	super(0, s); //hacked to extend player for mutual comparison casting
        wins = 0;
        ties = 0;
        losses = 0;
        goalsScored = 0;
        goalsYielded = 0;
        playerName = s;
        isInMutualComparison = -1;
        
        //hack
        if(pointsPerWin == 0) {
        	try {
        		pointsPerWin = Integer.parseInt(System.getProperty("TournamentPointsPerWin"));
        		pointsPerTie = Integer.parseInt(System.getProperty("TournamentPointsPerTie"));
        	}
        	catch (Exception e) {
        		System.err.println("No Rules.properties ?");
        		pointsPerWin = 2;
        		pointsPerTie = 1;
        	}
        }
    }

    public String getName()
    {
        return playerName;
    }

    public int getGames()
    {
        return wins + ties + losses;
    }

    public int getWins()
    {
        return wins;
    }

    public int getTies()
    {
        return ties;
    }

    public int getLosses()
    {
        return losses;
    }

    public int getScored()
    {
        return goalsScored;
    }

    public int getYielded()
    {
        return goalsYielded;
    }

    public int getPoints()
    {
        return (pointsPerWin * wins) + (pointsPerTie * ties);
    }

    public int getHasTiedPoints() {
    	return isInMutualComparison;
    }
    
    public void setHasTiedPoints(int points1) {
    	this.isInMutualComparison = points1;
    }
    
    public void increaseWins()
    {
        wins++;
    }

    public void increaseTies()
    {
        ties++;
    }

    public void increaseLosses()
    {
        losses++;
    }

    public void score(int i)
    {
        goalsScored += i;
    }

    public void yield(int i)
    {
        goalsYielded += i;
    }

    public void decreaseWins()
    {
        wins--;
    }

    public void decreaseTies()
    {
        ties--;
    }

    public void decreaseLosses()
    {
        losses--;
    }

    public void unscore(int i)
    {
        goalsScored -= i;
    }

    public void unyield(int i)
    {
        goalsYielded -= i;
    }

    public int goalDifference()
    {
        return goalsScored - goalsYielded;
    }

    public String getHtmlTableRow()
    {
        String s = HtmlTools.td(getName()) + HtmlTools.td(getGames()) + HtmlTools.td(getWins()) + HtmlTools.td(getTies()) + HtmlTools.td(getLosses()) + HtmlTools.td(getScored()) + HtmlTools.td("-") + HtmlTools.td(getYielded()) + HtmlTools.td(getPoints());
        return HtmlTools.tr(s);
    }

    public String getRow()
    {
        return Tools.format(playerName, 24) + Tools.format(getGames(), 3) + Tools.format(getWins(), 3) + Tools.format(getTies(), 3) + Tools.format(getLosses(), 3) + Tools.format(getScored(), 4) + "-" + Tools.format(getYielded() + "", 3) + Tools.format(getPoints(), 4);
    }

    public void print(PrintWriter printwriter)
    {
        printwriter.println(getRow());
    }

    public void print()
    {
        System.out.println(getRow());
    }

    public void updateWith(Match match)
    {
        if(!match.isOver())
            return;
        if(playerName.equals(match.home()))
        {
            score(match.homeGoals());
            yield(match.visitorGoals());
            if(match.homeGoals() > match.visitorGoals())
            {
                increaseWins();
                return;
            }
            if(match.homeGoals() < match.visitorGoals())
            {
                increaseLosses();
                return;
            } else
            {
                increaseTies();
                return;
            }
        }
        if(playerName.equals(match.visitor()))
        {
            score(match.visitorGoals());
            yield(match.homeGoals());
            if(match.homeGoals() > match.visitorGoals())
            {
                increaseLosses();
                return;
            }
            if(match.homeGoals() < match.visitorGoals())
            {
                increaseWins();
                return;
            } else
            {
                increaseTies();
                return;
            }
        } else
        {
            return;
        }
    }

    public void cancelMatch(Match match)
    {
        if(!match.isOver())
            return;
        if(playerName.equals(match.home()))
        {
            unscore(match.homeGoals());
            unyield(match.visitorGoals());
            if(match.homeGoals() > match.visitorGoals())
            {
                decreaseWins();
                return;
            }
            if(match.homeGoals() < match.visitorGoals())
            {
                decreaseLosses();
                return;
            } else
            {
                decreaseTies();
                return;
            }
        }
        if(playerName.equals(match.visitor()))
        {
            unscore(match.visitorGoals());
            unyield(match.homeGoals());
            if(match.homeGoals() > match.visitorGoals())
            {
                decreaseLosses();
                return;
            }
            if(match.homeGoals() < match.visitorGoals())
            {
                decreaseWins();
                return;
            } else
            {
                decreaseTies();
                return;
            }
        } else
        {
            return;
        }
    }

    private String playerName;
    private int wins;
    private int ties;
    private int losses;
    private int goalsScored;
    private int goalsYielded;
    private static int pointsPerWin = 0;
    private static int pointsPerTie = 0;
    private int isInMutualComparison = -1; //is tied at x points
}
