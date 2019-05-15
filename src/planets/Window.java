package planets; //classes in package planets == controller in MVC model

import model.PlanetarySystem;
import view.PlanetPainter;

import java.awt.*;
import static java.awt.FileDialog.SAVE;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public class Window extends Frame implements WindowListener {
    
    private PlanetPainter pl_painter;  // canvas that paints planets
    private Dimension dim;             // window dimension
    private WebCommunicator web_comm;
    
    private SimState state;
    
    public enum SimState {
        RUNNING,
        PAUSED,
        CLOSED
    }

    public SimState getSimState() {
        return state;
    }
    
    Button button_play_pause;          // pauses/resumes simulation
    Button button_save;                // saves current simulation state
    
    private class PausePlayButListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(button_play_pause == null) {
                throw new IllegalArgumentException("button_play_pause is null");
            }
                        
            if(state == null) {
                throw new IllegalArgumentException("state is null");
            } else switch (state) {
                case RUNNING:
                    pauseSimulation();
                    return;
                case PAUSED:
                    resumeSimulation();
                    return;
                default:
                    throw new IllegalArgumentException("app is already closed");
            }
        }
    };
    
    private String saveFile() {
        FileDialog chooser = new FileDialog(this, "Save as ...", SAVE);
        
        chooser.setFile("*.properties");
        chooser.setDirectory(".");
        chooser.setVisible(true);
        
        String fname = chooser.getFile();

        if(fname != null) {
            System.out.println(fname);
        }

        return fname;  
    }
    
    private class SaveButListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(button_save == null) {
                throw new IllegalArgumentException("button_save is null");
            }

            saveFile();
            
        }
    };

    private void frameInit(int width, int height, PlanetarySystem pl_syst_,
                                         PlanetPainter   pl_paint_)
    {
        
        state = SimState.RUNNING;

        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Window dimensions must be greater than zero.");
        }
        if(pl_syst_ == null) {
            throw new IllegalArgumentException("PlanetarySystem pl_syst_ = null");
        }if(pl_paint_ == null) {
            throw new IllegalArgumentException("PlanetPainter pl_paint_ = null");
        }
        
        dim = new Dimension(width, height);
     
        pl_painter = pl_paint_;
	pl_painter.setBounds(0, 0, 600, 500);
        add(pl_painter);

        button_play_pause = new Button("Pause");  
        button_play_pause.setBounds(dim.width - 80, 20, 60, 20); 
        button_play_pause.addActionListener(new PausePlayButListener());      
        add(button_play_pause);
        
        button_save = new Button("Save");
        button_save.setBounds(dim.width - 80, 50, 60, 20); 
        button_save.addActionListener(new SaveButListener());      
        add(button_save);

        setSize(dim.width, dim.height);
        setLayout(null);
        setResizable(false);
    
        addWindowListener(this);
        
    }

    public Window(int width, int height, PlanetarySystem pl_syst_,
                                         PlanetPainter   pl_paint_) {
        
        frameInit(width, height, pl_syst_, pl_paint_);
    
    }
    
    public void start() {
        setVisible(true);
    }

    public void addWebComm(WebCommunicator web_comm_) {
        state = SimState.RUNNING;

        if(web_comm_ == null) {
            throw new IllegalArgumentException("web_comm_ = null");
        }
        web_comm = web_comm_;
        web_comm.setState(state);

    }

    
    
    public void pauseSimulation() {
        button_play_pause.setLabel("Resume");
        state = SimState.PAUSED;
        web_comm.setState(SimState.PAUSED);
        pl_painter.pause();
    }
    
    public void resumeSimulation() {
        button_play_pause.setLabel("Pause");
        state = SimState.RUNNING;
        try {
            web_comm.setState(SimState.RUNNING);
        } catch(IllegalArgumentException err) {
            System.out.println(err.toString());
        }
        pl_painter.resume();
    }
    
    
    public void closeWindow() {
        System.out.println("windowClosing");  
        state = SimState.CLOSED;
        web_comm.setState(SimState.CLOSED);
        
        if(web_comm != null) {
            web_comm.closeConnection();
        }
        
        web_comm = null;

        pl_painter.close();
        pl_painter = null;
        System.out.println("windowClosed");  
        
    }


    @Override
    public void windowClosing(WindowEvent e) {   
        System.out.println("close event");

        if(state != SimState.CLOSED) {
            closeWindow();
        }
        System.out.println("dispose");  
        dispose();
        System.out.println("close event finished");
       
    }  

    @Override
    public void windowOpened(WindowEvent e) {
        //ystem.out.println("windowOpened");  
    }

    @Override
    public void windowClosed(WindowEvent e) {
        //System.out.println("windowClosed");  
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //System.out.println("windowIconified");  
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //System.out.println("windowDeiconified");  
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //System.out.println("windowActivated");  
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //System.out.println("windowDeactivated");  
    }
    
}
