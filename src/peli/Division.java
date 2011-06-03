package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Division.java
import java.io.*;
import java.util.*;

/** Creates, saves and prints a division
 * v1.1 internal html-results-tab
 * v1.9 html-templates, mutual result coloring
 * v.1.20 breakless seating for rotating wch2011 inherited points system
 * @author aulaskar
 *
 */
public class Division {

    private String title;
    private TreeSet<Player> players;
    private Mutual mutual;
    private int numberOfPlayers;
    private Hashtable<String, SeriesTableEntry> seriesTableEntries;
    private boolean hasDummyPlayer;
    private String seats[];
    private int times;
    private Round rounds[];
    private boolean playoff = false;

    //niko inisee, miksi pelaajien järjestys on tämä, kun suosikit ei kohtaa viimeisellä kierroksella
    private void makeInitialSeating() {
        int i = 0;
        for (Iterator iterator = players.iterator(); iterator.hasNext();) {
            seats[i++] = ((Player) iterator.next()).getName();
        }
        if (hasDummyPlayer) {
            seats[i] = seats[1];
            seats[1] = "X";
        }
    }

    private void makeNextSeating() {
        String s = seats[seats.length - 1];
        for (int i = seats.length - 1; i > 0; i--) {
            seats[i] = seats[i - 1];
        }
        seats[0] = s;
    }

    private void makeBreaklessInitialSeatingForEvenNumbers() {
        int i = 0;
        for (Iterator iterator = players.iterator(); iterator.hasNext();) {
            seats[i++] = ((Player) iterator.next()).getName();
        }
        if (hasDummyPlayer) {
            seats[i] = seats[0];
            seats[0] = "X";
        }
    }
   
    private void makeBreaklessNextSeatingForEvenNumbers() {
        String s = seats[seats.length - 1];
        for (int i = seats.length - 1; i > 0; i--) {
            seats[i] = seats[i - 1];
        }
        seats[1] = s; //X pysyy päädyssä tauolla positiossa 0
    }

    private void printSeating() {
        for (int i = 0; i < seats.length; i++) {
            System.out.println("Paikka " + i + ": " + seats[i]);
        }
    }

    /**
     * Extra option to support a pauseless final group with results
     * from a previous group
     */
    private void buildAllRounds() {
        String pfGen = System.getProperty("PFGenerate");
        if (pfGen != null && pfGen.equalsIgnoreCase("true")) {
            makeBreaklessInitialSeatingForEvenNumbers();
            for (int i = 0; i < rounds.length; i++) {
                rounds[i] = new Round(this, i + 1, seats);
                makeBreaklessNextSeatingForEvenNumbers();
            }
        } else {            
            makeInitialSeating();
            for (int i = 0; i < rounds.length; i++) {
                rounds[i] = new Round(this, i + 1, seats);
                makeNextSeating();
            }
        }
    }

    private void printAllRounds() {
        for (int i = 0; i < seats.length; i++) {
            rounds[i].print();
        }
    }

    //CHECK
    Division(String title, int times, TreeSet<Player> treeset) {
        seriesTableEntries = new Hashtable();
        hasDummyPlayer = false;
        this.title = title;
        this.times = times;
        players = treeset;
        mutual = new Mutual(treeset);
        numberOfPlayers = treeset.size();
        hasDummyPlayer = treeset.size() % 2 == 0;
        buildSeriesTableEntries(players);
        int treesetSize = hasDummyPlayer ? treeset.size() + 1 : treeset.size();
        rounds = new Round[treesetSize * times];
        seats = new String[treesetSize];
        buildAllRounds();
    }

    //CHECK
    Division(BufferedReader bufferedreader)
            throws FileFormatException, IOException {
        seriesTableEntries = new Hashtable();
        hasDummyPlayer = false;
        try {
            title = Tools.parseStringAfter("DIVISION:", bufferedreader.readLine());
            int numOfPlayers = Tools.parseIntAfter("PLAYERS:", bufferedreader.readLine());
            numberOfPlayers = numOfPlayers;
            players = new TreeSet(new PlayerComparator());
            for (int j = 0; j < numOfPlayers; j++) {
                players.add(new Player(j + 1, bufferedreader.readLine()));
            }
            if (!bufferedreader.readLine().equals("END-OF-PLAYERS")) {
                throw new FileFormatException();
            }
            hasDummyPlayer = numOfPlayers % 2 == 0;
            mutual = new Mutual(players);
            buildSeriesTableEntries(players);
            int numOfRounds = Tools.parseIntAfter("ROUNDS:", bufferedreader.readLine());
            rounds = new Round[numOfRounds];
            for (int l = 0; l < numOfRounds; l++) {
                rounds[l] = new Round(this, numOfPlayers / 2, bufferedreader);
            }
            if (!bufferedreader.readLine().equals("END-OF-DIVISION")) {
                throw new FileFormatException();
            }
        } catch (FileFormatException fileformatexception) {
            throw fileformatexception;
        } catch (IOException ioexception) {
            throw ioexception;
        }
    }

