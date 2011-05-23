/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

/**
 *
 * @author aulaskar
 */
public class PreviousGroupCopier {

    public String copyResultsFromPreviousGroup(String[] previousTnmt, String[] newTnmt) {
        StringBuffer mixedTnmt = new StringBuffer();
        String lineToReturn = null;

        //reads empty group first and searches found names for results in previous group
        for (String line : newTnmt) {
            lineToReturn = new String(line);
            String[] foundPair = line.split(":");
            if (foundPair.length == 2) { //players without result
                String player1 = foundPair[0];
                String player2 = foundPair[1];

                for (String line2 : previousTnmt) {
                    String[] foundResult = line2.split(":");
                    if (foundResult.length == 4) { //players with results but no postfix
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
            mixedTnmt.append(lineToReturn);
        }

        return mixedTnmt.toString();
    }
}
