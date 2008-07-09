package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Tournament.java

import java.io.*;
import java.util.*;

/**
 * A main class that includes final variables set at the start of the program,
 * save routines and rules for division sizes
 * v.1.1 added division sizes as configurable properties
 * fix tournament date here!
 * @author aulaskar
 *
 */
public class Tournament
{

    private void distributePlayers(TreeSet atreeset[], TreeSet treeset)
    {
        for(int i = 0; i < atreeset.length; i++)
            atreeset[i] = new TreeSet(new PlayerComparator());

        int j = 0;
        boolean flag = false;
        for(Iterator iterator = treeset.iterator(); iterator.hasNext();)
        {
            atreeset[j].add((Player)iterator.next());
            if(!flag)
            {
                if(++j == atreeset.length)
                {
                    j--;
                    flag = true;
                }
            } else
            if(--j < 0)
            {
                j++;
                flag = false;
            }
        }

    }

    //modified
    private int calculateNumberOfDivisions(int i)
    {
    	try {
    		//added
    		if(System.getProperty("TournamentHardcodedNumOfDivisions") != null) {
    			int divs = Integer.parseInt(System.getProperty("TournamentHardcodedNumOfDivisions"));
    			if(divs > 0) //zero means no hardcoded number 
    				return divs;
    		}

    		//hacked
    		if(i < Integer.parseInt(System.getProperty("TournamentMin2Divisions")))
    			return 1;
    		if(i < Integer.parseInt(System.getProperty("TournamentMin3Divisions")))
    			return 2;
    		if(i < Integer.parseInt(System.getProperty("TournamentMin4Divisions")))
    			return 3;
    		if(i < Integer.parseInt(System.getProperty("TournamentMin5Divisions")))
    			return 4;
    		if(i < Integer.parseInt(System.getProperty("TournamentMin6Divisions")))
    			return 5;
    		if(i < Integer.parseInt(System.getProperty("TournamentMin7Divisions")))
    			return 6;
    		return i >= Integer.parseInt(System.getProperty("TournamentMin8Divisions")) ? 8 : 7;
    	} 
    	catch (Exception e) {
			System.err.println("Reading rules failed, trying with defaults");
			e.printStackTrace();
			if(i < 26)
    			return 1;
    		if(i < 45)
    			return 2;
    		if(i < 60)
    			return 3;
    		if(i < 75)
    			return 4;
    		if(i < 90)
    			return 5;
    		if(i < 105)
    			return 6;
    		return i >= 120 ? 8 : 7;
    	
		}
    }

    private void loadRules(ResourceBundle rules1) {

    	
    	try {
    		System.setProperty(	"TournamentHardcodedNumOfDivisions", rules1.getString("hardcodedNumOfDivisions"));    		
    		System.setProperty(	"TournamentMin2Divisions", rules1.getString("min2Divisions"));  
    		System.setProperty(	"TournamentMin3Divisions", rules1.getString("min3Divisions"));
    		System.setProperty(	"TournamentMin4Divisions", rules1.getString("min4Divisions"));  
    		System.setProperty(	"TournamentMin5Divisions", rules1.getString("min5Divisions"));
    		System.setProperty(	"TournamentMin6Divisions", rules1.getString("min6Divisions"));
    		System.setProperty(	"TournamentMin7Divisions", rules1.getString("min7Divisions"));
    		System.setProperty(	"TournamentMin8Divisions", rules1.getString("min8Divisions"));
    		
    		System.setProperty(	"TournamentPointsPerWin", rules1.getString("pointsPerWin"));
    		System.setProperty(	"TournamentPointsPerTie", rules1.getString("pointsPerTie"));
    		System.setProperty(	"TournamentOrderByMutualMatch", rules1.getString("orderByMutualMatch"));
    		System.setProperty(	"TournamentShowMutualTableTab", rules1.getString("showMutualTableTab"));
    	}
    	catch (SecurityException se) {
    		System.err.println("Setting system properties not supported!");
    	}
    	catch (MissingResourceException me) {
			
		}
    	catch (Exception e) {
    		System.err.println("You need a valid Rules.properties-file. Trying to continue with default rules."); 
    		e.printStackTrace();
    		
    		System.setProperty(	"TournamentMin2Divisions", "26");  
    		System.setProperty(	"TournamentMin3Divisions", "45");
    		System.setProperty(	"TournamentMin4Divisions", "60");  
    		System.setProperty(	"TournamentMin5Divisions", "75");
    		System.setProperty(	"TournamentMin6Divisions", "90");
    		System.setProperty(	"TournamentMin7Divisions", "105");
    		System.setProperty(	"TournamentMin8Divisions", "120");
    		
    		System.setProperty(	"TournamentPointsPerWin", "2");
    		System.setProperty(	"TournamentPointsPerTie", "1");
    		System.setProperty(	"TournamentOrderByMutualMatch", "true");
    	}
    }
    
    Tournament(int i, TreeSet treeset)
    {
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
        for(int j = 0; j < numberOfDivisions; j++)
            divisions.add(new Division("Lohko " + (j + 1), i, atreeset[j]));

    }

