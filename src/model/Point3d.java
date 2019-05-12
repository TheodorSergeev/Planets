package model;

public class Point3d {

    private double[] coord;
    
    Point3d() {
        coord = new double[3];
        coord[0] = 0.0;
        coord[1] = 0.0;
        coord[2] = 0.0;

    }
    
    Point3d(double x_, double y_, double z_) {
        coord = new double[3];
        coord[0] = x_;
        coord[1] = y_;
        coord[2] = z_;
    }
    
    Point3d(Point3d p) {
        coord[0] = p.getX();
        coord[1] = p.getY();
        coord[2] = p.getZ();
    }
    
    double getX() {
        return coord[0];
    }
    
    double getY() {
        return coord[1];
    }
    
    double getZ() {
        return coord[2];
    }
    
    void add(Point3d addit) {
        coord[0] += addit.getX();
        coord[1] += addit.getY();
        coord[2] += addit.getZ();
    }
    
    
    Point3d diff(Point3d diff_point) {
        Point3d new_point = new Point3d(coord[0] - diff_point.getX(), 
                                        coord[1] - diff_point.getY(), 
                                        coord[2] - diff_point.getZ());
        return new_point;
    }
    
    Point3d mult(double c) {
        Point3d new_point = new Point3d(coord[0] * c, 
                                        coord[1] * c, 
                                        coord[2] * c);
        return new_point;
    }
    
    void normalize() {
        double len = length();
        for(int i = 0; i < 3; ++i) {    
            coord[i] /= len;
        }
    }
    
    double length() {
        double dist = 0.0;
        
        for(int i = 0; i < 3; ++i) {
            
            dist += Math.pow(coord[i], 2.0);
            
        }
        return Math.pow(dist, 0.5);
    }
    
}