    private void buildSeriesTableEntries(TreeSet<Player> treeset) {
        int rank;
        String name;
        Iterator iterator = treeset.iterator();
        while (iterator.hasNext()) {
            Player nextPlayer = ((Player) iterator.next());
            name = nextPlayer.getName();
            rank = nextPlayer.getRank();
            seriesTableEntries.put(name, new SeriesTableEntry(rank, name));
        }
    }

    public Mutual getMutual() {
        return mutual;
    }

    public String getTitle() {
        return title;
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public int getNumberOfRounds() {
        return rounds.length;
    }

    public Round getRound(int i) {
        return rounds[i];
    }

    public SeriesTableEntry getSeriesTableEntry(String name) {
        return (SeriesTableEntry) seriesTableEntries.get(name);
    }

    public SeriesTable getSeriesTable() {
        return new SeriesTable(seriesTableEntries, mutual);
    }

    //CHECK
    //tnmt-file division writing
    public void save(PrintWriter printwriter) {
        printwriter.println("DIVISION:" + getTitle());
        printwriter.println("PLAYERS:" + numberOfPlayers);
        for (Iterator iterator = players.iterator(); iterator.hasNext(); printwriter.println(((Player) iterator.next()).getName()));
        printwriter.println("END-OF-PLAYERS");
        printwriter.println("ROUNDS:" + getNumberOfRounds());
        for (int i = 0; i < getNumberOfRounds(); i++) {
            getRound(i).save(printwriter);
        }
        printwriter.println("END-OF-DIVISION");
    }

    /**
     * used in multiple places to get the color
     * @param tmpResults
     * @return
     */
    private String getScoreFormat(String resultHome, String resultAway) {
        String mutualResultClass = "tie";

        if (resultHome == null || resultAway == null) {
            return mutualResultClass;
        }

        //must get rid of result prefixes for parseint
        int[] tmpScores = {Integer.parseInt(Tools.getNumber(resultHome)), Integer.parseInt(Tools.getNumber(resultAway))};
        if (tmpScores[0] > tmpScores[1]) {
            mutualResultClass = "win";
        } else if (tmpScores[0] < tmpScores[1]) {
            mutualResultClass = "loss";
        } else {
            mutualResultClass = "tie";
        }

        return mutualResultClass;
    }

    /**
     * Make colored table for n results between two players
     * @param tmpResult
     * @return
     */
    private String formatMultiResult(String tmpResult) {
        //handle n-times-series
        StringBuilder formattedResult = new StringBuilder("<table>");
        String mutualResultClass = null;
        String[] tmpRows = tmpResult.split("<BR>");
        for (int i = 0; i < tmpRows.length; i++) {
            String[] tmpResults = tmpRows[i].split("-");
            mutualResultClass = getScoreFormat(tmpResults[0], tmpResults[1]);
            formattedResult.append("<tr><td class=\"").append(mutualResultClass).append("\">").append(tmpRows[i]).append("</td></tr>");
        }
        formattedResult.append("</table>");

        return formattedResult.toString();
    }

    /**
     * @deprecated, was voted illlogical
     * @param tmpResult
     * @return
     *//*
    private String getCombinedMultiResult(String tmpResult) {
    //handle n-times-series
    String mutualResultClass = null;
    String[] tmpRows = tmpResult.split("<BR>");
    int scorediff = 0;
    int goaldiff = 0;
    for (int i = 0; i < tmpRows.length; i++) {
    String[] tmpResults = tmpRows[i].split("-");
    int[] tmpScores = {Integer.parseInt(tmpResults[0]), Integer.parseInt(tmpResults[1])};
    if (tmpScores[0] > tmpScores[1]) {
    scorediff++;
    } else if (tmpScores[0] < tmpScores[1]) {
    scorediff--;
    }
    goaldiff += (tmpScores[0] - tmpScores[1]);
    }
    if (scorediff > 0) {
    mutualResultClass = "win";
    } else if (scorediff < 0) {
    mutualResultClass = "loss";
    } else {
    if (goaldiff > 0) {
    mutualResultClass = "win";
    } else if (goaldiff < 0) {
    mutualResultClass = "loss";
    } else {
    mutualResultClass = "tie";
    }
    }

    return mutualResultClass;
    }*/

    //mutual matches table on a template-based tournament html-page
    public String saveAll(List<Integer> playoffSeparators, String groupName) {
        String output = Constants.getTemplate().toString();
        SeriesTable seriestable = getSeriesTable();
        if (seriestable.size() < 1) {
            return ""; //@TODO strange extra division
        }

        output = output.replaceAll("<!-- SERIESTABLE -->", seriestable.toCssHtmlTableWithAnchors(playoffSeparators, groupName));

        StringBuilder mutualtable = new StringBuilder();
        for (int i = 0; i < seriestable.size(); i++) {
            String s = seriestable.elementAt(i).getName();
            mutualtable.append("\t<td align=\"center\" class=\"mutualinitials\">" + Tools.makeInitials(s) + "</td>" + System.getProperty("line.separator"));
        }
        mutualtable.append("</tr>" + System.getProperty("line.separator"));
        String tmpResult = null;

        for (int j = 0; j < seriestable.size(); j++) {
            SeriesTableEntry seriestableentry = seriestable.elementAt(j);
            String s1 = seriestableentry.getName();
            mutualtable.append("<tr><td class=\"mutualname\">" + s1 + "</td>" + System.getProperty("line.separator"));
            for (int k = 0; k < seriestable.size(); k++) {
                String mutualResultClass = "";
                if (j == k) {
                    mutualtable.append("<td class=\"mutualempty\">&nbsp;</td>" + System.getProperty("line.separator"));
                } else {
                    SeriesTableEntry seriestableentry1 = seriestable.elementAt(k);
                    String s2 = seriestableentry1.getName();
                    //Choose style for result appearance:
                    //creates too many objects:
                    tmpResult = mutual.getResult(s1, s2);
                    if (tmpResult.matches(".*<BR>.*")) {
                        tmpResult = formatMultiResult(tmpResult);
                    } else { //simple comparison, not needed?
                        String[] tmpResults = tmpResult.split("-");
                        if (tmpResults != null && tmpResults.length >= 2) {
                            mutualResultClass = getScoreFormat(tmpResults[0], tmpResults[1]);
                        }
                    }
                    //put out formatted mutual html-results:
                    if (seriestableentry.getHasTiedPoints() >= 0 && seriestableentry.getHasTiedPoints() == seriestableentry1.getHasTiedPoints()) {
                        mutualtable.append("\t<td align=\"center\" class=\"" + mutualResultClass + "\"><b class=\"mutualcomparisonresult\"> " + tmpResult + "</b></td>" + System.getProperty("line.separator"));
                    } else {
                        mutualtable.append("\t<td align=\"center\" class=\"" + mutualResultClass + "\"> " + tmpResult + "</td>" + System.getProperty("line.separator"));
                    }
                }
            }
            mutualtable.append("</tr>" + System.getProperty("line.separator"));
        }

        output = output.replaceAll("<!-- MUTUALTABLE -->", mutualtable.toString());

        //printwriter.print(output.toString());
        return output.toString();
    }
    //mutual matches table on a tournament v.1.0 html-page

    public void saveAll_legacy(PrintWriter printwriter) {
        //print overall results table first
        SeriesTable seriestable = getSeriesTable();
        printwriter.println("<table align=center bgcolor=\"#c0c0c0\">");
        printwriter.println("<tr>\n<td>\n<pre>\n");
        seriestable.print(printwriter);
        printwriter.println("</pre>\n</td>\n</tr>\n</table>\n<p>");
        //print mutual results next
        printwriter.println("<table width=\"100%\" border=1>");
        printwriter.println("<tr><th align=center>Keskin\344iset ottelut</th>");
        for (int i = 0; i < seriestable.size(); i++) {
            String s = seriestable.elementAt(i).getName();
            printwriter.println("\t<td align=center>" + Tools.makeInitials(s) + "</td>");
        }

        printwriter.println("</tr>");
        for (int j = 0; j < seriestable.size(); j++) {
            SeriesTableEntry seriestableentry = seriestable.elementAt(j);
            String s1 = seriestableentry.getName();
            printwriter.println("<tr><td>" + s1 + "</td>");
            for (int k = 0; k < seriestable.size(); k++) {
                if (j == k) {
                    printwriter.println("<td>&nbsp;</td>");
                } else {
                    SeriesTableEntry seriestableentry1 = seriestable.elementAt(k);
                    String s2 = seriestableentry1.getName();
                    printwriter.println("\t<td align=center> " + mutual.getResult(s1, s2) + "</td>");
                }
            }
            printwriter.println("</tr>");
        }

        printwriter.println("</table>");
    }

    //show html-results page in the program with internal browser, by aulaskar
    public String createMutualTable() {
        StringBuilder printout = new StringBuilder();
        SeriesTable seriestable = getSeriesTable();
        printout.append("<html>");
        printout.append("<body>");
        printout.append("<center>");
        printout.append("<table align=\"center\" bgcolor=\"#c0c0c0\">");
        printout.append("<tr>\n<td>\n<pre>\n");
        printout.append(seriestable.toString());
        printout.append("</pre>\n</td>\n</tr>\n</table>\n<p>");
        printout.append("<font size=\"2\"><table width=\"100%\" border=\"1\">");
        printout.append("<tr><th align=\"center\">Keskin\344iset ottelut</th>");
        for (int i = 0; i < seriestable.size(); i++) {
            String s = seriestable.elementAt(i).getName();
            printout.append("\t<td align=\"center\">" + Tools.makeInitials(s) + "</td>");
        }

        printout.append("</tr>");
        for (int j = 0; j < seriestable.size(); j++) {
            SeriesTableEntry seriestableentry = seriestable.elementAt(j);
            String s1 = seriestableentry.getName();
            printout.append("<tr><td>" + s1 + "</td>");
            for (int k = 0; k < seriestable.size(); k++) {
                if (j == k) {
                    printout.append("<td>&nbsp;</td>");
                } else {
                    SeriesTableEntry seriestableentry1 = seriestable.elementAt(k);
                    String s2 = seriestableentry1.getName();
                    //hack:
                    if (seriestableentry.getHasTiedPoints() >= 0 && seriestableentry.getHasTiedPoints() == seriestableentry1.getHasTiedPoints()) {
                        printout.append("\t<td align=\"center\"><b> " + mutual.getResult(s1, s2) + "</b></td>");
                    } else {
                        printout.append("\t<td align=\"center\"> " + mutual.getResult(s1, s2) + "</td>");
                    }
                }
            }
            printout.append("</tr>");
        }

        printout.append("</table></font>");
        printout.append("</center>");
        printout.append("</body>");
        printout.append("</html>");

        return printout.toString();
    }

    public void saveTable(PrintWriter printwriter) {
        HtmlTools.h1(printwriter, getTitle());
        getSeriesTable().htmlSave(printwriter);
    }

    public void saveMatches(PrintWriter printwriter) {
        HtmlTools.tableIntro(printwriter, true, "100%");
        for (int i = 0; i < getNumberOfRounds() - 1; i += 2) {
            printwriter.println("<tr><td>");
            getRound(i).saveMatches(printwriter);
            printwriter.println("</td>");
            printwriter.println("<td align=\"right\">");
            getRound(i + 1).saveMatches(printwriter);
            printwriter.println("</td></tr>");
        }

        printwriter.println("<tr><td>");
        getRound(getNumberOfRounds() - 1).saveMatches(printwriter);
        printwriter.println("</td></tr>");
        HtmlTools.tableOutro(printwriter);
    }

    public void saveMatches_legacy(PrintWriter printwriter) {
        HtmlTools_legacy.tableIntro(printwriter, true, "100%");
        for (int i = 0; i < getNumberOfRounds() - 1; i += 2) {
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

    public ArrayList<SeriesTableEntry> getStandings() {
        ArrayList<SeriesTableEntry> standings = new ArrayList<SeriesTableEntry>();
        SeriesTable series = getSeriesTable();

        for (int i = 0; i < series.size(); i++) {
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
