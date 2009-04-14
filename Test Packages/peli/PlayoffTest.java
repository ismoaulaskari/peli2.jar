package peli;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import peli.Playoff;
import peli.PlayoffPair;

/**
 *
 * @author aulaskar
 */
public class PlayoffTest {

    public PlayoffTest() {
    }


    @Test public void testPlayoff() {
        System.out.println("Playoff(list,size)");
        String[] players = new String[] { "Kristian Iso-Tryykäri", "Seppo Kosonen", "Anssi Järvinen", "Janne Kokko", "Konsta Jukka", "Jani Lappalainen", "Mika Myllykangas", "Erik Lindgren", "Erno Lantiainen", "Janne Ollila" };
        ArrayList playersArrayList = new ArrayList(Arrays.asList(players));
        assertEquals(playersArrayList.subList(0, 1), new Playoff(playersArrayList, 2).getPlayers());
    }

    /**
     * Test of markRankings method, of class Playoff.
     */
   /* @Test
    public void testMarkRankings() {
        System.out.println("markRankings");
        ArrayList groupStandings = null;
        Playoff instance = null;
        instance.markRankings(groupStandings);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of isEmptyPlayoffs method, of class Playoff.
     */
    @Ignore
    @Test
    public void testIsEmptyPlayoffs() {
        System.out.println("isEmptyPlayoffs");
        Playoff instance = null;
        boolean expResult = false;
        boolean result = instance.isEmptyPlayoffs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSize method, of class Playoff.
     */
    @Ignore
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        Playoff instance = null;
        int expResult = 0;
        int result = instance.getSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSize method, of class Playoff.
     */
    @Ignore
    @Test
    public void testSetSize() {
        System.out.println("setSize");
        int size = 0;
        Playoff instance = null;
        instance.setSize(size);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPlayers method, of class Playoff.
     */
    @Ignore
    @Test
    public void testGetPlayers() {
        System.out.println("getPlayers");
        Playoff instance = null;
        List expResult = null;
        List result = instance.getPlayers();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPlayers method, of class Playoff.
     */
    @Ignore
    @Test
    public void testSetPlayers() {
        System.out.println("setPlayers");
        List players = null;
        Playoff instance = null;
        instance.setPlayers(players);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSurvivors method, of class Playoff.
     */
    @Ignore
    @Test
    public void testGetSurvivors() {
        System.out.println("getSurvivors");
        Playoff instance = null;
        ArrayList expResult = null;
        ArrayList result = instance.getSurvivors();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLosers method, of class Playoff.
     */
    @Ignore
    @Test
    public void testGetLosers() {
        System.out.println("getLosers");
        Playoff instance = null;
        ArrayList expResult = null;
        ArrayList result = instance.getLosers();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPlayoffPairs method, of class Playoff.
     */
    @Ignore
    @Test
    public void testGetPlayoffPairs() {
        System.out.println("getPlayoffPairs");
        Playoff instance = null;
        PlayoffPair[] expResult = null;
        PlayoffPair[] result = instance.getPlayoffPairs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class Playoff.
     */
    @Ignore
    @Test
    public void testSave() {
        System.out.println("save");
        PrintWriter printwriter = null;
        Playoff instance = null;
        instance.save(printwriter);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveAll method, of class Playoff.
     */
    @Ignore
    @Test
    public void testSaveAll() {
        System.out.println("saveAll");
        Playoff instance = null;
        String expResult = "";
        String result = instance.saveAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}