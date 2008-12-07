/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.util.ArrayList;

/**
 *
 * @author aulaskar
 */
public class PlayoffPair {
    //rounds
    private String homeTeam,  awayTeam;
    private int homeWins,  awayWins;
    private Playoff mother;
    private ArrayList matches = new ArrayList();

    public PlayoffPair(Playoff playoff, String homeTeam, String awayTeam) {
        if (playoff == null) {
            throw new IllegalArgumentException("Empty playoffs attempted!");
        }

        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.mother = playoff;
        for(int i=0; i<7; i++) {
            this.matches.add(new Match(homeTeam, awayTeam));
        }
    }

    public int getWins(String player) {
        if (player.equals(getHomeTeam())) {
            return this.homeWins;
        } else if (player.equals(getAwayTeam())) {
            return this.awayWins;
        }

        return 0;
    }

    public void updateWins() {
        this.homeWins = 0;
        this.awayWins = 0;
        
        for (int i = 0; i < this.getMatches().size(); i++) {
            Match match = (Match) this.getMatches().get(i);
            if (match.homeGoals() > match.visitorGoals()) {
                this.homeWins++;
            } else if (match.homeGoals() < match.visitorGoals()) {
                this.awayWins++;
            }
        }
                
    }

    public String getWinner() {
        if(this.homeWins > this.awayWins) return this.getHomeTeam();
        else if(this.homeWins < this.awayWins) return this.getAwayTeam();
        
        return null;
    }

    public String getLoser() {
        if(this.homeWins < this.awayWins) return this.getHomeTeam();
        else if(this.homeWins > this.awayWins) return this.getAwayTeam();

        return null;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public ArrayList getMatches() {
        return this.matches;
    }

    public void setMatches(ArrayList matches) {
        this.matches = matches;
    }

    public String getHomeWins() {
        return String.valueOf(homeWins);
    }

    public String getAwayWins() {
        return String.valueOf(awayWins);
    }
    

    public String toString() {        
        return homeTeam + "-" + awayTeam + ":" + matches;        
    }
}
