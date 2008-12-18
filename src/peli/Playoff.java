/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
    //private Tournament mother;
    private PlayoffPair[] playoffPairs;

    public Playoff(List players, int size) {
        //if (players == null || size == 0) {
        if (size == 0) {
            throw new IllegalArgumentException("Empty playoffs attempted!");
        }
        this.players = players;
        this.size = size;
        //this.mother = tournament;
        this.playoffPairs = createPairs(players, size);
    }

//    public Playoff(Tournament tournament, List players) {
//        this(tournament, players, players.size());
//    }
    public Playoff(BufferedReader bufferedreader) throws FileFormatException, IOException {
        try {
            this.size = Tools.parseIntAfter("PLAYOFF-SIZE:", bufferedreader.readLine());
            this.playoffPairs = new PlayoffPair[this.size/2];
            for (int i = 0; i < this.size/2; i++) {
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
    //won't support empty players
    private PlayoffPair[] createPairs(List players, int size) {
//        if (size > players.size()) {
//            size = players.size();
//        }
        PlayoffPair[] pairs = new PlayoffPair[size / 2];
        for (int i = 0; i < size / 2; i++) {
            String player1, player2;

            if (!players.isEmpty()) {
                player1 = (String) players.remove(0);
            } else {
                player1 = "X";
            }
            if (!players.isEmpty()) {
                player2 = (String) players.remove(0);
            } else {
                player2 = "X";
            }

            pairs[i] = new PlayoffPair(this, player1, player2);
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

    public ArrayList getSurvivors() {
        ArrayList survivors = new ArrayList();
        for (int i = 0; i < playoffPairs.length; i++) {
            survivors.add(playoffPairs[i].getWinner());
        }

        return survivors;
    }

    public List getLosers() {
        ArrayList losers = new ArrayList();
        for (int i = 0; i < playoffPairs.length; i++) {
            losers.add(playoffPairs[i].getWinner());
        }

        return losers;
    }

    public PlayoffPair[] getPlayoffPairs() {
        return playoffPairs;
    }
    //tnmt-file division writing
    public void save(PrintWriter printwriter) {
        printwriter.println("PLAYOFF-SIZE:" + this.size);
        for (int i = 0; i < this.playoffPairs.length; i++) {
            printwriter.println("PLAYOFFPAIR");
            this.playoffPairs[i].save(printwriter);
            printwriter.println("END-OF-PLAYOFFPAIR");
        }
        printwriter.println("END-OF-PLAYOFF");
    }
}
