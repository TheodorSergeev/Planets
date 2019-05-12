package model;

import java.util.*; 

public class PlanetarySystem {

    private final ArrayList <AstralBody> arr;
    private final double dt;
    
    public void addPlanet(double m,  double R, 
                          double x,  double y,
                          double vx, double vy, boolean is_fixed) {
        
        arr.add(new AstralBody(m, R, x, y, vx, vy, is_fixed));
        
    }
    
    public int size() {
        return arr.size();
    }
    
    private void checkPos(int pos) {
        if(pos < 0 || pos >= arr.size()) {
            throw new IllegalArgumentException("Planet number pos = " + pos + " is out of bounds");
        }
    }
    
    public double getPlanetLeftUpX(int pos) {
        checkPos(pos);
        return arr.get(pos).getLeftUpX();   
    }
 
    public double getPlanetLeftUpY(int pos) {
        checkPos(pos);
        return arr.get(pos).getLeftUpY();   
    }

    public double getPlanetRadius(int pos) {
        checkPos(pos);
        return arr.get(pos).getR();   
    }

    
    public void iteration() {
        
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
        
        //AstralBody sun = new AstralBody(100.0, 10.0, 300.0, 250.0, 0.0, 0.0, true);

        //double vy_0 = Math.pow(G * sun.getM() / 200.0, 0.5);
                
        //AstralBody earth   = new AstralBody(5.0, 10.0, 500.0, 250.0, 0.0, vy_0, false);
        //AstralBody earth2  = new AstralBody(1.0, 10.0, 450.0, 300.0, 0.0, -vy_0, false);
       
        //arr.add(earth2);
        //arr.add(earth);
        //arr.add(sun);
        
    }
 
}
