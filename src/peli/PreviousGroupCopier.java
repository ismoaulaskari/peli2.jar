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
        for (String line : previousTnmt) {
            String[] foundResult = line.split(":");
            if(foundResult.length == 4) { //regular result without postfixes
                String player1 = foundResult[0];
                String player2 = foundResult[1];
                String result1 = foundResult[2];
                String result2 = foundResult[3];

                for (String line2 : newTnmt) {
                    String[] foundPair = line2.split(":");
                    if(foundPair.length == 2) { //players without result
                        if(foundPair[0].equalsIgnoreCase(player1)) {

                        }
                    }
                }
            }
        }

        return null;
    }
}
