package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.PlanetarySystem;
import view.SimState;
import view.Window;

public class ServerWebCommunicator extends WebCommunicator {
    
    public ServerWebCommunicator(Window wind,
                                 PlanetarySystem pl_syst_,  
                                 int init_delay_, int int_send_, 
                                 int port_) {
        
        /*window = wind;
        if(window == null) {
            throw new IllegalArgumentException("window = null");
        }*/
            
        pl_syst = pl_syst_;
        if(pl_syst == null) {
            throw new IllegalArgumentException("pl_syst = null");
        }

        if(init_delay_ < 0) {
            throw new IllegalArgumentException("init_delay_ < 0");
        }
        if(int_send_ < 0) {
            throw new IllegalArgumentException("int_send_ < 0");
        }
        
        init_delay = init_delay_;
        int_web = int_send_;
        
        type = WebType.SERVER;
        port = port_;
        
        checkPort();

        try {
            
            server_sock = new ServerSocket(port);
            client_sock = server_sock.accept();
            
            inp = new ObjectInputStream(client_sock.getInputStream());
            out = new ObjectOutputStream(client_sock.getOutputStream());
            out.flush();
            
            his_state = SimState.RUNNING;
           
        }  catch (SocketException ex) {
            Logger.getLogger(ServerWebCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerWebCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
