/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package planets;

import model.PlanetarySystem;
/**
 *
 * @author root
 */
public class Planets {

    /**
     * @param args the command line arguments
     */ 
    public static void main(String[] args) {
        PlanetarySystem pl_syst = new PlanetarySystem();
        Window main_wind = new Window(700, 500, pl_syst);
        //Point3d test = new Point3d(1.0, 1.0, 0.0);
        //Point3d test2 = new Point3d(0.0, 0.0, 0.0);
    }
    
}
