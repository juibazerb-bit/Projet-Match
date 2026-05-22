/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package Plateau;

import LogiqueJeu.GestionIA;
import Modele.Plateau;
import Modele.Coord;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author aguenich
 */
public class GestionIATest {
    
    public GestionIATest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of listMatchs method, of class GestionIA.
     */
    @Test
    public void testListMatchs() {
        System.out.println("listMatchs");
        Plateau plateau = null;
        GestionIA instance = new GestionIA();
        String expResult = "";
        String result = instance.listMatchs(plateau);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listEchange method, of class GestionIA.
     */
    @Test
    public void testListEchange() {
        System.out.println("listEchange");
        Plateau plateau = null;
        GestionIA instance = new GestionIA();
        ArrayList<Coord> expResult = null;
        ArrayList<Coord> result = instance.listEchange(plateau);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of aideOrdi method, of class GestionIA.
     */
    @Test
    public void testAideOrdi() {
        System.out.println("aideOrdi");
        Plateau plateau = null;
        int nbCoups = 0;
        GestionIA instance = new GestionIA();
        ArrayList<Coord> expResult = null;
        ArrayList<Coord> result = instance.aideOrdi(plateau, nbCoups);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of aideNCoups method, of class GestionIA.
     */
    @Test
    public void testAideNCoups() {
        System.out.println("aideNCoups");
        int N = 0;
        GestionIA instance = new GestionIA();
        instance.aideNCoups(N);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
