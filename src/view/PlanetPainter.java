package view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.PlanetarySystem;
import planets.Window;

public class PlanetPainter extends Canvas {

    private PlanetarySystem pl_syst;   // reference to what to draw

    private Dimension dim;     // window dimension
    private Image offscreen;   // offscreen image for double bufferisation
    private Graphics2D offgc;

    private Timer timer_calc;  // compute physical iterations
    private Timer timer_paint; //  update window
    
    private int init_delay;
    private int interval_paint;
    private int interval_calc;
    
    private final ArrayList <Color> palette;
    private double paint_pause;
    
    
    public void addPlanetColor(Color new_col) {
        if(new_col == null) {
            throw new IllegalArgumentException("Color new_col == null");
        }
        palette.add(new_col);
    }
    
    public final void stopTimers() {
        timer_calc.cancel();
        timer_paint.cancel();
    }

    public final void restartTimers() {
        timer_calc  = new Timer();
        timer_paint = new Timer();
        timer_calc.scheduleAtFixedRate (new ScheduleCalcTask(),  init_delay, interval_calc);        
        timer_paint.scheduleAtFixedRate(new SchedulePaintTask(), init_delay, interval_paint);             
    }
    
    public final void startTimers(int init_delay_, int int_paint_, int int_calc_) {
        if(init_delay_ < 0) {
            throw new IllegalArgumentException("init_delay_ < 0.");
        }
        if(int_paint_ < 0) {
            throw new IllegalArgumentException("int_paint_ < 0.");
        }
        if(int_calc_ < 0) {
            throw new IllegalArgumentException("int_calc_ < 0.");
        }
       
        init_delay     = init_delay_;
        interval_paint = int_paint_;
        interval_calc  = int_calc_;

        restartTimers();
    }
    
    public PlanetPainter(int width, int height, PlanetarySystem pl_syst_, 
                         int init_delay_, int int_paint_, int int_calc_,
                         double paint_pause_) {

        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Window dimensions must be greater than zero.");
        }
        if(paint_pause_ < 0) {
            throw new IllegalArgumentException("paint_pause_ < 0.");
        }
        
        paint_pause    = paint_pause_;
               
        dim = new Dimension(width, height);
        palette = new ArrayList<>();

        pl_syst = pl_syst_;
        startTimers(init_delay_, int_paint_, int_calc_);
        
        offscreen = null;
        offgc = null;
        
    }
    
    private class SchedulePaintTask extends TimerTask {
        @Override
        public void run() {
            repaint();
            try {
                TimeUnit.SECONDS.sleep((long) paint_pause);
            } catch (InterruptedException ex) {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
  
    private class ScheduleCalcTask extends TimerTask {
        @Override
        public void run() {
            pl_syst.iteration();
        }
    }        

    @Override
    public void update(Graphics g) {
        
        offscreen = createImage(dim.width, dim.height);
        offgc = (Graphics2D) offscreen.getGraphics();

        offgc.setColor(Color.WHITE);//getForeground());
//      offgc.setColor(getBackground());
        offgc.fillRect(0, 0, dim.width, dim.height);
        
        int size = pl_syst.size();
        
        if(palette.size() < pl_syst.size()) {
            throw new IllegalArgumentException("Palette of colors is smaller than number of planets");
        }
        
        for(int i = 0; i < size; ++i) {
            offgc.setColor(palette.get(i));

            Ellipse2D ell = new Ellipse2D.Double(pl_syst.getPlanetLeftUpX(i),
                                                 pl_syst.getPlanetLeftUpY(i), 
                                                 pl_syst.getPlanetRadius(i), 
                                                 pl_syst.getPlanetRadius(i));
            
            offgc.draw(ell);
            offgc.fill(ell);
        }
     
        g.drawImage(offscreen, 0, 0, this);
        
    }

}
