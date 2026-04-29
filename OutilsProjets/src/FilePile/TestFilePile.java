/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FilePile;

/**
 *
 * @author guillaume.laurent
 */
public class TestFilePile {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        File<String> file = new File<String>();

        file.push("Hello");
        file.push("world");

        String motFile = file.pop(); // récupération et suppression de "Hello"

        System.out.println(motFile);
        
        Pile<String> pile = new Pile<String>();

        pile.push("Hello ");
        pile.push("world");

        String motPile = pile.pop(); // récupération et suppression de "world"

        System.out.println(motPile);
 
    }

}
