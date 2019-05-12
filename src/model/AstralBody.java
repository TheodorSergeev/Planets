package model;

public class AstralBody {
 
    private boolean is_fixed; // if true, planet doesn't move
    private double mass, radius;
    private Point3d coord, speed, accel;
    
    AstralBody(double m, double R, 
               double x_, double y_, 
               double vx_, double vy_, 
               boolean is_fixed_) {
        if(m <= 0) {
            // exception
        }
        if(R <= 0) {
            // exception
        }
        
        is_fixed = is_fixed_;
        mass = m;
        radius = R;
        
        coord = new Point3d(x_,  y_,  0.0); 
        speed = new Point3d(vx_, vy_, 0.0); 
        accel = new Point3d(0.0, 0.0, 0.0); 
    
    }

    
    public double getM() {
        return mass;
    }
    
    public double getR() {
        return radius;
    }
    
    public Point3d getCoord() {
        return coord;
    }
    
    public double getRadius() {
        return radius;
    }
    
    public double getLeftUpX() {
        return coord.getX() - radius / 2;
    }
    
    public double getLeftUpY() {
        return coord.getY() - radius / 2;
    }
    
    public Point3d distance(AstralBody body) {
        Point3d dist = coord.diff(body.getCoord());
        return dist;
    }
    
    public Point3d compGravAcc(AstralBody body) {
        Point3d dist = distance(body);
        double len = dist.length();

        dist.normalize();
        // g m M \vec{r} / r^3

        double G = 1.0;//6.6740831 * Math.pow(10, -11);
        double c = - G * body.getM() / Math.pow(len, 2.0);
        //System.out.println("c = " + c); 
        //System.out.println("len = " + len); 
        return dist.mult(c);
    }
    
    public void addAcceleration(Point3d da) {
        //System.out.println("da = " + da.getX() + " " + da.getY()); 
        accel.add(da);
        //System.out.println("accel = " + accel.getX() + " " + accel.getY()); 
   
    }
    
    public void move(double dt) {
        if(!is_fixed) {
            // x = x_0 + vt + at^2/2
            Point3d vt = speed.mult(dt);

            //System.out.println("vt = " + vt.getX() + " " + vt.getY()); 
            //System.out.println("at = " + accel.getX() + " " + accel.getY()); 

            coord.add(vt);
            coord.add(accel.mult(dt * dt / 2));

            // v = v_0 + at
            speed.add(accel.mult(dt));

            // a = 0
            accel = accel.mult(0.0);
        }
    }
    
}
