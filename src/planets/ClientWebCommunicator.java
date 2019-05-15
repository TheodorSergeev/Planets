package planets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.PlanetarySystem;

public class ClientWebCommunicator extends WebCommunicator {
     
    public ClientWebCommunicator(Window wind,
                                 PlanetarySystem pl_syst_,  
                                 int init_delay_, int int_recv_, 
                                 int port_, InetAddress ip_) {
                
        window = wind;
        if(window == null) {
            throw new IllegalArgumentException("window = null");
        }
            
        pl_syst = pl_syst_;
        if(pl_syst == null) {
            throw new IllegalArgumentException("pl_syst = null");
        }

        type = CLIENT;
        port = port_;
        ip = ip_;
        
        checkPort();
        checkIp();
        
        try {
            
            client_sock = new Socket(ip, port);

            out = new ObjectOutputStream(client_sock.getOutputStream());
            out.flush();
            inp = new ObjectInputStream(client_sock.getInputStream());

            his_state = Window.SimState.RUNNING;

        }  catch (SocketException ex) {
            System.out.println("connection refused. closing");
            System.exit(0);
            //Logger.getLogger(ClientWebCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientWebCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        startWebTimer(init_delay_, int_recv_);
        
    }
    
}
