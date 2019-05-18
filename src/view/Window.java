package view; //classes in package planets == controller in MVC model

import controller.MainWindowListener;
import model.PlanetarySystem;

import java.awt.*;
import static java.awt.FileDialog.SAVE;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import controller.PausePlayButListener;
import controller.UnfixPlanetButListener;
import controller.WebCommunicator;


public class Window extends Frame {
    
    private PlanetarySystem pl_syst;   // class containing list of planets
    private PlanetPainter pl_painter;  // canvas that paints planets
    private Dimension dim;             // window dimension
    private WebCommunicator web_comm;  // web communicator
    
    private SimState state;            // simulation state
    
    public SimState getSimState() {
        return state;
    }
    
    Button button_play_pause;         // pauses/resumes simulation
    Button button_save;               // saves current simulation state
    ArrayList <Button> button_unfix;  // fix/unfix planet positions
    
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
                                         PlanetPainter   pl_paint_) {     
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
     
        pl_syst = pl_syst_;
        pl_painter = pl_paint_;
	pl_painter.setBounds(0, 0, width - 100, height);
        add(pl_painter);
     
        button_play_pause = new Button("Pause");  
        PausePlayButListener but_list = new PausePlayButListener(this);
        button_play_pause.setBounds(dim.width - 80, 20, 60, 20); 
        button_play_pause.addActionListener(but_list);      
        add(button_play_pause);
        
        //button_save = new Button("Save");
        //button_save.setBounds(dim.width - 80, 50, 60, 20); 
        //button_save.addActionListener(new SaveButListener());      
        //add(button_save); //todo
        
        setSize(dim.width, dim.height);
        setLayout(null);
        setResizable(false);
        
        MainWindowListener win_list = new MainWindowListener(this);
        addWindowListener(win_list);
    }
    
    private void addUnfixButtons() {
        
        if(pl_syst == null) {
            throw new IllegalArgumentException("failed addUnfixButtons - pl_syst = null");
        }
        
        button_unfix = new ArrayList<>();
        
        int size = pl_syst.size();
        String name;
        String fix = "Fix ";
        String unfix = "Unfix ";
        
        for(int i = 0; i < size; ++i) {
            
            if(pl_syst.getFixed(i) == false) {
                name = fix + i;
            } else {
                name = unfix + i;
            }
                        
            Button new_but = new Button(name);
            
            if(new_but == null) {
                throw new IllegalArgumentException("failed to create new fix/unfix button");
            }
            
            button_unfix.add(new_but);
            
            new_but.setBackground(pl_painter.getPlanetColor(i));

            UnfixPlanetButListener but_unf_list = new UnfixPlanetButListener(this, pl_syst, i);
            new_but.setBounds(dim.width - 80, 50 + 30 * (i + 1), 60, 20); 
            new_but.addActionListener(but_unf_list);      
            add(new_but);  

        }
        
    }
    
    public void redrawButtons() {
        
        int size = pl_syst.size();

        for(int i = 0; i < size; ++i) {
            
            if(pl_syst.getFixed(i) == true) {
                button_unfix.get(i).setLabel("Unfix " + i);
            }
            else {
                button_unfix.get(i).setLabel("Fix " + i);
            }
            
        }
        
    }
    
    public Window(int width, int height, PlanetarySystem pl_syst_,
                                         PlanetPainter   pl_paint_) {
        
        frameInit(width, height, pl_syst_, pl_paint_);
        addUnfixButtons();

    }
    
    public Window(int width, int height, PlanetarySystem pl_syst_,
                                         PlanetPainter   pl_paint_,
                                         WebCommunicator web_comm_) {
               
        frameInit(width, height, pl_syst_, pl_paint_);
        
        state = SimState.RUNNING;

        if(web_comm_ == null) {
            throw new IllegalArgumentException("web_comm_ = null");
        }
        web_comm = web_comm_;
        
        if(web_comm.type() == WebCommunicator.WebType.SERVER) {
            addUnfixButtons();
        }

        web_comm.setWindow(this);                
        web_comm.setState(state);
        
    }
    
    public void start() {
        setVisible(true);
    }
    
    public void pauseSimulation() {
        button_play_pause.setLabel("Resume");
        state = SimState.PAUSED;
        if(web_comm != null) {
            web_comm.setState(SimState.PAUSED);
        }
        pl_painter.pause();
    }
    
    public void resumeSimulation() {
        button_play_pause.setLabel("Pause");
        state = SimState.RUNNING;
        if(web_comm != null) {
            try {
                web_comm.setState(SimState.RUNNING);
            } catch(IllegalArgumentException err) {
                System.out.println(err.toString());
            }
        }
        pl_painter.resume();
    }
    
    
    public void closeWindow() {
        
        System.out.println("windowClosing"); 
        
        if(state == SimState.RUNNING) {
            pl_painter.close();
        }
        
        state = SimState.CLOSED;
        
        if(web_comm != null) {
            web_comm.setState(SimState.CLOSED);
            web_comm.closeConnection();
        }
        
        pl_painter = null;
        web_comm = null;
        
        dispose();
        System.out.println("windowClosed");  
        
    }
    
}
