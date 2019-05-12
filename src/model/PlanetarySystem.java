package model;

import java.util.*; 

public class PlanetarySystem {

    public final ArrayList <AstralBody> arr;
    private final double dt;
    
    public int size() {
        return arr.size();
    }
    
    public void Iteration() {
        
        for(AstralBody planet1: arr) {                
            
            for(AstralBody planet2: arr) {
                
                if(planet1 != planet2)
                    planet1.addAcceleration(planet1.compGravAcc(planet2));
                
            }
            
        }
        
        for(AstralBody planet: arr) {
            planet.move(dt);
        }
        
    }
    
    public PlanetarySystem() {
        arr = new ArrayList<>();
        dt = 0.1;
        
        // convert to enum
        double G = 1.0;//6.6740831 * Math.pow(10, -11);
        //double sun_mass   = 1.98847 * Math.pow(10, 30);
        //double earth_mass = 5.9722 * Math.pow(10, 24);
        
        AstralBody sun = new AstralBody(100.0, 10.0, 300.0, 250.0, 0.0, 0.0, true);

        double vy_0 = Math.pow(G * sun.getM() / 200.0, 0.5);
        
        AstralBody earth   = new AstralBody(5.0, 10.0, 500.0, 250.0, 0.0, vy_0, false);
        AstralBody earth2  = new AstralBody(1.0, 10.0, 450.0, 300.0, 0.0, -vy_0, false);
       
        arr.add(earth2);
        arr.add(earth);
        arr.add(sun);
        
    }
 
}
