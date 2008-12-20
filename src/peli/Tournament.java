package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Tournament.java
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;

/**
 * A main class that includes final variables set at the start of the program,
 * save routines and rules for division sizes
 * v.1.1 added division sizes as configurable properties
 * fix tournament date here! fixed now with v. 1.9
 * so is clearer division names in the GUI
 * and html-template support
 * @author aulaskar
 *
 */
public class Tournament {

    private Locale locale;
    private ResourceBundle messages;
    private ResourceBundle rules; //hack
    private Vector divisions;
    private int numberOfDivisions;
    private static final String legacydate = "x.x.2000";
    private static final String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date().getTime());
    private HashMap playoffs = new HashMap();
    private Playoff placementMatches;
    private int largestPlayoff = 0;
    // private ArrayList playoffSurvivors = new ArrayList();
    public HashMap getPlayoffs() {
        return this.playoffs;
    }

    public Playoff getPlacementMatches(int size) {
        if (this.placementMatches == null) { //new
            ArrayList placementPlayers = this.getStandingsNames();
            //int firstLoser = size;
            //ArrayList placementPlayers = new ArrayList();
            //for(int i = firstLoser; i < this.getStandings().size(); i++) {
            //    placementPlayers.add(orderedPlayers.get(i));
            //}
            //this.placementMatches = new Playoff(placementPlayers, this.getStandings().size() - playoffSize);
            this.placementMatches = new Playoff(placementPlayers, size);
        }

        return this.placementMatches;
    }

    /**
     * Reseed each new playoff round
     * @param size
     * @return
     */
    public Playoff getPlayoffWithReseed(int size) {
        Playoff playoff = null;
        if (!this.playoffs.containsKey(size)) {

            if (this.playoffs.containsKey(size * 2)) {

                ArrayList survivors = ((Playoff) this.playoffs.get(size * 2)).getSurvivors();
                ArrayList groupStandings = this.getStandingsNames();
                ArrayList survivorIndexes = new ArrayList();
                for (Object survivor : survivors) {
                    survivorIndexes.add(groupStandings.indexOf(survivor));
                }
                Collections.sort(survivorIndexes);
                ArrayList newSurvivors = new ArrayList();
                for (int i = 0; i < survivorIndexes.size(); i++) {
                    newSurvivors.add(groupStandings.get(i));
                }

                this.playoffs.put(size, new Playoff(seedPlayoff(newSurvivors, size), size));

            } else {
                this.playoffs.put(size, new Playoff(seedPlayoff(getStandingsNames(), size), size));
            }

        }
        playoff = (Playoff) this.playoffs.get(size);

        //don't advance to next round with empty results:
        if (playoff.isEmptyPlayoffs()) {
            this.playoffs.remove(size);
            playoff = null;
        } else {
            if (size > this.getLargestPlayoff()) {
                this.largestPlayoff = size;
            }
        }

        return playoff;
    }

    /**
     * Do not reseed after first playoff round(need to get multiple final groups unordered) 
     * @param size
     * @return
     */
    public Playoff getPlayoffNoReseed(int size) {
        Playoff playoff = null;
        if (!this.playoffs.containsKey(size)) {

            if (this.playoffs.containsKey(size * 2)) {
                //this.playoffs.put(size, new Playoff(seedPlayoff(((Playoff) this.playoffs.get(size * 2)).getSurvivors(), size), size));
                this.playoffs.put(size, new Playoff(((Playoff) this.playoffs.get(size * 2)).getSurvivors(), size));

            } else {
                this.playoffs.put(size, new Playoff(seedPlayoff(getStandingsNames(), size), size));
            }

        }
        playoff = (Playoff) this.playoffs.get(size);

        //don't advance to next round with empty results:
        if (playoff.isEmptyPlayoffs()) {
            this.playoffs.remove(size);
            playoff = null;
        } else {
            if (size > this.getLargestPlayoff()) {
                this.largestPlayoff = size;
            }
        }

        return playoff;
    }

    public void clearPlayoffs() {
        this.playoffs.clear();
    }

    /**
     * Need to return an ordered list of playoff pairs
     * 
     * @param playerStandings
     * @return
     */
    public ArrayList seedPlayoff(ArrayList playerStandings, int size) {
        ArrayList newPairs = new ArrayList(size);
        for (int i = 0; i < size - 1; i++) {
            newPairs.add(playerStandings.get(0 + i));
            newPairs.add(playerStandings.get(size - (i + 1)));
        }

        return newPairs;
    }
    //private static final String displayName = System.getProperty("TournamentFileName") + " / ";
    private void distributePlayers(TreeSet atreeset[], TreeSet treeset) {
        for (int i = 0; i < atreeset.length; i++) {
            atreeset[i] = new TreeSet(new PlayerComparator());
        }
        int j = 0;
        boolean flag = false;
        for (Iterator iterator = treeset.iterator(); iterator.hasNext();) {
            atreeset[j].add((Player) iterator.next());
            if (!flag) {
                if (++j == atreeset.length) {
                    j--;
                    flag = true;
                }
            } else if (--j < 0) {
                j++;
                flag = false;
            }
        }

    }

    //modified
    private int calculateNumberOfDivisions(int i) {
        try {
            //added
            if (System.getProperty("TournamentHardcodedNumOfDivisions") != null) {
                int divs = Integer.parseInt(System.getProperty("TournamentHardcodedNumOfDivisions"));
                if (divs > 0) //zero means no hardcoded number 
                {
                    return divs;
                }
            }

            //hacked
            if (i < Integer.parseInt(System.getProperty("TournamentMin2Divisions"))) {
                return 1;
            }
            if (i < Integer.parseInt(System.getProperty("TournamentMin3Divisions"))) {
                return 2;
            }
            if (i < Integer.parseInt(System.getProperty("TournamentMin4Divisions"))) {
                return 3;
            }
            if (i < Integer.parseInt(System.getProperty("TournamentMin5Divisions"))) {
                return 4;
            }
            if (i < Integer.parseInt(System.getProperty("TournamentMin6Divisions"))) {
                return 5;
            }
            if (i < Integer.parseInt(System.getProperty("TournamentMin7Divisions"))) {
                return 6;
            }
            return i >= Integer.parseInt(System.getProperty("TournamentMin8Divisions")) ? 8 : 7;
        } catch (Exception e) {
            System.err.println("Reading rules failed, trying with defaults");
            e.printStackTrace();
            if (i < 26) {
                return 1;
            }
            if (i < 45) {
                return 2;
            }
            if (i < 60) {
                return 3;
            }
            if (i < 75) {
                return 4;
            }
            if (i < 90) {
                return 5;
            }
            if (i < 105) {
                return 6;
            }
            return i >= 120 ? 8 : 7;

        }
    }

    private void loadRules(ResourceBundle rules1) {


        try {
            System.setProperty("TournamentHardcodedNumOfDivisions", rules1.getString("hardcodedNumOfDivisions"));
            System.setProperty("TournamentMin2Divisions", rules1.getString("min2Divisions"));
            System.setProperty("TournamentMin3Divisions", rules1.getString("min3Divisions"));
            System.setProperty("TournamentMin4Divisions", rules1.getString("min4Divisions"));
            System.setProperty("TournamentMin5Divisions", rules1.getString("min5Divisions"));
            System.setProperty("TournamentMin6Divisions", rules1.getString("min6Divisions"));
            System.setProperty("TournamentMin7Divisions", rules1.getString("min7Divisions"));
            System.setProperty("TournamentMin8Divisions", rules1.getString("min8Divisions"));

            System.setProperty("TournamentPointsPerWin", rules1.getString("pointsPerWin"));
            System.setProperty("TournamentPointsPerTie", rules1.getString("pointsPerTie"));
            System.setProperty("TournamentOrderByMutualMatch", rules1.getString("orderByMutualMatch"));
            System.setProperty("TournamentShowMutualTableTab", rules1.getString("showMutualTableTab"));
            System.setProperty("TournamentUseVersion1HtmlOutput", rules1.getString("useVersion1HtmlOutput"));
            System.setProperty("TournamentShowPlayoffTab", rules1.getString("showPlayoffTab"));
        } catch (SecurityException se) {
            System.err.println("Setting system properties not supported!");
        } catch (MissingResourceException me) {
        } catch (Exception e) {
            System.err.println("You need a valid Rules.properties-file. Trying to continue with default rules.");
            e.printStackTrace();

            System.setProperty("TournamentMin2Divisions", "26");
            System.setProperty("TournamentMin3Divisions", "45");
            System.setProperty("TournamentMin4Divisions", "60");
            System.setProperty("TournamentMin5Divisions", "75");
            System.setProperty("TournamentMin6Divisions", "90");
            System.setProperty("TournamentMin7Divisions", "105");
            System.setProperty("TournamentMin8Divisions", "120");

            System.setProperty("TournamentPointsPerWin", "2");
            System.setProperty("TournamentPointsPerTie", "1");
            System.setProperty("TournamentOrderByMutualMatch", "true");
        }
    }

    Tournament(int i, TreeSet treeset) {
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);*/
        locale = Constants.getInstance().getLocale();
        messages = Constants.getInstance().getMessages();
        rules = Constants.getInstance().getRules();
        this.loadRules(rules);
        divisions = new Vector();
        numberOfDivisions = 1;
        numberOfDivisions = calculateNumberOfDivisions(treeset.size());
        TreeSet atreeset[] = new TreeSet[numberOfDivisions];
        distributePlayers(atreeset, treeset);
        for (int j = 0; j < numberOfDivisions; j++) {
            divisions.add(new Division(messages.getString("group") + " " + (j + 1), i, atreeset[j]));
        }

    }

    Tournament(File file)
            throws IOException, FileFormatException {
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);*/
        locale = Constants.getInstance().getLocale();
        messages = Constants.getInstance().getMessages();
        rules = Constants.getInstance().getRules();
        //rules = ResourceBundle.getBundle("Rules");added for changeable division sizes
        this.loadRules(rules);
        divisions = new Vector();
        numberOfDivisions = 1;
        try {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(file.getName()));
            numberOfDivisions = Tools.parseIntAfter("TOURNAMENT-SIZE:", bufferedreader.readLine());
            for (int i = 0; i < numberOfDivisions; i++) {
                divisions.add(new Division(bufferedreader));
            }

            if (bufferedreader.ready()) { //there's a playoff too
                int playoffsSize = Tools.parseIntAfter("PLAYOFFS-SIZE:", bufferedreader.readLine());
                for (int i = 0; i < playoffsSize; i++) {
                    Playoff playoff = new Playoff(bufferedreader);
                    playoffs.put(playoff.getSize(), playoff);
                }
            }

            if (bufferedreader.ready()) { //there's placementmatches too
                int playoffsSize = Tools.parseIntAfter("PLACEMENTMATCHES:", bufferedreader.readLine());
                for (int i = 0; i < playoffsSize; i++) {
                    this.placementMatches = new Playoff(bufferedreader);
                }
                if (!bufferedreader.readLine().equals("END-OF-PLACEMENTMATCHES")) {
                    throw new FileFormatException();
                }            
            }

            bufferedreader.close();
        } catch (IOException ioexception) {
            throw ioexception;
        } catch (FileFormatException fileformatexception) {
            throw fileformatexception;
        }

    }

    public int size() {
        return numberOfDivisions;
    }

    public int getNumberOfDivisions() {
        return numberOfDivisions;
    }

    public int getNumberOfPlayoffs() {
        return this.playoffs.size();
    }

    public Division getDivision(int i) {
        return (Division) divisions.elementAt(i);
    }

    public String[] getDivisionTitles() {
        String displayName = System.getProperty("TournamentFileName") + " / ";
        switch (numberOfDivisions) {
            case 1: // '\001'
                return (new String[]{
                            displayName + "Agdur"
                        });

            case 2: // '\002'
                return (new String[]{
                            displayName + "Agdur", displayName + "Bulldozer"
                        });

            case 3: // '\003'
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C"
                        });

            case 4: // '\004'
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D"
                        });

            case 5: // '\005'
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E"
                        });

            case 6: // '\006'
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E" + displayName + "F"
                        });

            case 7: // '\007'
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E" + displayName + "F" + displayName + "G"
                        });

            case 8: // '\b'
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E" + displayName + "F" + displayName + "G" + displayName + "H"
                        });
        }
        return (new String[]{
                    displayName + " A"
                });
    }

    public void save(PrintWriter printwriter, int i) {
        switch (i) {
            case 1: // '\001'
                saveMatches(printwriter);
                break;

            case 0: // '\0'
                saveTables(printwriter);
                break;

            case 2: // '\002'
                save(printwriter);
                break;

            case 3: // '\003'
                saveAll(printwriter);
                break;

            case 4:
                saveStandings(printwriter);
                break;

            default:
                save(printwriter);
                break;
        }
    }

    //tnmt-file
    public void save(PrintWriter printwriter) {
        printwriter.println("TOURNAMENT-SIZE:" + getNumberOfDivisions());
        for (int i = 0; i < getNumberOfDivisions(); i++) {
            getDivision(i).save(printwriter);
        }
        //if playoff, save playoff
        Set rounds = playoffs.keySet();
        printwriter.println("PLAYOFFS-SIZE:" + getNumberOfPlayoffs());
        for (Iterator i = rounds.iterator(); i.hasNext();) {
            ((Playoff) playoffs.get(i.next())).save(printwriter);
        }
        printwriter.println("PLACEMENTMATCHES:" + 1);
        this.placementMatches.save(printwriter);
        printwriter.println("END-OF-PLACEMENTMATCHES");
    }

    //added by aulaskar to help organising final groups
    /** print combined standings of all divisions */
    public ArrayList getStandings() {
        ArrayList divisions = new ArrayList();
        ArrayList overallstandings = new ArrayList();

        for (int i = 0; i < getNumberOfDivisions(); i++) {
            divisions.add(getDivision(i).getStandings());        //trust that the first division allways exists
        }
        int maxdivisionsize = ((ArrayList) divisions.get(0)).size() + 2;

        //order combined standings of all divisions
        for (int i = 0; i < maxdivisionsize; i++) {
            TreeSet treeset = new TreeSet(new SeriesTableEntryComparator());

            for (int j = 0; j < getNumberOfDivisions(); j++) {
                try {
                    treeset.add(((ArrayList) divisions.get(j)).get(i));
                } catch (IndexOutOfBoundsException ie) {
                    //nothing, divisions may have different sizes
                }
            }

            overallstandings.addAll(treeset);
            treeset.clear();
        }

        return overallstandings;
    }

    public String getFormattedStandings() {
        StringBuilder sb = new StringBuilder();
        int placement = 1;
        ArrayList overallstandings = getStandings();
        for (Iterator iterator = overallstandings.iterator(); iterator.hasNext();) {
            sb.append(placement++).append(".").append(((SeriesTableEntry) iterator.next()).getName()).append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

    public ArrayList getStandingsNames() {
        ArrayList justnames = new ArrayList();
        ArrayList overallstandings = getStandings();
        for (Iterator iterator = overallstandings.iterator(); iterator.hasNext();) {
            justnames.add(((SeriesTableEntry) iterator.next()).getName());
        }

        return justnames;
    }

    //added by aulaskar to help organising final groups
    /** print combined standings of all divisions */
    public void saveStandings(PrintWriter printwriter) {
        ArrayList overallstandings = getStandings();

        //print to file
        for (Iterator iterator = overallstandings.iterator(); iterator.hasNext();) {
            printwriter.println(((SeriesTableEntry) iterator.next()).getName());
        }

    }

    public void saveTables(PrintWriter printwriter) {
        HtmlTools.intro(printwriter, messages.getString("seriesTable"));
        HtmlTools.hr(printwriter);
        HtmlTools.br(printwriter);
        for (int i = 0; i < divisions.size(); i++) {
            getDivision(i).saveTable(printwriter);
        }
        HtmlTools.br(printwriter);
        HtmlTools.hr(printwriter);
        HtmlTools.br(printwriter);
        HtmlTools.outro(printwriter);
    }

    public void saveMatches(PrintWriter printwriter) {
        HtmlTools.intro(printwriter, messages.getString("matchProgramme"));
        HtmlTools.hr(printwriter);
        for (int i = 0; i < divisions.size(); i++) {
            getDivision(i).saveMatches(printwriter);
        }
        HtmlTools.br(printwriter);
        HtmlTools.hr(printwriter);
        HtmlTools.br(printwriter);
        HtmlTools.outro(printwriter);
    }

    public void saveAll(PrintWriter printwriter) {
        //won't work
        //if(rules.getString("useVersion1HtmlOutput").equalsIgnoreCase("false")) {        
        if (System.getProperty("TournamentUseVersion1HtmlOutput") == null) {

            //use header.txt            
            String header = Constants.getHeader().toString();
            header = header.replaceAll("<!-- TITLE -->", System.getProperty("TournamentFileName"));
            header = header.replaceAll("<!-- DATE -->", date);
            header = header.replaceAll("<!-- HEADING -->", messages.getString("templateHeading"));
            printwriter.print(header);

            //use template.txt                                                
            for (int i = 0; i < getNumberOfDivisions(); i++) {
                getDivision(i).saveAll(printwriter);
            //HtmlTools.hr(printwriter);
            }

            //use footer.txt
            printwriter.print(Constants.getFooter().toString());

        } else {  //force version 1.0 compatible html output format
            saveAll_legacy(printwriter);
        }

    }

    /** Tournament v. 1.0 html-output */
    public void saveAll_legacy(PrintWriter printwriter) {
        HtmlTools.intro(printwriter, messages.getString("seriesTableAndMutualMatches"));
        HtmlTools.insertDate(printwriter, legacydate); //fixed
        HtmlTools.hr(printwriter);
        for (int i = 0; i < getNumberOfDivisions(); i++) {
            getDivision(i).saveAll_legacy(printwriter);
            HtmlTools.hr(printwriter);
        }

        HtmlTools.outro(printwriter);
    }

    public void finalize() {
        this.divisions = null;
    }

    public int getLargestPlayoff() {
        return largestPlayoff;
    }
}
