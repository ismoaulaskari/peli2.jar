/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package peli;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aulaskar
 */
public class Playoff {
    
    private int size;
    private List players;
    //rounds
    private Tournament mother;
    private PlayoffPair[] playoffPairs;
    
    
    public Playoff(Tournament tournament, List players, int size) {
        if(players == null || size == 0) {
            throw new IllegalArgumentException("Empty playoffs attempted!");
        }
        this.players = players;
        this.size = size;
        this.mother = tournament;
        this.playoffPairs = createPairs(players, size);
    }

    public Playoff(Tournament tournament, List players) {
        this(tournament, players, players.size());
    }
    
    //won't support empty players
    private PlayoffPair[] createPairs(List players, int size) {
        if(size > players.size()) {
            size = players.size();
        }
        PlayoffPair[] pairs = new PlayoffPair[size/2];
        for(int i=0; i<size/2; i++) {
            //pairs[i] = new PlayoffPair(this, "foo"+i, "bar"+i);        
            pairs[i] = new PlayoffPair(this, (String)players.remove(0), (String)players.remove(0));        
        }
                
        return pairs;
    }
    
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List getPlayers() {
        return players;
    }

    public void setPlayers(List players) {
        this.players = players;
    }

    public List getSurvivors() {
        ArrayList survivors = new ArrayList();
        for(int i=0; i<playoffPairs.length; i++) {
            survivors.add(playoffPairs[i].getWinner());
        }
        
        return survivors;
    }
    
    public List getLosers() {
        ArrayList losers = new ArrayList();
        for(int i=0; i<playoffPairs.length; i++) {
            losers.add(playoffPairs[i].getWinner());
        }
        
        return losers;
    }

    public PlayoffPair[] getPlayoffPairs() {
        return playoffPairs;
    }
        
}
