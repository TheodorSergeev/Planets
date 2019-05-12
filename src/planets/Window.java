package planets; //classes in package planets == controller in MVC model

import model.PlanetarySystem;
import view.PlanetPainter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public class Window extends Frame implements WindowListener {
    
    private PlanetPainter pl_painter;  // canvas that paints planets
    private Dimension dim;             // window dimension
    
    private boolean is_sim_running;    // flags if the simulation is running
    Button button_play_pause;          // pauses/resumes simulation
    Button button_save;                // saves current simulation state
    
    class PausePlayButListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(button_play_pause == null) {
                throw new IllegalArgumentException("button_play_pause is null");
            }
            
            if(is_sim_running) {
                button_play_pause.setLabel("Resume");
                is_sim_running = false;
                pl_painter.stopTimers();
            } else {
                button_play_pause.setLabel("Pause");
                is_sim_running = true;
                pl_painter.restartTimers();
            }
        }
    };

    public Window(int width, int height, PlanetarySystem pl_syst_,
                                         PlanetPainter   pl_paint_) {

        is_sim_running = true;
        
        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Window dimensions must be greater than zero.");
        }
        if(pl_syst_ == null) {
            throw new IllegalArgumentException("PlanetarySystem pl_syst_ = null");
        }if(pl_paint_ == null) {
            throw new IllegalArgumentException("PlanetPainter pl_paint_ = null");
        }
        
        dim = new Dimension(width, height);
     
        pl_painter = pl_paint_;// 
	pl_painter.setBounds(0, 0, 600, 500);
        add(pl_painter);

        button_play_pause = new Button("Pause");  
        button_play_pause.setBounds(dim.width - 80, 20, 60, 20); 
        button_play_pause.addActionListener(new PausePlayButListener());      
        add(button_play_pause);
        
        button_save = new Button("Save");
        button_save.setBounds(dim.width - 80, 50, 60, 20); 
        //button_save.addActionListener(new PausePlayButListener());      
        add(button_save);

        setSize(dim.width, dim.height);
        setLayout(null);
        setVisible(true);
        setResizable(false);
    
        addWindowListener(this);       
    
    }
    
    @Override
    public void windowClosing(WindowEvent arg0) {  
        System.out.println("windowClosing");  
        pl_painter.stopTimers();
        dispose();
    }  

    @Override
    public void windowOpened(WindowEvent e) {

        System.out.println("windowOpened");  
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("windowClosed");  
    }

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("windowIconified");  
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("windowDeiconified");  
    }

    @Override
    public void windowActivated(WindowEvent e) {
        System.out.println("windowActivated");  
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        System.out.println("windowDeactivated");  
    }
    
}
