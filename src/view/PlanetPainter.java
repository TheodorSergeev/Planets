package view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.PlanetarySystem;
import planets.Window;

public class PlanetPainter extends Canvas {

    PlanetarySystem pl_syst;   // reference to what to draw

    private Dimension dim;     // window dimension
    private Image offscreen;   // offscreen image for double bufferisation
    private Graphics2D offgc;

    private Timer timer_calc;  // compute physical iterations
    private Timer timer_paint; //  update window
    
    private int init_delay;
    private int interval_paint;
    private int interval_calc;
    private double paint_pause;
    
    
    public final void stop_timers() {
        timer_calc.cancel();
        timer_paint.cancel();
    }

    public final void restart_timers() {
        timer_calc  = new Timer();
        timer_paint = new Timer();
        timer_calc.scheduleAtFixedRate (new ScheduleCalcTask(),  init_delay, interval_calc);        
        timer_paint.scheduleAtFixedRate(new SchedulePaintTask(), init_delay, interval_paint);             
    }
    
    public final void start_timers(int init_delay_, int int_paint_, int int_calc_) {
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

        restart_timers();
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
        pl_syst = pl_syst_;
        start_timers(init_delay_, int_paint_, int_calc_);
        
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
            pl_syst.Iteration();
        }
    }        

    @Override
    public void update(Graphics g) {
        
        offscreen = createImage(dim.width, dim.height);
        offgc = (Graphics2D) offscreen.getGraphics();

        offgc.setColor(Color.WHITE);//getForeground());
//        offgc.setColor(getBackground());
        offgc.fillRect(0, 0, dim.width, dim.height);

        String hexColor[] = {"0x45e5B", "0xFF1493", "0xFF1493"};
        
        int size = pl_syst.size();
        
        for(int i = 0; i < size; ++i) {
            offgc.setColor(Color.decode(hexColor[i]));

            Ellipse2D ell = new Ellipse2D.Double(pl_syst.arr.get(i).getLeftUpX(),
                                                 pl_syst.arr.get(i).getLeftUpY(), 
                                                 pl_syst.arr.get(i).getR(), 
                                                 pl_syst.arr.get(i).getR());
            
            offgc.draw(ell);
            offgc.fill(ell);
        }
     
        g.drawImage(offscreen, 0, 0, this);
        
    }

}
