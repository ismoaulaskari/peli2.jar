package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SeriesTable.java

import java.io.PrintWriter;
import java.util.*;

/**
 * Seriestable (overall, no mutuals) that knows its elements and how to print
 * itself either as txt or html
 * v1.1 added correct mutual ordering as a extra routine
 * @author aulaskar
 * 
 */
public class SeriesTable {

        private SeriesTableEntry table[];
	private Mutual mutual;

	SeriesTable(Hashtable hashtable, Mutual mutual1)
    {
		this.mutual = mutual1;
        TreeSet treeset = new TreeSet(new SeriesTableEntryComparator());
        SeriesTableEntry seriestableentry;
        for(Enumeration enumeration = hashtable.elements(); enumeration.hasMoreElements(); treeset.add(seriestableentry))
            seriestableentry = (SeriesTableEntry)enumeration.nextElement();

        table = new SeriesTableEntry[treeset.size()];
        int i = 0;
        for(Iterator iterator = treeset.iterator(); iterator.hasNext();)
            table[i++] = (SeriesTableEntry)iterator.next();

        //      added by aulaskar
        if(System.getProperty("TournamentOrderByMutualMatch").equalsIgnoreCase("true"))
        	sortMutually(mutual); 
    }
	
	/**
	 * mutual sort for seriestable-class that needs a Mutual from Division with complete results 
	 * */
    public void sortMutually(Mutual mutual)
    {
    	boolean debug = false;
    	if(System.getProperty("debug") != null)
    		debug = true;
        // mutual sort (messy but works v.1.0)
    	boolean last_player = false;
        int j = 0;
        int k = 0;
        int mutualsize = 0;
        int points = 0; 
        int lastpoints = table[0].getPoints();
        /* keep the results of currently handled players with equal points */
        Hashtable currentmutualplayers = new Hashtable();
    	ArrayList neworderedtable = new ArrayList();
    	TreeSet mutualtreeset = new TreeSet(new MutualSeriesTableEntryComparator());
    	
        // go thru all the players one by one
    	for(j = 0; j <= table.length; j++) {
    		
    		if(last_player) 
    			break;
    		if(j >= table.length) {//horrible hack to enable showing of the bottom players
    			j = table.length -1;
    			last_player = true;
    			lastpoints--;
    		}
    		
    		points = table[j].getPoints();
    		if(debug) System.out.println("Examining " + table[j].getName());
    		    		 
    		if(points != lastpoints) { //this player is not part in mutual comparison    			
    			if(debug) System.out.println("points not lastpoints");
    			mutualtreeset.clear();    			
    			mutualsize = currentmutualplayers.size();
    			String[] names = new String[mutualsize];
    			int l = 0; 
    			//names of the players whose results are needed for mutual comparison
    			Enumeration tmpplayers = currentmutualplayers.elements();
    			
    			while(tmpplayers.hasMoreElements()) { 
    				names[l++] = ((SeriesTableEntry)tmpplayers.nextElement()).getName();
    				if(debug) System.out.println("comparing " + names[l-1] + " with " + lastpoints + "p");
    			}
    			
    			//create new player entries that don't have results yet
    			tmpplayers = currentmutualplayers.elements();
    			while(tmpplayers.hasMoreElements()) {
    				String currentplayer = ((SeriesTableEntry)tmpplayers.nextElement()).getName(); 
    				MutualSeriesTableEntry mutualplayer = new MutualSeriesTableEntry(currentplayer, (SeriesTableEntry)currentmutualplayers.get(currentplayer));

    				//add only mutual results for the named player
    				for(String s : names) {     					 
    					if(! s.equals(currentplayer)) {
    						if(debug) System.out.print(currentplayer + " vs " + s + " : ");
    						Enumeration enumeration;
    						try {
    							enumeration = (Enumeration)mutual.getNativeResult(currentplayer, s); 
    							while(enumeration.hasMoreElements()) { 
    								Match match = new Match(currentplayer, s);
    								match.setResult((String)enumeration.nextElement()); 
    								if(debug) System.out.println(match.getResult());
    								mutualplayer.updateWith(match);    								
    							}    							
    						} 
    						catch (NullPointerException e) {
    							//no results available yet
    							return;
    						}
    					} 
    					else {
    						if(mutualsize > 1) {
    							//for special html output remember that these players are at a tie
    							((SeriesTableEntry)currentmutualplayers.get(currentplayer)).setHasTiedPoints(lastpoints);
    						}
    					}
    				} 
    				mutualtreeset.add(mutualplayer); //add to ordered treeset
    			}

    			//should add the original seriestableentries to list in correct order
    			for(Iterator iterator = mutualtreeset.iterator(); iterator.hasNext();) {
    				SeriesTableEntry player = ((SeriesTableEntry)iterator.next()); 
    				String tmp = player.getName();
    				//we don't want to add players here more than once so let't keep track!
    				if(currentmutualplayers.containsKey(tmp)) {
    					if(debug) System.out.println(player.getRow());
    					neworderedtable.add(currentmutualplayers.get(tmp));
    					currentmutualplayers.remove(tmp);
    				}
    			}
    			//cleanup
    			currentmutualplayers.clear();
    			mutualtreeset.clear();    			
    			lastpoints = points;    				
    		}
    		
    		if(!last_player) {//hack to enable showing of the bottom players
    			//this player is put into the next comparision-round
    			currentmutualplayers.put(((SeriesTableEntry)table[j]).getName(), (SeriesTableEntry)table[j]); // always
    		}
        }
			
    	table = (SeriesTableEntry[])neworderedtable.toArray(new SeriesTableEntry[neworderedtable.size()]);
    	neworderedtable.clear();
    }

	public int size() {
		return table.length;
	}

	public SeriesTableEntry elementAt(int i) {
		return table[i];
	}

	public void htmlSave(PrintWriter printwriter) {
		//sortMutually(this.mutual); //does this help for immediate updates
		HtmlTools.tableIntro(printwriter, false, "100%");
		for (int i = 0; i < size(); i++)
			printwriter.println(elementAt(i).getHtmlTableRow());

		HtmlTools.tableOutro(printwriter);
	}

	public void print(PrintWriter printwriter) {
		//sortMutually(this.mutual); //does this help for immediate updates
		for (int i = 0; i < size(); i++)
			printwriter.println(Tools.format(i + 1, 4) + " "
					+ elementAt(i).getRow() + "  ");

	}

	//hack
	public String toString() {
		StringBuffer seriestable = new StringBuffer();
		
		//sortMutually(this.mutual); //does this help for immediate updates
		for (int i = 0; i < size(); i++)
			seriestable.append(Tools.format(i + 1, 4) + " "
					+ elementAt(i).getRow() + "  \n");

		return seriestable.toString();
	}

	
	
}
