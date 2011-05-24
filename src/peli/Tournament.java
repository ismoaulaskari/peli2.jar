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
 * v.1.11 playoff and placementmatch into html
 * v.1.11 combine html and tnmt-save
 * v.1.11 super-tnmt
 * v.1.13 playoff schema/seeding options
 * v.1.14 special playoff sizes
 * v.1.16 Support disqualified players of two types, partly (wo) and completely (dq) disqualified,
 * v.1.18 and inherited from previous group (pg)
 * @TODO support for extending a series(make default series bigger but hidden)
 * @TODO match schedule printout for players?
 * @TODO xml-output
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
    private static final SimpleDateFormat PELIJARDATE = new SimpleDateFormat("dd.MM.yyyy");
    private static String date = PELIJARDATE.format(new Date().getTime());
    private HashMap playoffs = new HashMap<Integer, Playoff>();
    private Playoff placementMatches = null;
    private Playoff bronzeMatch = null;
    private volatile int largestPlayoff = 0;
    private String seedingModel = "CREATEDYNAMIC";
    // private ArrayList playoffSurvivors = new ArrayList();

    public HashMap getPlayoffs() {
        return this.playoffs;
    }

    public List getPlayoffsSortedKeySet() {
        List rounds = new LinkedList(playoffs.keySet());
        Collections.sort(rounds);

        return rounds;
    }

    public boolean isPlacementMatches() {
        return (this.placementMatches != null);
    }

    public boolean isBronzeMatch() {
        return (this.bronzeMatch != null);
    }

    /**
     * Size should probably be the size of the tournament
     * The GUI must choose to show only the appropriate pairs of the playoff
     * @param size
     * @return
     */
    public Playoff getPlacementMatches(int size) {
        ArrayList groupStandings = this.getStandingsNames(getStandings());
        if (this.placementMatches == null) { //new
            ArrayList placementPlayers = this.getStandingsNames(getStandings());
            //placementPlayers = new ArrayList(placementPlayers.subList(playoffSize - 1, placementPlayers.size() - playoffSize));
            //int firstLoser = size;
            //ArrayList placementPlayers = new ArrayList();
            //for(int i = firstLoser; i < this.getStandings().size(); i++) {
            //    placementPlayers.add(orderedPlayers.get(i));
            //}
            //this.placementMatches = new Playoff(placementPlayers, this.getStandings().size() - playoffSize);
            this.placementMatches = new Playoff(placementPlayers, size);
        }
        this.placementMatches.markRankings(groupStandings);

        return this.placementMatches;
    }

    /**
     * Try to get current bronzematch-pair
     * @return
     */
    public Playoff getBronzeMatch() {
        ArrayList groupStandings = addPlayoffsToStandings(this.getStandingsNames(getStandings()));
        if (this.bronzeMatch == null && this.playoffs.containsKey(4) && this.playoffs.containsKey(2)) {
            ArrayList losers = ((Playoff) this.playoffs.get(4)).getLosers();
            if (losers.size() == 2) {
                this.bronzeMatch = new Playoff(losers, 2);
            }
        }

        if (this.bronzeMatch != null) {
            this.bronzeMatch.markRankings(groupStandings);
        }

        return this.bronzeMatch;
    }

    public Playoff getPlayoff(String seedingModel, int size) {
        if (seedingModel.equals("CREATESTATIC")) {
            return getPlayoffNoReseed(size);
        } else if (seedingModel.equals("CREATERANDOM")) {
            return getPlayoffRandomSeed(size);
        } else {
            return getPlayoffWithReseed(size);
        }
    }

    /**
     * Reseed each new playoff round
     * @param size
     * @return
     */
    public Playoff getPlayoffWithReseed(int size) {
        Playoff playoff = null;
        ArrayList groupStandings = this.getStandingsNames(getStandingsForPlayoffs());
        int rememberSize = size;

        if (!this.playoffs.containsKey(size)) {

            if (this.playoffs.containsKey(size * 2)) { //there is a previous round?
                //who are left?
                ArrayList survivors = ((Playoff) this.playoffs.get(size * 2)).getSurvivors();
                ArrayList survivorIndexes = new ArrayList();
                for (Object survivor : survivors) {
                    survivorIndexes.add((int) groupStandings.indexOf(survivor));
                }
                //order survivors based on group standings
                Collections.sort(survivorIndexes);
                ArrayList newSurvivors = new ArrayList();
                for (int i = 0; i < survivorIndexes.size(); i++) {
                    try {
                        newSurvivors.add(groupStandings.get((Integer) survivorIndexes.get(i)));
                    } catch (IndexOutOfBoundsException ie) {
                        //   System.err.println("Problem with survivor " + survivorIndexes.get(i));
                        newSurvivors.add("X");
                    }
                }
                this.playoffs.put(size, new Playoff(seedPlayoff(newSurvivors, size), size));
            } else { //no previous round so create new
                ArrayList playoffStandings = getStandingsNames(getStandingsForPlayoffs());
                size = handleSpecialPlayoffsize(size, playoffStandings);
                this.playoffs.put(size, new Playoff(seedPlayoff(playoffStandings, size), size));
            }

        }
        playoff = (Playoff) this.playoffs.get(size);
        playoff.markRankings(groupStandings);
        //don't advance to next round with empty results:
        if (playoff.isEmptyPlayoffs()) {
            this.playoffs.remove(size);
            playoff = null;
        } else {
            if (rememberSize > this.getLargestPlayoff()) {
                this.largestPlayoff = rememberSize;
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
        ArrayList groupStandings = this.getStandingsNames(getStandingsForPlayoffs());
        int rememberSize = size;

        if (!this.playoffs.containsKey(size)) {

            if (this.playoffs.containsKey(size * 2)) { //there is a previous round?

                this.playoffs.put(size, new Playoff(seedStaticPlayoff(((Playoff) this.playoffs.get(size * 2)).getPlayoffPairs(), size), size));
                //this.playoffs.put(size, new Playoff(seedStaticPlayoff(((Playoff) this.playoffs.get(size * 2)).getSurvivors(), size), size));
                //this.playoffs.put(size, new Playoff(((Playoff) this.playoffs.get(size * 2)).getSurvivors(), size));

            } else {
                ArrayList playoffStandings = getStandingsNames(getStandingsForPlayoffs());
                size = handleSpecialPlayoffsize(size, playoffStandings);
                this.playoffs.put(size, new Playoff(seedPlayoff(playoffStandings, size), size));
                //this.playoffs.put(size, new Playoff(getStandingsNames(getStandingsForPlayoffs()), size));
            }

        }
        playoff = (Playoff) this.playoffs.get(size);
        playoff.markRankings(groupStandings);

        //don't advance to next round with empty results:
        if (playoff.isEmptyPlayoffs()) {
            this.playoffs.remove(size);
            playoff = null;
        } else {
            if (rememberSize > this.getLargestPlayoff()) {
                this.largestPlayoff = rememberSize;
            }
        }

        return playoff;
    }

    public Playoff getPlayoffRandomSeed(int size) {
        Playoff playoff = null;
        ArrayList groupStandings = this.getStandingsNames(getStandingsForPlayoffs());
        int rememberSize = size;

        if (this.playoffs.containsKey(size)) {
            //no reseed for existing random playoff!            
        } else if (this.playoffs.containsKey(size * 2)) { //there is a previous round?            
            this.playoffs.put(size, new Playoff(seedRandomPlayoff(((Playoff) this.playoffs.get(size * 2)).getSurvivors(), size), size));
            //this.playoffs.put(size, new Playoff(seedRandomPlayoffByPairs(((Playoff) this.playoffs.get(size * 2)).getPlayoffPairs(), size), size));
        } else {
            ArrayList playoffStandings = getStandingsNames(getStandingsForPlayoffs());
            size = handleSpecialPlayoffsize(size, playoffStandings);
            this.playoffs.put(size, new Playoff(seedRandomPlayoff(playoffStandings, size), size));
        }

        playoff = (Playoff) this.playoffs.get(size);
        playoff.markRankings(groupStandings);

        //don't advance to next round with empty results:
        if (playoff.isEmptyPlayoffs()) {
            this.playoffs.remove(size);
            playoff = null;
        } else {
            if (rememberSize > this.getLargestPlayoff()) {
                this.largestPlayoff = rememberSize;
            }
        }

        return playoff;
    }

    public void clearPlayoffs() {
        this.playoffs.clear();
        this.largestPlayoff = 0;
    }

    public void clearPlacementMatches() {
        this.placementMatches = null;
    }

    public void clearBronzeMatch() {
        this.bronzeMatch = null;
    }

    /**
     * Need to return an ordered list of playoff pairs
     * 
     * @param playerStandings
     * @TODO indexoutofbounds luodessa liian iso playoff
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

    /**
     * Need to return a random list of playoff pairs
     *
     * @param playerStandings
     * @TODO indexoutofbounds luodessa liian iso playoff
     * @return
     */
    public ArrayList seedRandomPlayoff(ArrayList playerStandings, int size) {
        ArrayList newPairs = new ArrayList(size);

        for (int i = 0; i < size / 2; i++) {
            newPairs.add(playerStandings.get(0 + i));
            newPairs.add(playerStandings.get(size - (i + 1)));
        }

        int newi = 0;
        String tmp = null;

        for (int i = 0; i < newPairs.size(); i++) {
            newi = (int) (Math.random() * newPairs.size());
            if (i != newi) {
                tmp = (String) newPairs.set(newi, newPairs.get(i));
                newPairs.set(i, tmp);
            }
        }

        return newPairs;
    }

    //** not in use */
    public ArrayList seedRandomPlayoffByPairs(PlayoffPair[] playoffMatches, int size) {
        ArrayList newPairs = new ArrayList(size);
        //order first players
        for (int i = 0; i < size / 2; i++) {
            newPairs.add(playoffMatches[0 + i].getWinner());
            newPairs.add(playoffMatches[size - (i + 1)].getWinner());
        }

        int newi = 0;
        String tmp = null;
        for (int i = 0; i < newPairs.size(); i++) {
            newi = (int) (Math.random() * newPairs.size());
            if (i != newi) {
                tmp = (String) newPairs.set(newi, newPairs.get(i));
                newPairs.set(i, tmp);
            }
        }

        return newPairs;
    }

    /**
     * Need to return a list of playoff pairs ordered for the static playoff
     * created from ordered list of matches
     * @param playerStandings
     * @TODO indexoutofbounds luodessa liian iso playoff
     * @return
     */
    public ArrayList seedStaticPlayoff(PlayoffPair[] playoffMatches, int size) {
        ArrayList newPairs = new ArrayList(size);
        //order first players
        for (int i = 0; i < size - 1; i++) {
            newPairs.add(playoffMatches[0 + i].getWinner());
            newPairs.add(playoffMatches[size - (i + 1)].getWinner());
        }

        return newPairs;
    }

    /**
     * Need to return a list of playoff pairs ordered for the static playoff
     *
     * @param playerStandings
     * @TODO indexoutofbounds luodessa liian iso playoff
     * @return
     * @TODO NOT WORKING
     */
    public ArrayList seedStaticPlayoff2(ArrayList playerStandings, int size) {
        ArrayList newPairs = new ArrayList(size);
        //order first players
        for (int i = 0; i < size - 1; i++) {
            newPairs.add(playerStandings.get(0 + i));
            newPairs.add(playerStandings.get(size - (i + 1)));
        }

        ArrayList newPairs2 = new ArrayList(size);
        //then order matches
        for (int i = 0; i < newPairs.size(); i += 2) {
            newPairs2.add(i, newPairs.get(i));
            //newPairs2.add(i+1, newPairs.get(i+1));

            //newPairs2.add(newPairs.size()-i-1, newPairs.get(i+2));
            newPairs2.add(newPairs.size() - i, newPairs.get(i + 3));
        }

        return newPairs2;
    }

    public String getSeedingModel() {
        return this.seedingModel;
    }

    public void setSeedingModel(String seedingModel) {
        this.seedingModel = seedingModel;
    }

    /**
     * Checks that existing playoffrounds have winners
     * @return
     */
    public boolean isPlayoffRoundFinished() {
        if (this.largestPlayoff == 0) {
            return true;
        }

        boolean finished = true;
        for (Object playoff : this.playoffs.values()) {
            if (!((Playoff) playoff).isFinished()) {
                finished = false;
            }
        }

        return finished;
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

    private int handleSpecialPlayoffsize(int size, ArrayList playoffStandings) {
        //special cases
        if (size == 6) {
            playoffStandings.add(6, "X");
            playoffStandings.add(7, "X");
            size = 8;
        } else {
            if (size == 12) {
                playoffStandings.add(12, "X");
                playoffStandings.add(13, "X");
                playoffStandings.add(14, "X");
                playoffStandings.add(15, "X");
                size = 16;
            }
        }
        return size;
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
        Date lastModified = null;
        try {
            lastModified = new Date(file.lastModified());
        } catch (SecurityException se) {
            System.err.println("Cannot get file modification date: " + se);
        }
        if (lastModified != null) {
            date = PELIJARDATE.format(lastModified.getTime());
        }

        //rules = ResourceBundle.getBundle("Rules");added for changeable division sizes
        this.loadRules(rules);
        divisions = new Vector();
        numberOfDivisions = 1;
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new LineNumberReader(new FileReader(file.getName()));
            numberOfDivisions = Tools.parseIntAfter("TOURNAMENT-SIZE:", bufferedreader.readLine());
            for (int i = 0; i < numberOfDivisions; i++) {
                divisions.add(new Division(bufferedreader));
            }

            if (bufferedreader.ready()) { //there's a playoff too
                int playoffsSize = Tools.parseIntAfter("PLAYOFFS-SIZE:", bufferedreader.readLine());
                this.seedingModel = Tools.parseStringAfter("SEEDINGMODEL:", bufferedreader.readLine());
                for (int i = 0; i < playoffsSize; i++) {
                    Playoff playoff = new Playoff(bufferedreader);
                    int size = playoff.getSize();
                    playoffs.put(size, playoff);
                    if (size > this.getLargestPlayoff()) { //needed for placementmatches
                        this.largestPlayoff = size;
                    }
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

            if (bufferedreader.ready()) { //there's a bronze match too
                int playoffsSize = Tools.parseIntAfter("BRONZEMATCH:", bufferedreader.readLine());
                for (int i = 0; i < playoffsSize; i++) {
                    this.bronzeMatch = new Playoff(bufferedreader);
                }
                if (!bufferedreader.readLine().equals("END-OF-BRONZEMATCH")) {
                    throw new FileFormatException();
                }
            }


            bufferedreader.close();
        } catch (IOException ioexception) {
            throw ioexception;
        } catch (FileFormatException fileformatexception) {
            throw new FileFormatException(fileformatexception, ((LineNumberReader) bufferedreader).getLineNumber());
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
        switch (numberOfDivisions) { //@TODO this as dynamic one-liner
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
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E", displayName + "F"
                        });

            case 7: // '\007'
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E", displayName + "F", displayName + "G"
                        });

            case 8: // '\b'
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E", displayName + "F", displayName + "G", displayName + "H"
                        });

            case 9:
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E", displayName + "F", displayName + "G", displayName + "H", displayName + "I"
                        });

            case 10:
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E", displayName + "F", displayName + "G", displayName + "H", displayName + "I", displayName + "J"
                        });

            case 11:
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E", displayName + "F", displayName + "G", displayName + "H", displayName + "I", displayName + "J", displayName + "K"
                        });

            case 12:
                return (new String[]{
                            displayName + "A", displayName + "B", displayName + "C", displayName + "D", displayName + "E", displayName + "F", displayName + "G", displayName + "H", displayName + "I", displayName + "J", displayName + "K", displayName + "L"
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
                saveStandingsWithPlayoffs(printwriter);
                break;

            case 5:
                saveMatchesByPlayer(printwriter);
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
        List rounds = getPlayoffsSortedKeySet();
        if (!rounds.isEmpty()) {
            printwriter.println("PLAYOFFS-SIZE:" + getNumberOfPlayoffs());
            printwriter.println("SEEDINGMODEL:" + getSeedingModel());
            for (Iterator i = rounds.iterator(); i.hasNext();) {
                ((Playoff) playoffs.get(i.next())).save(printwriter);
            }
        }
        if (this.placementMatches != null) {
            printwriter.println("PLACEMENTMATCHES:" + 1);
            this.placementMatches.save(printwriter);
            printwriter.println("END-OF-PLACEMENTMATCHES");
        }
        if (this.bronzeMatch != null) {
            printwriter.println("BRONZEMATCH:" + 1);
            this.bronzeMatch.save(printwriter);
            printwriter.println("END-OF-BRONZEMATCH");
        }

    }

    //added by aulaskar to help organising final groups
    /** get combined standings of all divisions 
     * Different division sizes are compensated */
    public ArrayList getStandings() {
        ArrayList divisions = new ArrayList();
        ArrayList overallstandings = new ArrayList();
        int divisionMaxNoOfMatches = 0;
        int divTimes = 1; //how many round robins?

        for (int i = 0; i < getNumberOfDivisions(); i++) {
            divisions.add(getDivision(i).getStandings());        //trust that the first division allways exists
            int divSize = getDivision(i).getNumberOfPlayers();
            int divRounds = getDivision(i).getNumberOfRounds();
            divTimes = divRounds / divSize;
            if (divSize > divisionMaxNoOfMatches) {
                divisionMaxNoOfMatches = divSize;
            }
        }

        int maxdivisionsize = ((ArrayList) divisions.get(0)).size() + 2;

        //order combined standings of all divisions
        for (int i = 0; i < maxdivisionsize; i++) {
            TreeSet treeset = new TreeSet(new SeriesTableEntryComparator());

            for (int j = 0; j < getNumberOfDivisions(); j++) {
                try {
                    SeriesTableEntry playerInGroup = (SeriesTableEntry) ((ArrayList) divisions.get(j)).get(i);
                    //compensate only if many different divisions, and only one round robin
                    if (getNumberOfDivisions() > 1 && divTimes == 1) {
                        playerInGroup.setCompensatedMatches(divisionMaxNoOfMatches - (playerInGroup.getGames() + 1));
                    }
                    treeset.add(playerInGroup);
                } catch (IndexOutOfBoundsException ie) {
                    //nothing, divisions may have different sizes
                }
            }

            overallstandings.addAll(treeset);
            treeset.clear();
        }

        return overallstandings;
    }

    /** get combined standings of all divisions, but preserve mutual ordering for 2-group playoffs */
    public ArrayList getStandingsForPlayoffs() {
        ArrayList divisions = new ArrayList();
        ArrayList overallstandings = new ArrayList();

        for (int i = 0; i < getNumberOfDivisions(); i++) {
            divisions.add(getDivision(i).getStandings());        //trust that the first division allways exists
        }
        int maxdivisionsize = ((ArrayList) divisions.get(0)).size() + 2;

        //order combined standings of all divisions
        for (int i = 0; i < maxdivisionsize; i++) {

            for (int j = 0; j < getNumberOfDivisions(); j++) {
                try {
                    overallstandings.add(((ArrayList) divisions.get(j)).get(i));
                } catch (IndexOutOfBoundsException ie) {
                    //nothing, divisions may have different sizes
                }
            }

        }

        return overallstandings;
    }

    //by aulaskar
    public ArrayList addPlayoffsToStandings(ArrayList overallstandings) {
        //Set rounds = playoffs.keySet();
        List rounds = getPlayoffsSortedKeySet();
        Boolean isFirst = true;
        int x = 0;
        for (Iterator i = rounds.iterator(); i.hasNext();) { //each level of playoffs, mark placements from basic group first
            Integer size = (Integer) i.next();
            ((Playoff) playoffs.get(size)).markRankings(overallstandings); //HACK just in case placements are not set in headless mode
        }
        for (Iterator i = rounds.iterator(); i.hasNext();) { //each level of playoffs, now overallstandings can be modified safely
            Integer size = (Integer) i.next();
            if (isFirst) {
                isFirst = false;
                if (size == 2) { //final, get the winner
                    Object winner = ((Playoff) playoffs.get(size)).getSurvivors().get(0);
                    if (winner == null) {
                        winner = "?";
                    }
                    overallstandings.set(x++, winner);
                } else { //playoff not finished
                    return overallstandings;
                }
            }
            //the rest are all losers (but they should  be ordered based on the group)
            for (Object loser : ((Playoff) playoffs.get(size)).getLosers()) {
                if (loser == null) {
                    overallstandings.set(x++, "?");
                } else {
                    if (!((String) loser).equals("X")) {
                        overallstandings.set(x++, loser);
                    }
                }
            }
        }

        return overallstandings;
    }

    //by aulaskar
    public ArrayList addPlacementMatchesToStandings(ArrayList overallstandings) {
        //modify based on placementmatches
        if (this.placementMatches != null) {
            for (Object o : this.placementMatches.getPlayoffPairs()) {
                Object winner = ((PlayoffPair) o).getWinner();
                if (winner != null) {
                    Object loser = ((PlayoffPair) o).getLoser();
                    int winnerplace = overallstandings.indexOf(winner);
                    int loserplace = overallstandings.indexOf(loser);
                    if (winnerplace >= 0 && loserplace >= 0) { //swap?
                        if (loserplace < winnerplace) {
                            overallstandings.set(loserplace, winner);
                            overallstandings.set(winnerplace, loser);
                        }
                    } else {
                        System.err.println("placementmatches can't find " + winner + " or " + loser);
                    }
                }
            }
        } else {
            //System.err.println("placementmatches null");
        }

        return overallstandings;
    }

    //by aulaskar
    public ArrayList addBronzeMatchToStandings(ArrayList overallstandings) {
        //modify based on placementmatches
        if (this.bronzeMatch != null) {
            for (Object o : this.bronzeMatch.getPlayoffPairs()) {
                Object winner = ((PlayoffPair) o).getWinner();
                if (winner != null) {
                    Object loser = ((PlayoffPair) o).getLoser();
                    int winnerplace = overallstandings.indexOf(winner);
                    int loserplace = overallstandings.indexOf(loser);
                    if (winnerplace >= 0 && loserplace >= 0) { //swap?
                        if (loserplace < winnerplace) {
                            overallstandings.set(loserplace, winner);
                            overallstandings.set(winnerplace, loser);
                        }
                    } else {
                        System.err.println("bronzematches can't find " + winner + " or " + loser);
                    }
                }
            }
        } else {
            //System.err.println("bronzematches null");
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

    public ArrayList getStandingsNames(ArrayList overallstandings) {
        ArrayList justnames = new ArrayList();
        //ArrayList overallstandings = getStandings();
        for (Iterator iterator = overallstandings.iterator(); iterator.hasNext();) {
            justnames.add(((SeriesTableEntry) iterator.next()).getName());
        }

        return justnames;
    }

    public ArrayList getOverAllStandings() {
        ArrayList overallstandings = addPlacementMatchesToStandings(addPlayoffsToStandings(getStandingsNames(getStandings())));
        overallstandings = addBronzeMatchToStandings(overallstandings);

        return overallstandings;
    }

    //added by aulaskar
    /** print combined standings of all divisions */
    public void saveStandingsWithPlayoffs(PrintWriter printwriter) {
        ArrayList overallstandings = getOverAllStandings();

        //print to file
        for (Iterator iterator = overallstandings.iterator(); iterator.hasNext();) {
            printwriter.println(iterator.next());
        }

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
            HtmlTools.pageBreak(printwriter);
        }
        HtmlTools.br(printwriter);
        HtmlTools.hr(printwriter);
        HtmlTools.br(printwriter);
        HtmlTools.outro(printwriter);
    }

    public void saveMatchesByPlayer(PrintWriter printwriter) {
        HtmlTools.intro(printwriter, messages.getString("matchProgrammeByPlayer"));
        HtmlTools.hr(printwriter);
        /*for (int i = 0; i < divisions.size(); i++) {
        getDivision(i).saveMatches(printwriter);
        HtmlTools.pageBreak(printwriter);
        }*/
        ArrayList players = getOverAllStandings();
        StringWriter stringwriter = new StringWriter();
        PrintWriter sprintwriter = new PrintWriter(stringwriter, true);
        this.save(sprintwriter);
        String tnmt = stringwriter.toString();
        StringBuilder byPlayer = new StringBuilder();
        Object[] lines = tnmt.split(System.getProperty("line.separator"));
        for (Object player : players) {
            printwriter.print(player);
            HtmlTools.br(printwriter);
            String printout = "";
            for (Object line : lines) {                
                if(((String)line).matches("^ROUND:.*")) {                    
                    printout = "<br/>" + line + " ";
                }
                if(((String)line).matches(".*" + (String)player + ".*")) {                    
                    ((String)line).replaceFirst(":", "-"); //modifying inside loop
                    printwriter.print(printout + line);
                }
            }
            HtmlTools.br(printwriter);
        }
        //printwriter.print(tnmt);
        sprintwriter.close();

        HtmlTools.br(printwriter);
        HtmlTools.hr(printwriter);
        HtmlTools.br(printwriter);
        HtmlTools.outro(printwriter);
    }

    /**
     * print html-file with embedded tnmt
     * @param printwriter
     */
    public void saveAll(PrintWriter printwriter) {
        //won't work
        //if(rules.getString("useVersion1HtmlOutput").equalsIgnoreCase("false")) {        
        if (System.getProperty("TournamentUseVersion1HtmlOutput") == null) {

            //use header.txt            
            String header = Constants.getHeader().toString();
            header = header.replaceAll("<!-- TITLE -->", System.getProperty("TournamentFileName"));
            header = header.replaceAll("<!-- DATE -->", date);

            /*hidden tnmt-output is always printed*/
            StringWriter stringwriter = new StringWriter();
            PrintWriter sprintwriter = new PrintWriter(stringwriter, true);
            sprintwriter.println("<TNMT>");
            this.save(sprintwriter);
            sprintwriter.println("</TNMT>");
            header = header.replaceAll("<!-- TNMT -->", stringwriter.toString());
            sprintwriter.close();

            printwriter.print(header);

            String output = "";
            String[] divisiontitles = getDivisionTitles();
            int numberOfDivisions = getNumberOfDivisions();
            ArrayList playoffSeparators = new ArrayList<Integer>(); //separate playoff-qualified players in output
            playoffSeparators.add(this.getRealPlayoffSize() / numberOfDivisions);

            //use template.txt
            for (int i = 0; i < numberOfDivisions; i++) {
                output += getDivision(i).saveAll(playoffSeparators, String.valueOf(i));
                output = output.replaceFirst("<!-- HEADING -->",
                        divisiontitles[i] + " " + messages.getString("templateHeading"));
                //HtmlTools.hr(printwriter);
                output += "<p style=\"page-break-before: always\"/>";
            }

            output += Constants.getFooter().toString();
            output = output.replaceAll("<VERSION/>", System.getProperty("Peli.jarVersion"));

            if (placementMatches != null) {
                output = output.replaceAll("<!--HIDE_PLACEMENTMATCHES", "");
                output = output.replaceAll("HIDE_PLACEMENTMATCHES-->", "");
                output = output.replaceAll("<PLACEMENTMATCHES/>", placementMatches.saveAll());
            }
            if (playoffs.size() > 0) {
                output = output.replaceAll("<!--HIDE_PLAYOFF", "");
                output = output.replaceAll("HIDE_PLAYOFF-->", "");
                //if playoff, save playoff
                StringBuilder playoffoutput = new StringBuilder();
                List rounds = getPlayoffsSortedKeySet();
                if (!rounds.isEmpty()) {
                    Collections.reverse(rounds);
                    for (Iterator i = rounds.iterator(); i.hasNext();) {
                        Integer round = (Integer) i.next();
                        if (round == 2) {
                            //bronze
                            playoffoutput.append("<!--HIDE_BRONZEMATCH");
                            playoffoutput.append("<BRONZEMATCH/>");
                            playoffoutput.append("HIDE_BRONZEMATCH-->");
                        }
                        playoffoutput.append(((Playoff) playoffs.get(round)).saveAll());
                    }
                }
                output = output.replaceAll("<PLAYOFF/>", playoffoutput.toString());
            }
            if (bronzeMatch != null) {
                output = output.replaceAll("<!--HIDE_BRONZEMATCH", "");
                output = output.replaceAll("HIDE_BRONZEMATCH-->", "");
                output = output.replaceAll("<BRONZEMATCH/>", bronzeMatch.saveAll());
            }
            if (playoffs.size() > 0) {
                StringBuilder standingsoutput = new StringBuilder();
                standingsoutput.append("<ol class=\"playoff\">").append(System.getProperty("line.separator"));
                for (Object ob : getOverAllStandings()) {
                    standingsoutput.append("<li class=\"standings\">").append((String) ob).append("</li>").append(System.getProperty("line.separator"));
                }
                standingsoutput.append("</ol>").append(System.getProperty("line.separator"));
                output = output.replaceAll("<!--HIDE_STANDINGS", "");
                output = output.replaceAll("HIDE_STANDINGS-->", "");
                output = output.replaceAll("<STANDINGS/>", standingsoutput.toString());
            }

            //use footer.txt
            printwriter.print(output);

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
        return this.largestPlayoff;
    }

    /**
     * calculate the real playoffsize ignoring X-players
     * @return
     */
    public int getRealPlayoffSize() {
        int realsize = 0;
        if (this.getLargestPlayoff() > 0) {
            for (PlayoffPair playoffpair : ((Playoff) this.playoffs.get(this.getLargestPlayoff())).getPlayoffPairs()) {
                if (!playoffpair.getHomeTeam().equalsIgnoreCase("X") && !playoffpair.getAwayTeam().equalsIgnoreCase("X")) {
                    realsize += 2;
                } else { //at least one player is X
                    realsize++;
                }
            }
        }

        return realsize;
    }
}
