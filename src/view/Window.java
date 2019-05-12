package view;

import model.PlanetarySystem;
import java.awt.*;
import java.awt.geom.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Window extends Frame {
    
    PlanetarySystem pl_syst;
    
    private Dimension dim;  // window dimension

    Image offscreen = null; // offscreen image for double bufferisation
    Graphics2D offgc;
    
    private Timer timer_paint; //  update window
    private Timer timer_calc;  // compute physical iterations
    private final int INIT_DELAY = 100;
    private final int INTERVAL_CALC  = 1;
    private final int INTERVAL_PAINT = 10;

    public Window(int width, int height, PlanetarySystem pl_syst_) {

        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Window dimensions must be greater than zero.");
        }
        
        dim = new Dimension(width, height);
        
        timer_paint = new Timer();
        timer_calc  = new Timer();
        timer_paint.scheduleAtFixedRate(new SchedulePaintTask(), INIT_DELAY, INTERVAL_PAINT);        
        timer_paint.scheduleAtFixedRate(new ScheduleCalcTask (), INIT_DELAY, INTERVAL_CALC);        
        
        pl_syst = pl_syst_;
        
        setSize(dim.width, dim.height);
        setLayout(null);
        setVisible(true);
    }
    
    private class SchedulePaintTask extends TimerTask {

        @Override
        public void run() {
            repaint();
            try {
                TimeUnit.SECONDS.sleep((long) 0.1);
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

        Graphics2D g2 = (Graphics2D) g;
        
        offscreen = createImage(dim.width, dim.height);
        offgc = (Graphics2D) offscreen.getGraphics();

        offgc.setColor(getBackground());
        offgc.fillRect(0, 0, dim.width, dim.height);
        offgc.setColor(getForeground());

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
