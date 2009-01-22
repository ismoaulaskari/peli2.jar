/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package peli;
import java.util.Comparator;
/**
 *
 * @author aulaskar
 */
public class PlayoffPlacementSort implements Comparator {

    public int compare(Object pair0, Object pair1) {
        return ((PlayoffPair) pair0).getLoserPlacement() - ((PlayoffPair) pair1).getLoserPlacement();
    }

}
