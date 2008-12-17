/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author aulaskar
 */
public class PlayoffPair {
    //rounds
    private String homeTeam,  awayTeam;
    private int homeWins,  awayWins;
//    private Playoff mother;
    private ArrayList matches = new ArrayList();
    private final int MAXMATCHES = 7;

    public PlayoffPair(Playoff playoff, String homeTeam, String awayTeam) {
        if (playoff == null) {
            throw new IllegalArgumentException("Empty playoffs attempted!");
        }

        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
//        this.mother = playoff;
        for (int i = 0; i < MAXMATCHES; i++) {
            this.matches.add(new Match(homeTeam, awayTeam));
        }
    }

    PlayoffPair(BufferedReader bufferedreader) throws FileFormatException, IOException {
        try {            
            if (!bufferedreader.readLine().equals("PLAYOFFPAIR")) {                
                throw new FileFormatException();
            }
            for (int i = 0; i < MAXMATCHES; i++) {                
                this.matches.add(new Match(bufferedreader.readLine()));
            }
            this.homeTeam = ((Match) this.matches.get(0)).home();
            this.awayTeam = ((Match) this.matches.get(0)).visitor();            
            if (!bufferedreader.readLine().equals("END-OF-PLAYOFFPAIR")) {
                throw new FileFormatException();
            }
        } catch (FileFormatException fileformatexception) {
            throw fileformatexception;
        } catch (IOException ioexception) {
            throw ioexception;
        }

        this.updateWins();
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
        if (this.homeWins > this.awayWins) {
            return this.getHomeTeam();
        } else if (this.homeWins < this.awayWins) {
            return this.getAwayTeam();
        }
        return null;
    }

    public String getLoser() {
        if (this.homeWins < this.awayWins) {
            return this.getHomeTeam();
        } else if (this.homeWins > this.awayWins) {
            return this.getAwayTeam();
        }
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

    public void save(PrintWriter printwriter) {
        for (int i = 0; i < this.matches.size(); i++) {
            ((Match) this.matches.get(i)).save(printwriter);
        }
    }

    public String toString() {
        return homeTeam + "-" + awayTeam + ":" + matches;
    }
}