    Tournament(File file)
        throws IOException, FileFormatException
    {
        /*locale = new Locale(new String("fi"), new String("FI"));
        messages = ResourceBundle.getBundle("Messages", locale);*/
    	locale = Constants.getInstance().getLocale();
    	messages = Constants.getInstance().getMessages();
    	rules = Constants.getInstance().getRules();
        //rules = ResourceBundle.getBundle("Rules");added for changeable division sizes
        this.loadRules(rules);
        divisions = new Vector();
        numberOfDivisions = 1;
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(file.getName()));
            numberOfDivisions = Tools.parseIntAfter("TOURNAMENT-SIZE:", bufferedreader.readLine());
            for(int i = 0; i < numberOfDivisions; i++)
                divisions.add(new Division(bufferedreader));

            bufferedreader.close();
        }
        catch(IOException ioexception)
        {
            throw ioexception;
        }
        catch(FileFormatException fileformatexception)
        {
            throw fileformatexception;
        }
    }

    public int size()
    {
        return numberOfDivisions;
    }

    public int getNumberOfDivisions()
    {
        return numberOfDivisions;
    }

    public Division getDivision(int i)
    {
        return (Division)divisions.elementAt(i);
    }

    public String[] getDivisionTitles()
    {
        switch(numberOfDivisions)
        {
        case 1: // '\001'
            return (new String[] {
                "Agdur"
            });

        case 2: // '\002'
            return (new String[] {
                "Agdur", "Bulldozer"
            });

        case 3: // '\003'
            return (new String[] {
                "A", "B", "C"
            });

        case 4: // '\004'
            return (new String[] {
                "A", "B", "C", "D"
            });

        case 5: // '\005'
            return (new String[] {
                "A", "B", "C", "D", "E"
            });

        case 6: // '\006'
            return (new String[] {
                "A", "B", "C", "D", "E", "F"
            });

        case 7: // '\007'
            return (new String[] {
                "A", "B", "C", "D", "E", "F", "G"
            });

        case 8: // '\b'
            return (new String[] {
                "A", "B", "C", "D", "E", "F", "G", "H"
            });
        }
        return (new String[] {
            "Lohko A"
        });
    }

    public void save(PrintWriter printwriter, int i)
    {
        switch(i)
        {
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

    public void save(PrintWriter printwriter)
    {
        printwriter.println("TOURNAMENT-SIZE:" + getNumberOfDivisions());
        for(int i = 0; i < getNumberOfDivisions(); i++)
            getDivision(i).save(printwriter);

    }

    //added by aulaskar to help organising final groups
    /** print combined standings of all divisions */
    public void saveStandings(PrintWriter printwriter)
    {
    	ArrayList divisions = new ArrayList();
    	ArrayList overallstandings = new ArrayList();
    	
        for(int i = 0; i < getNumberOfDivisions(); i++)
            divisions.add(getDivision(i).getStandings());

        //trust that the first division allways exists
        int maxdivisionsize = ((ArrayList)divisions.get(0)).size() + 2;
        
        //order combined standings of all divisions
        for(int i = 0; i < maxdivisionsize; i++) {
        	TreeSet treeset = new TreeSet(new SeriesTableEntryComparator());
        	
        	for(int j = 0; j < getNumberOfDivisions(); j++) {
        	    try {	
        	    	treeset.add(((ArrayList)divisions.get(j)).get(i));
        	    }
        	    catch (IndexOutOfBoundsException ie) {
					//nothing, divisions may have different sizes
				}
        	}
        		
        	overallstandings.addAll(treeset);
        	treeset.clear();	
        }
        
        //print to file
        for(Iterator iterator = overallstandings.iterator(); iterator.hasNext();) {
        	printwriter.println(((SeriesTableEntry)iterator.next()).getName());   	
        }
        
    }
    
    public void saveTables(PrintWriter printwriter)
    {
        HtmlTools.intro(printwriter, messages.getString("seriesTable"));
        HtmlTools.hr(printwriter);
        HtmlTools.br(printwriter);
        for(int i = 0; i < divisions.size(); i++)
            getDivision(i).saveTable(printwriter);

        HtmlTools.br(printwriter);
        HtmlTools.hr(printwriter);
        HtmlTools.br(printwriter);
        HtmlTools.outro(printwriter);
    }

    public void saveMatches(PrintWriter printwriter)
    {
        HtmlTools.intro(printwriter, messages.getString("matchProgramme"));
        HtmlTools.hr(printwriter);
        for(int i = 0; i < divisions.size(); i++)
            getDivision(i).saveMatches(printwriter);

        HtmlTools.br(printwriter);
        HtmlTools.hr(printwriter);
        HtmlTools.br(printwriter);
        HtmlTools.outro(printwriter);
    }

    public void saveAll(PrintWriter printwriter)
    {
        HtmlTools.intro(printwriter, messages.getString("seriesTableAndMutualMatches"));
        HtmlTools.insertDate(printwriter, date); //fixed
        HtmlTools.hr(printwriter);
        for(int i = 0; i < getNumberOfDivisions(); i++)
        {
            getDivision(i).saveAll(printwriter);
            HtmlTools.hr(printwriter);
        }

        HtmlTools.outro(printwriter);
    }

    private Locale locale;
    private ResourceBundle messages;
    private ResourceBundle rules; //hack
    private Vector divisions;
    private int numberOfDivisions;
    private static final String date = "x.x.2000";
}
