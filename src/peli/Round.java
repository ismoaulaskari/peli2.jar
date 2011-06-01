package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Round.java

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *some mapping between the tournament rounds in memory and in the tnmt-file
 * @author aulaskar
 *
 */
public class Round
{

    private Locale locale;
    private ResourceBundle messages;
    private Division mother;
    private int number;
    private String pausePlayer;
    private Match matches[];
    private int numberOfMatches;

    Round(Division division, int numOfMatches, String seats[])
    {
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);*/
    	locale = Constants.getInstance().getLocale();
    	messages = Constants.getInstance().getMessages();
        mother = division;
        number = numOfMatches;
        pausePlayer = seats[0];
        numberOfMatches = seats.length / 2;
        matches = new Match[numberOfMatches];
        try
        {
            for(int j = 1; j <= numberOfMatches; j++)
                matches[j - 1] = new Match(seats[j] + ':' + seats[seats.length - j]);

        }
        catch(FileFormatException fileformatexception) { }
    }

    Round(Division division, int numOfMatches, BufferedReader bufferedreader)
        throws FileFormatException, IOException
    {
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);*/
    	locale = Constants.getInstance().getLocale();
    	messages = Constants.getInstance().getMessages();
        mother = division;
        numberOfMatches = numOfMatches;
        matches = new Match[numOfMatches];
        try
        {
            number = Tools.parseIntAfter("ROUND:", bufferedreader.readLine());
            String s = bufferedreader.readLine();
            if(s.startsWith("("))
                pausePlayer = s.substring(1, s.length() - 1);
            else
                throw new FileFormatException();
            for(int j = 0; j < numOfMatches; j++)
            {
                Match match = new Match(bufferedreader.readLine());
                matches[j] = match;
                if(!match.isDummy())
                {
                    division.getMutual().insert(match);
                    division.getSeriesTableEntry(match.home()).updateWith(match);
                    division.getSeriesTableEntry(match.visitor()).updateWith(match);
                }
            }

            if(!bufferedreader.readLine().equals("END-OF-ROUND"))
                throw new FileFormatException();
        }
        catch(FileFormatException fileformatexception)
        {
            throw fileformatexception;
        }
        catch(IOException ioexception)
        {
            throw ioexception;
        }
    }

    public Match getMatch(int i)
    {
        return matches[i];
    }

    public void print()
    {
        System.out.println("\nRound " + number);
        System.out.println(messages.getString("pause") + ": " + pausePlayer);
        for(int i = 0; i < numberOfMatches; i++)
            matches[i].print();

    }

    public void save(PrintWriter printwriter)
    {
        printwriter.println("ROUND:" + getNumber());
        printwriter.println("(" + pausePlayer + ")");
        for(int i = 0; i < getNumberOfMatches(); i++)
            getMatch(i).save(printwriter);

        printwriter.println("END-OF-ROUND");
    }

    public void saveMatches(PrintWriter printwriter)
    {
        HtmlTools.tableIntro(printwriter, false);
        printwriter.println("<TR><TH COLSPAN=8>" + mother.getTitle() + " / " + messages.getString("round") + " " + number + "</TH></TR>");
        printwriter.println("<TR>");
        HtmlTools.td(printwriter, "0");
        HtmlTools.td(printwriter, ":");
        HtmlTools.td(printwriter, "<EM>" + messages.getString("pause") + ":</EM>");
        HtmlTools.td(printwriter, "&nbsp;");
        HtmlTools.td(printwriter, pausePlayer, 4);
        printwriter.println("</TR>");
        for(int i = 0; i < numberOfMatches; i++)
            printwriter.println(matches[i].getHtmlTableRow(i + 1));

        HtmlTools.tableOutro(printwriter);
    }

    public String getPausePlayer()
    {
        return pausePlayer;
    }

    public int getNumber()
    {
        return number;
    }

    public int getNumberOfMatches()
    {
        return numberOfMatches;
    }

    public Division getDivision()
    {
        return mother;
    }

    public void finalize() {
        this.mother = null;
        this.matches = null;        
    }
}
