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
    private int homePlacement,  awayPlacement;
    //    private Playoff mother;
    private ArrayList matches = new ArrayList();
    private final int MAXMATCHES = Constants.getMAXMATCHES();

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
        for (Object m : getMatches()) {
            ((Match) m).setHome(homeTeam);
        }
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
        for (Object m : getMatches()) {
            ((Match) m).setVisitor(awayTeam);
        }
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

    //tnmt
    public void save(PrintWriter printwriter) {
        for (int i = 0; i < this.matches.size(); i++) {
            ((Match) this.matches.get(i)).save(printwriter);
        }
    }

    //html 
    public String saveAll() {
        boolean emptyrow = true;
        StringBuilder output = new StringBuilder();
        if (this.getHomeTeam().equals(this.getWinner())) {
            emptyrow = false;
            output.append("<u class=\"playoff\">");
            output.append(this.getHomeTeam());
            output.append("</u>");
            output.append("-");
            output.append(this.getAwayTeam());
        } else {
            if (this.getAwayTeam().equals(this.getWinner())) {
                emptyrow = false;
                output.append(this.getHomeTeam());
                output.append("-");
                output.append("<u class=\"playoff\">");
                output.append(this.getAwayTeam());
                output.append("</u>");
            } else {
//                emptyrow = true; //@TODO show situations where result even
                if (this.getPlayedMatches() > 0) {
                    emptyrow = false;
                    output.append(this.getHomeTeam());
                    output.append("-");
                    output.append(this.getAwayTeam());
                }

            }
        }
        if (!emptyrow) {
            output.append(" : ").append(this.getHomeWins()).append("-").append(this.getAwayWins()).append(" (");
            for (int i = 0; i < this.matches.size(); i++) {
                Match match = (Match) this.matches.get(i);
                output.append(match.saveAll());
                if (i < (getPlayedMatches() - 1)) {
                    if (match.isOver()) {
                        output.append(", ");
                    }
                }
            }
            output.append(")<br class=\"playoff\"/><br class=\"playoff\"/>");
        }

        return output.toString();
    }

    public String toString() {
        return homeTeam + "-" + awayTeam + ":" + matches;
    }

    public String getAwayPlacement() {
        return String.valueOf(awayPlacement) + ".";
    }

    public String getHomePlacement() {
        return String.valueOf(homePlacement) + ".";
    }

    public int getLoserPlacement() {
        int placement = 0;
        if (getLoser().equals(awayTeam)) {
            placement = awayPlacement;
        } else {
            placement = homePlacement;
        }

        return placement;
    }

    public void setAwayPlacement(int ranking) {
        this.awayPlacement = ranking;
    }

    public void setHomePlacement(int ranking) {
        this.homePlacement = ranking;
    }

    public void setAwayPlacement(String string) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setHomePlacement(String string) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int getPlayedMatches() {
        int played = 0;
        for (int i = 0; i < this.matches.size(); i++) {
            if (((Match) this.matches.get(i)).isOver()) {
                played++;
            }
        }

        return played;
    }
}
