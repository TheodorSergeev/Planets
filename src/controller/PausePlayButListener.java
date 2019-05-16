package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.Window;
import view.SimState;

public class PausePlayButListener implements ActionListener{
    
    Window window;
    
    public PausePlayButListener(Window wind_) {
        if(wind_ == null) {
            throw new IllegalArgumentException("wind_ = null");
        }
        
        window = wind_;
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        SimState state = window.getSimState();
        
        if(state == null) {
            throw new IllegalArgumentException("state is null");
        } else switch (state) {
            case RUNNING:
                window.pauseSimulation();
                return;
            case PAUSED:
                window.resumeSimulation();
                return;
            default:
                throw new IllegalArgumentException("app is already closed");
        }
    }
};

