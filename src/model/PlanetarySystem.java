package model;

import java.util.*; 

public class PlanetarySystem {

    private final ArrayList <AstralBody> arr;
    private final double dt;
    
    public ArrayList <Point3d> getPlanetsPositions() {
        
        ArrayList <Point3d> coords = new ArrayList<>();
        
        for(AstralBody planet : arr) {
            coords.add(planet.getCoord());
        }
        
        return coords;
        
    }
    
    public boolean getFixed(int pl_pos) {
        
        if(pl_pos < 0 || pl_pos > size()) {
            throw new IllegalArgumentException("pl_pos ou of bounds");
        }
        
        return arr.get(pl_pos).getFixed();
        
    }
    
    public void changeFixPlanet(int pl_pos) {
        
        if(pl_pos < 0 || pl_pos > size()) {
            throw new IllegalArgumentException("pl_pos ou of bounds");
        }
        
        System.out.println("Changing fixed planet");
        
        if(arr.get(pl_pos).getFixed()) {
            System.out.println("fixed? " + arr.get(pl_pos).getFixed());
            arr.get(pl_pos).unfix();
            System.out.println("fixed? " + arr.get(pl_pos).getFixed());
        }
        else {
            arr.get(pl_pos).fix();
        }
        
    }
    
    public void setPlanetsPositions(ArrayList <Point3d> new_pos) {    
        if(new_pos == null) {
            throw new IllegalArgumentException("new_pos array is null");
        }
  
        int size = arr.size();

        if(new_pos.size() != size) {
            throw new IllegalArgumentException("new_pos has size = " + new_pos.size() + " != " + size);
        }
        
        for(int i = 0; i < size; ++i) {
            arr.get(i).setPos(new_pos.get(i));
        }               
    }   
    public double dt() {
        return dt;
    }
    
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
    
    public double getPlanetX(int pos) {
        checkPos(pos);
        return arr.get(pos).getCoord().getX();   
    }
    
    
    public double getPlanetY(int pos) {
        checkPos(pos);
        return arr.get(pos).getCoord().getY();   
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
    
    public PlanetarySystem(double dt_) {       
        arr = new ArrayList<>();
        
        if(dt_ < 0) {
            throw new IllegalArgumentException("dt_ < 0 in PlanetarySystem");
        }
        
        dt = dt_;
        double G = 1.0;//6.6740831 * Math.pow(10, -11);               
    }

}
