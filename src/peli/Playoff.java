/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * v.1.14.1 support for different seeding models and
 * playoffs for 6 and 12 players have been completed without
 * changing this class
 * Check Tournament.java and TournamentGUI.java
 * @author aulaskar
 */
public class Playoff {

    private boolean emptyPlayoffs = false;
    private int size;
    private List<String> players;
    //rounds
    //private Tournament mother;
    private PlayoffPair[] playoffPairs;

    public Playoff(List<String> players, int size) {
        //if (players == null || size == 0) {
        if (size == 0) {
            throw new IllegalArgumentException("Empty playoffs attempted!");
        } else { //special cases not needed so far
            /*if (size == 6) {
                size = 8;
            } else {
                if (size == 12) {
                    size = 16;
                }
            }*/
        }

        this.players = players;
        this.size = size;
        //this.mother = tournament;
        this.emptyPlayoffs = true;
        this.playoffPairs = createPairs(players, size);

    }

//    public Playoff(Tournament tournament, List players) {
//        this(tournament, players, players.size());
//    }
    public Playoff(BufferedReader bufferedreader) throws FileFormatException, IOException {
        try {
            this.size = Tools.parseIntAfter("PLAYOFF-SIZE:", bufferedreader.readLine());
            this.playoffPairs = new PlayoffPair[this.size / 2];
            for (int i = 0; i < this.size / 2; i++) {
                this.playoffPairs[i] = new PlayoffPair(bufferedreader);
            }
            if (!bufferedreader.readLine().equals("END-OF-PLAYOFF")) {
                throw new FileFormatException();
            }
        } catch (FileFormatException fileformatexception) {
            throw fileformatexception;
        } catch (IOException ioexception) {
            throw ioexception;
        }

    }

    /**
     * Adds metadata to playoffpairs about the players groupstandings
     * how expensive is this..not a problem with 100 players
     * @param groupStandings
     */
    void markRankings(ArrayList<String> groupStandings) {
        for (int i = 0; i < this.playoffPairs.length; i++) {
            this.playoffPairs[i].setHomePlacement(groupStandings.indexOf(this.playoffPairs[i].getHomeTeam()) + 1);
            this.playoffPairs[i].setAwayPlacement(groupStandings.indexOf(this.playoffPairs[i].getAwayTeam()) + 1);
        }
    }

    //won't support empty players
    private PlayoffPair[] createPairs(List<String> players, int size) {

        PlayoffPair[] pairs = new PlayoffPair[size / 2];
        for (int i = 0; i < size / 2; i++) {
            String player1 = null;
            String player2 = null;

            if (!players.isEmpty()) {
                player1 = (String) players.remove(0);
                if (player1 == null) {
                    player1 = "X";
                } else {
                    this.emptyPlayoffs = false;
                }
            }

            if (!players.isEmpty()) {
                player2 = (String) players.remove(0);
                if (player2 == null) {
                    player2 = "X";
                } else {
                    this.emptyPlayoffs = false;
                }
            }

            pairs[i] = new PlayoffPair(this, player1, player2);
        }

        return pairs;
    }

    public boolean isEmptyPlayoffs() {
        return this.emptyPlayoffs;
    }

    public boolean isFinished() {
        boolean finished = true;
        for (int i = 0; i < playoffPairs.length; i++) {
            if(Integer.parseInt(playoffPairs[i].getHomeWins()) == Integer.parseInt(playoffPairs[i].getAwayWins())) {
                finished = false;
            }            
        }

        return finished;
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

    public ArrayList<String> getSurvivors() {
        ArrayList<String> survivors = new ArrayList<String>();
        for (int i = 0; i < playoffPairs.length; i++) {
            survivors.add(playoffPairs[i].getWinner());
        }

        return survivors;
    }

    public ArrayList<String> getLosers() {
        ArrayList<PlayoffPair> pairs = new ArrayList<PlayoffPair>();
        ArrayList<String> losers = new ArrayList<String>();
        for (int i = 0; i < playoffPairs.length; i++) {
            pairs.add(playoffPairs[i]);
        }
        //must be ordered like basic group
        Collections.sort(pairs, new PlayoffPlacementSort());
        for (PlayoffPair object : pairs) {
            losers.add(((PlayoffPair) object).getLoser());
        }

        return losers;
    }

    public PlayoffPair[] getPlayoffPairs() {
        return playoffPairs;
    }

    //tnmt-file writing
    public void save(PrintWriter printwriter) {
        printwriter.println("PLAYOFF-SIZE:" + this.size);
        for (int i = 0; i < this.playoffPairs.length; i++) {
            printwriter.println("PLAYOFFPAIR");
            this.playoffPairs[i].save(printwriter);
            printwriter.println("END-OF-PLAYOFFPAIR");
        }
        printwriter.println("END-OF-PLAYOFF");
    }

    //html-file content
    public String saveAll() {
        StringBuilder output = new StringBuilder();
        output.append("<p class=\"playoff\">").append(System.getProperty("line.separator"));
        //printwriter.println("PLAYOFF-SIZE:" + this.size);
        for (int i = 0; i < this.playoffPairs.length; i++) {
            //printwriter.println("PLAYOFFPAIR");
            output.append(this.playoffPairs[i].saveAll());
        //printwriter.println("END-OF-PLAYOFFPAIR");
        }
        //printwriter.println("END-OF-PLAYOFF");
        output.append("</p>").append(System.getProperty("line.separator"));

        return output.toString();
    }
}
