/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package peli;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author aulaskar
 */
public class Playoff {
    
    private int size;
    private List players;
    //rounds
    private HashMap matches;
    
    
    public Playoff(List players, int size) {
        if(players == null || size == 0) {
            throw new IllegalArgumentException("Empty playoffs attempted!");
        }
        this.players = players;
        this.size = size;
    }

    public Playoff(List players) {
        this(players, players.size());
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
        return players;
    }
    
    public List getLosers() {
        return players;
    }
        
}
