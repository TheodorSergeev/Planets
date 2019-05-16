package controller;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import view.Window;

public class MainWindowListener implements WindowListener {
    
    Window window;
    
    public MainWindowListener(Window wind_) {
        if(wind_ == null) {
            throw new IllegalArgumentException("wind_ = null");
        }
        
        window = wind_;
        
    }
    
    @Override
    public void windowClosing(WindowEvent e) {   
        window.closeWindow();
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
    
};

