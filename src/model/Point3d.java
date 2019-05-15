package model;

import java.io.Serializable;

public class Point3d implements Serializable {

    private double[] coord;
    
    public Point3d() {
        coord = new double[3];
        coord[0] = 0.0;
        coord[1] = 0.0;
        coord[2] = 0.0;

    }
    
    public Point3d(double x_, double y_, double z_) {
        coord = new double[3];
        coord[0] = x_;
        coord[1] = y_;
        coord[2] = z_;
    }
    
    public Point3d(Point3d p) {
        coord[0] = p.getX();
        coord[1] = p.getY();
        coord[2] = p.getZ();
    }
    
    public double getX() {
        return coord[0];
    }
    
    public double getY() {
        return coord[1];
    }
    
    public double getZ() {
        return coord[2];
    }
    
    public void add(Point3d addit) {
        coord[0] += addit.getX();
        coord[1] += addit.getY();
        coord[2] += addit.getZ();
    }
    
    
    public Point3d diff(Point3d diff_point) {
        Point3d new_point = new Point3d(coord[0] - diff_point.getX(), 
                                        coord[1] - diff_point.getY(), 
                                        coord[2] - diff_point.getZ());
        return new_point;
    }
    
    public Point3d mult(double c) {
        Point3d new_point = new Point3d(coord[0] * c, 
                                        coord[1] * c, 
                                        coord[2] * c);
        return new_point;
    }
    
    public void normalize() {
        double len = length();
        for(int i = 0; i < 3; ++i) {    
            coord[i] /= len;
        }
    }
    
    public double length() {
        double dist = 0.0;
        
        for(int i = 0; i < 3; ++i) {
            
            dist += Math.pow(coord[i], 2.0);
            
        }
        return Math.pow(dist, 0.5);
    }
    
}
