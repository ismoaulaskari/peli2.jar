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
public class PlayoffPair {
        
    //rounds
    private Playoff mother;
    private ArrayList matches = new ArrayList();
    
    
    public PlayoffPair(Playoff playoff, String homeTeam, String awayTeam) {
        if(playoff == null) {
            throw new IllegalArgumentException("Empty playoffs attempted!");
        }
        
        this.mother = playoff;
    }


    

    public List getWinner() {
        return null;
    }
    
    public List getLoser() {
        return null;
    }
        
}
