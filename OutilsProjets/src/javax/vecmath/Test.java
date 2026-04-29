/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javax.vecmath;

/**
 *
 * @author guillaume.laurent
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Vector3d v = new Vector3d(45,67,90);
        Vector3d b = new Vector3d(1,0,-100);
        
        v.scale(2);
        
        b.add(v);
        
        
        System.out.println(b);
        
        
        
        
    }
    
}
