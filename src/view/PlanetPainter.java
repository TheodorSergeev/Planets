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
import view.Window;

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
    
    private ArrayList <Color> palette;
    private double paint_pause;
    
    private boolean to_calculate;
    
    public void addPlanetColor(Color new_col) {
        if(new_col == null) {
            throw new IllegalArgumentException("Color new_col == null");
        }
        palette.add(new_col);
    }
    
    public Color getPlanetColor(int pl_num) {
        if(pl_num < 0 || pl_num > palette.size()) {
            throw new IllegalArgumentException("No planet with number " + 
                                               pl_num +  "exists");
        }
        return palette.get(pl_num);
    }

    protected void stopTimers() {
        if(timer_calc == null) {
            throw new IllegalArgumentException("Can't close a null timer timer_calc");
        }
        if(timer_paint == null) {
            throw new IllegalArgumentException("Can't close a null timer timer_paint");
        }

        timer_calc.cancel();
        timer_paint.cancel();
        
        timer_calc = null;
        timer_paint = null;
    }

    protected void restartTimers() {
        timer_calc  = new Timer();
        timer_paint = new Timer();
        timer_calc.scheduleAtFixedRate (new ScheduleCalcTask(),  init_delay, interval_calc);        
        timer_paint.scheduleAtFixedRate(new SchedulePaintTask(), init_delay, interval_paint);             
    }
    
    final protected void startTimers(int init_delay_, int int_paint_, int int_calc_) {
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
    
    final protected void initializeCanvas(int width, int height, 
                                  PlanetarySystem pl_syst_, double paint_pause_) {
        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Window dimensions must be greater than zero.");
        }
        if(paint_pause_ < 0) {
            throw new IllegalArgumentException("paint_pause_ < 0.");
        }
        if(pl_syst_ == null) {
            throw new IllegalArgumentException("pl_syst_ is null.");
        }
        
        paint_pause = paint_pause_;
        pl_syst = pl_syst_;

        dim = new Dimension(width, height);
        palette = new ArrayList<>();

        offscreen = null;
        offgc = null;
    }

    public PlanetPainter(int width, int height, 
                         PlanetarySystem pl_syst_, double paint_pause_,
                         int init_delay_, int int_paint_, int int_calc_) {

        to_calculate = true;
        initializeCanvas(width, height, pl_syst_, paint_pause_);
        startTimers(init_delay_, int_paint_, int_calc_);
                
    }

    public PlanetPainter(int width, int height, 
                         PlanetarySystem pl_syst_, double paint_pause_,
                         int init_delay_, int int_paint_) {

        to_calculate = false;
        initializeCanvas(width, height, pl_syst_, paint_pause_);
        startTimers(init_delay_, int_paint_, int_paint_);
                
    }

    
    protected class SchedulePaintTask extends TimerTask {
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
  
    protected class ScheduleCalcTask extends TimerTask {
        @Override
        public void run() {
            if(to_calculate) {
                pl_syst.iteration();
            } else {
                //System.out.println("I don't calculate");
            }
        }
    }        

    protected Image createOffscreen() {
        offscreen = createImage(dim.width, dim.height);
        offgc = (Graphics2D) offscreen.getGraphics();

        offgc.setColor(Color.WHITE);//getForeground());
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
        
        return offscreen;
    }
    
    @Override
    public void update(Graphics g) {
        
        offscreen = createOffscreen();
        g.drawImage(offscreen, 0, 0, this);
        
    }
    
    public void close() {
        stopTimers();
        init_delay     = -123;
        interval_paint = -123;
        interval_calc  = -123;
    }
    
    public void pause() {
        stopTimers();
    }

    public void resume() {
        restartTimers();
    }
    

}
