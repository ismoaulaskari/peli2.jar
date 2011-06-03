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

    public String copyResultsFromPreviousGroup(List<String> previousTnmt, List<String> newTnmt) {
        StringBuffer mixedTnmt = new StringBuffer();
        String lineToReturn = null;
        int linesNotRead = newTnmt.size();

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
                    if (foundResult.length == 4 || (foundResult.length == 5 && foundResult[4].equalsIgnoreCase("pg"))) { //players with results            
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
                }
            }
            if (linesNotRead > 0) { //last line in tnmt must not be empty
                lineToReturn = lineToReturn + System.getProperty("line.separator");
            }
            mixedTnmt.append(lineToReturn);
        }

        return mixedTnmt.toString();
    }

}
