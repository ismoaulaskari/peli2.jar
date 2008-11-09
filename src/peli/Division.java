package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Division.java

import java.io.*;
import java.util.*;

/** Creates, saves and prints a division
 * v1.1 internal html-results-tab
 * @author aulaskar
 *
 */
public class Division
{

    private String title;
    private TreeSet players;
    private Mutual mutual;
    private int numberOfPlayers;
    private Hashtable seriesTableEntries;
    private boolean hasDummyPlayer;
    private String seats[];
    private int times;
    private Round rounds[];

    private void makeInitialSeating()
    {
        int i = 0;
        for(Iterator iterator = players.iterator(); iterator.hasNext();)
            seats[i++] = ((Player)iterator.next()).getName();

        if(hasDummyPlayer)
        {
            seats[i] = seats[1];
            seats[1] = "X";
        }
    }

    private void makeNextSeating()
    {
        String s = seats[seats.length - 1];
        for(int i = seats.length - 1; i > 0; i--)
            seats[i] = seats[i - 1];

        seats[0] = s;
    }

    private void printSeating()
    {
        for(int i = 0; i < seats.length; i++)
            System.out.println("Paikka " + i + ": " + seats[i]);

    }

    private void buildAllRounds()
    {
        makeInitialSeating();
        for(int i = 0; i < rounds.length; i++)
        {
            rounds[i] = new Round(this, i + 1, seats);
            makeNextSeating();
        }

    }

    private void printAllRounds()
    {
        for(int i = 0; i < seats.length; i++)
            rounds[i].print();

    }

    Division(String s, int i, TreeSet treeset)
    {
        seriesTableEntries = new Hashtable();
        hasDummyPlayer = false;
        title = s;
        times = i;
        players = treeset;
        mutual = new Mutual(treeset);
        numberOfPlayers = treeset.size();
        hasDummyPlayer = treeset.size() % 2 == 0;
        buildSeriesTableEntries(players);
        int j = hasDummyPlayer ? treeset.size() + 1 : treeset.size();
        rounds = new Round[j * i];
        seats = new String[j];
        buildAllRounds();
    }

