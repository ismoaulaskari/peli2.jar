/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.util.List;

/**
 *
 * @author aulaskar
 */
public class PreviousGroupCopier {

    /**
     * evil hack that changes the tnmt
     * @param previousTnmt
     * @param newTnmt
     * @param playersInFinalGroup
     * @return
     */
    public String copyResultsFromPreviousGroup(List<String> previousTnmt, List<String> newTnmt, int playersInFinalGroup) {
        StringBuilder mixedTnmt = new StringBuilder();
        String lineToReturn = null;
        int linesNotRead = newTnmt.size();
        StringBuilder pgLines = new StringBuilder();
        int maxRounds = playersInFinalGroup / 2;

        boolean firstRun = true;
        boolean stopPrinting = false;
        //reads empty group first and searches found names for results in previous group
        for (String line : newTnmt) {
            linesNotRead--;
            lineToReturn = new String(line);
            String[] foundPair = line.split(":");
            if (foundPair.length == 2) { //players without result
                String player1 = foundPair[0];
                String player2 = foundPair[1];
                if (player1.equalsIgnoreCase("ROUND")) {
                    int round = 0;
                    try {
                        round = Integer.parseInt(player2);
                    } catch (NumberFormatException ne) {
                        //player named round
                        round = 0;
                    } finally {
                        if (round > 0 && round > maxRounds) {
                            stopPrinting = true;
                        }
                    }
                }
                if (player1.equalsIgnoreCase("ROUNDS")) {
                    int rounds = 0;
                    try {
                        rounds = Integer.parseInt(player2);
                    } catch (NumberFormatException ne) {
                        //player named rounds
                        rounds = 0;
                    } finally {
                        if(rounds > 0) {
                            lineToReturn = "ROUNDS:" + (maxRounds + 1);
                        }
                    }
                }


                for (String line2 : previousTnmt) {
                    String[] foundResult = line2.split(":");

                    if (foundResult.length == 4) { //players with results
                        if (foundResult[0].equalsIgnoreCase(player1)) {
                            if (foundResult[1].equalsIgnoreCase(player2)) {
                                lineToReturn = player1 + ":" + player2 + ":" + foundResult[2] + ":" + foundResult[3] + ":" + "pg";
                            }
                        } else {
                            if (foundResult[1].equalsIgnoreCase(player1)) {
                                if (foundResult[0].equalsIgnoreCase(player2)) {
                                    lineToReturn = player1 + ":" + player2 + ":" + foundResult[3] + ":" + foundResult[2] + ":" + "pg";
                                }
                            }
                        }
                    }

                    if (foundResult.length == 5) { //players with results with postfix
                        if (foundResult[4].equalsIgnoreCase("pg") && firstRun) {
                            pgLines.append(line2).append(System.getProperty("line.separator"));
                        }
                    }
                }
                firstRun = false;
            }
            if (linesNotRead > 0) { //last line in tnmt must not be empty
                lineToReturn = lineToReturn + System.getProperty("line.separator");
            } else {
                mixedTnmt.append("ROUND:" + (maxRounds + 1) + System.getProperty("line.separator"));
                mixedTnmt.append("(X)" + System.getProperty("line.separator"));
                mixedTnmt.append(pgLines);
                mixedTnmt.append("END-OF-ROUND" + System.getProperty("line.separator"));
                mixedTnmt.append("END-OF-DIVISION" + System.getProperty("line.separator"));
            }
            if (!stopPrinting) {
                mixedTnmt.append(lineToReturn);
            }
        }

        return mixedTnmt.toString();
    }

    public String copyResultsFromPreviousGroup(List<String> previousTnmt, List<String> newTnmt) {
        StringBuilder mixedTnmt = new StringBuilder();
        String lineToReturn = null;
        int linesNotRead = newTnmt.size();
        StringBuilder pgLines = new StringBuilder();

        boolean firstRun = true;
        //reads empty group first and searches found names for results in previous group
        for (String line : newTnmt) {
            linesNotRead--;
            lineToReturn = new String(line);
            String[] foundPair = line.split(":");
            if (foundPair.length == 2) { //players without result
                String player1 = foundPair[0];
                String player2 = foundPair[1];

                for (String line2 : previousTnmt) {
                    String[] foundResult = line2.split(":");
                    if (foundResult.length == 4) { //players with results
                        if (foundResult[0].equalsIgnoreCase(player1)) {
                            if (foundResult[1].equalsIgnoreCase(player2)) {
                                lineToReturn = player1 + ":" + player2 + ":" + foundResult[2] + ":" + foundResult[3] + ":" + "pg";
                            }
                        } else {
                            if (foundResult[1].equalsIgnoreCase(player1)) {
                                if (foundResult[0].equalsIgnoreCase(player2)) {
                                    lineToReturn = player1 + ":" + player2 + ":" + foundResult[3] + ":" + foundResult[2] + ":" + "pg";
                                }
                            }
                        }
                    }

                    if (foundResult.length == 5) { //players with results with postfix
                        if (foundResult[4].equalsIgnoreCase("pg") && firstRun) {
                            pgLines.append(line2).append(System.getProperty("line.separator"));
                        }
                    }
                }
                firstRun = false;
            }
            if (linesNotRead > 0) { //last line in tnmt must not be empty                
                lineToReturn = lineToReturn + System.getProperty("line.separator");
            } else {
                mixedTnmt.append(pgLines);
            }
            mixedTnmt.append(lineToReturn);
        }

        return mixedTnmt.toString();
    }
}