    Division(BufferedReader bufferedreader)
        throws FileFormatException, IOException
    {
        seriesTableEntries = new Hashtable();
        hasDummyPlayer = false;
        try
        {
            title = Tools.parseStringAfter("DIVISION:", bufferedreader.readLine());
            int i = Tools.parseIntAfter("PLAYERS:", bufferedreader.readLine());
            numberOfPlayers = i;
            players = new TreeSet(new PlayerComparator());
            for(int j = 0; j < i; j++)
                players.add(new Player(j + 1, bufferedreader.readLine()));

            if(!bufferedreader.readLine().equals("END-OF-PLAYERS"))
                throw new FileFormatException();
            hasDummyPlayer = i % 2 == 0;
            mutual = new Mutual(players);
            buildSeriesTableEntries(players);
            int k = Tools.parseIntAfter("ROUNDS:", bufferedreader.readLine());
            rounds = new Round[k];
            for(int l = 0; l < k; l++)
                rounds[l] = new Round(this, i / 2, bufferedreader);

            if(!bufferedreader.readLine().equals("END-OF-DIVISION"))
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

    private void buildSeriesTableEntries(TreeSet treeset)
    {
        String s;
        for(Iterator iterator = treeset.iterator(); iterator.hasNext(); seriesTableEntries.put(s, new SeriesTableEntry(s)))
            s = ((Player)iterator.next()).getName();

    }

    public Mutual getMutual()
    {
        return mutual;
    }

    public String getTitle()
    {
        return title;
    }

    public int getNumberOfPlayers()
    {
        return players.size();
    }

    public int getNumberOfRounds()
    {
        return rounds.length;
    }

    public Round getRound(int i)
    {
        return rounds[i];
    }

    public SeriesTableEntry getSeriesTableEntry(String s)
    {
        return (SeriesTableEntry)seriesTableEntries.get(s);
    }

    public SeriesTable getSeriesTable()
    {
        return new SeriesTable(seriesTableEntries, mutual);
    }

    //tnmt-file division writing
    public void save(PrintWriter printwriter)
    {
        printwriter.println("DIVISION:" + getTitle());
        printwriter.println("PLAYERS:" + numberOfPlayers);
        for(Iterator iterator = players.iterator(); iterator.hasNext(); printwriter.println(((Player)iterator.next()).getName()));
        printwriter.println("END-OF-PLAYERS");
        printwriter.println("ROUNDS:" + getNumberOfRounds());
        for(int i = 0; i < getNumberOfRounds(); i++)
            getRound(i).save(printwriter);

        printwriter.println("END-OF-DIVISION");
    }

    //mutual matches table on a template-based tournament html-page
    public void saveAll(PrintWriter printwriter)
    {
        String output = Constants.getTemplate().toString();
        SeriesTable seriestable = getSeriesTable();               
        
        output = output.replaceAll("<!-- SERIESTABLE -->", seriestable.toString());
        
        StringBuilder mutualtable = new StringBuilder();        
        for(int i = 0; i < seriestable.size(); i++)
        {
            String s = seriestable.elementAt(i).getName();
            mutualtable.append("\t<td align=center>" + Tools.makeInitials(s) + "</td>");
        }
        mutualtable.append("</tr>");
        for(int j = 0; j < seriestable.size(); j++)
        {
            SeriesTableEntry seriestableentry = seriestable.elementAt(j);
            String s1 = seriestableentry.getName();
            mutualtable.append("<tr><td>" + s1 + "</td>");
            for(int k = 0; k < seriestable.size(); k++)
                if(j == k)
                {
                    mutualtable.append("<td>&nbsp;</td>");
                } else
                {
                    SeriesTableEntry seriestableentry1 = seriestable.elementAt(k);
                    String s2 = seriestableentry1.getName();
                    //hack:
                    if(seriestableentry.getHasTiedPoints() >= 0 && seriestableentry.getHasTiedPoints() == seriestableentry1.getHasTiedPoints())
                    	mutualtable.append("\t<td align=center><b> " + mutual.getResult(s1, s2) + "</b></td>");
                    else
                    	mutualtable.append("\t<td align=center> " + mutual.getResult(s1, s2) + "</td>");
                }
            mutualtable.append("</tr>");
        }

        output = output.replaceAll("<!-- MUTUALTABLE -->", seriestable.toString());
        
        //not always!! only when a playoff exists?
        output = output.replaceAll("<!-- PLAOFF -->", "playfoo");
        
        output = output.replaceAll("<!-- STANDINGS -->", "standings");
        
        printwriter.print(output.toString());
    }
    
    //mutual matches table on a tournament v.1.0 html-page
    public void saveAll_legacy(PrintWriter printwriter)
    {
        //print overall results table first
        SeriesTable seriestable = getSeriesTable();
        printwriter.println("<table align=center bgcolor=\"#c0c0c0\">");
        printwriter.println("<tr>\n<td>\n<pre>\n");
        seriestable.print(printwriter);
        printwriter.println("</pre>\n</td>\n</tr>\n</table>\n<p>");
        //print mutual results next
        printwriter.println("<table width=\"100%\" border=1>");
        printwriter.println("<tr><th align=center>Keskin\344iset ottelut</th>");
        for(int i = 0; i < seriestable.size(); i++)
        {
            String s = seriestable.elementAt(i).getName();
            printwriter.println("\t<td align=center>" + Tools.makeInitials(s) + "</td>");
        }

        printwriter.println("</tr>");
        for(int j = 0; j < seriestable.size(); j++)
        {
            SeriesTableEntry seriestableentry = seriestable.elementAt(j);
            String s1 = seriestableentry.getName();
            printwriter.println("<tr><td>" + s1 + "</td>");
            for(int k = 0; k < seriestable.size(); k++)
                if(j == k)
                {
                    printwriter.println("<td>&nbsp;</td>");
                } else
                {
                    SeriesTableEntry seriestableentry1 = seriestable.elementAt(k);
                    String s2 = seriestableentry1.getName();
                    printwriter.println("\t<td align=center> " + mutual.getResult(s1, s2) + "</td>");
                }

            printwriter.println("</tr>");
        }

        printwriter.println("</table>");
    }

    //show html-results page in the program with internal browser, by aulaskar
    public String createMutualTable()
    {
    	StringBuilder printout = new StringBuilder();
        SeriesTable seriestable = getSeriesTable();
        printout.append("<html>");
        printout.append("<body>");
        printout.append("<table align=center bgcolor=\"#c0c0c0\">");
        printout.append("<tr>\n<td>\n<pre>\n");
        printout.append(seriestable.toString());
        printout.append("</pre>\n</td>\n</tr>\n</table>\n<p>");
        printout.append("<font size=\"2\"><table width=\"100%\" border=1>");
        printout.append("<tr><th align=center>Keskin\344iset ottelut</th>");
        for(int i = 0; i < seriestable.size(); i++)
        {
            String s = seriestable.elementAt(i).getName();
            printout.append("\t<td align=center>" + Tools.makeInitials(s) + "</td>");
        }

        printout.append("</tr>");
        for(int j = 0; j < seriestable.size(); j++)
        {
            SeriesTableEntry seriestableentry = seriestable.elementAt(j);
            String s1 = seriestableentry.getName();
            printout.append("<tr><td>" + s1 + "</td>");
            for(int k = 0; k < seriestable.size(); k++)
                if(j == k)
                {
                    printout.append("<td>&nbsp;</td>");
                } else
                {
                    SeriesTableEntry seriestableentry1 = seriestable.elementAt(k);
                    String s2 = seriestableentry1.getName();
                    //hack:
                    if(seriestableentry.getHasTiedPoints() >= 0 && seriestableentry.getHasTiedPoints() == seriestableentry1.getHasTiedPoints())
                    	printout.append("\t<td align=center><b> " + mutual.getResult(s1, s2) + "</b></td>");
                    else
                    	printout.append("\t<td align=center> " + mutual.getResult(s1, s2) + "</td>");
                }

            printout.append("</tr>");
        }

        printout.append("</table></font>");
        printout.append("</body>");
        printout.append("</html>");
        
        return printout.toString();
    }

    public void saveTable(PrintWriter printwriter)
    {
        HtmlTools.h1(printwriter, getTitle());
        getSeriesTable().htmlSave(printwriter);
    }

    public void saveMatches(PrintWriter printwriter)
    {
        HtmlTools.tableIntro(printwriter, true, "100%");
        for(int i = 0; i < getNumberOfRounds() - 1; i += 2)
        {
            printwriter.println("<TR><TD>");
            getRound(i).saveMatches(printwriter);
            printwriter.println("</TD>");
            printwriter.println("<TD ALIGN=RIGHT>");
            getRound(i + 1).saveMatches(printwriter);
            printwriter.println("</TD></TR>");
        }

        printwriter.println("<TR><TD>");
        getRound(getNumberOfRounds() - 1).saveMatches(printwriter);
        printwriter.println("</TD></TR>");
        HtmlTools.tableOutro(printwriter);
    }

    //used by tournament to get overall standings
    public ArrayList getStandings() {
    	ArrayList standings = new ArrayList();
    	SeriesTable series = getSeriesTable();
    	
    	for(int i = 0; i < series.size(); i++) {
    		standings.add(series.elementAt(i));
    	}
    	
    	return standings;
    }
    
    public void finalize() {
        this.seats = null;
        this.rounds = null;
        this.seriesTableEntries = null;
        this.players = null;
        this.mutual = null;
    }
}
