package planets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import model.PlanetarySystem;
import model.Point3d;

abstract public class WebCommunicator {
    
    PlanetarySystem pl_syst;
    Window window;
    
    protected Window.SimState my_state, his_state;
    
    protected int port;
    protected InetAddress ip;
    protected int type;         // server or client
    
    protected Timer timer_web;  // receive/send info via web
    
    protected ServerSocket server_sock;
    protected Socket       client_sock;
       
    protected ObjectOutputStream out;
    protected ObjectInputStream  inp;
    
    final protected int SERVER = 111;
    final protected int CLIENT = 999;
    final protected int MAX_PORT_NUMBER = 9999;
    final protected int MIN_PORT_NUMBER = 9000;

    final protected void checkPort() {
        if(port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }
    }
    
    final protected void checkIp() {
        if(ip == null) {
            throw new IllegalArgumentException("Ip is null");
        }
    }

    public void setState(Window.SimState new_state) {
        System.out.println("new = " + new_state);
        my_state = new_state;
        System.out.println("new = " + my_state);
    }
    
    private void sendMessage() {
        if(his_state == Window.SimState.CLOSED) {
            throw new IllegalArgumentException("partner is already closed");
        }

        if(type == SERVER && his_state == Window.SimState.RUNNING && my_state == Window.SimState.RUNNING) {
            sendPlanetsPos();
        } else if(his_state != Window.SimState.CLOSED) {
            sendState();
        }
    }
    
    private void sendPlanetsPos() {           
        try {
            out.writeObject(pl_syst.getPlanetsPositions());
            out.flush();
            out.reset();
        } catch (IOException ex) {
            System.out.println("!!! sendPlanetsPos failed");
            //Logger.getLogger(WebCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void sendState() {  
        
        if(out == null) {
            throw new IllegalArgumentException("sendState failed - command_out stream is null");
        }
        if(my_state == null) {
            throw new IllegalArgumentException("sendState failed - WebState my_state is null");
        }
        if(his_state == null) {
            throw new IllegalArgumentException("sendState failed - WebState his_state is null");
        }
        if(his_state == Window.SimState.CLOSED) {
            throw new IllegalArgumentException("sendState failed - WebState his_state is CLOSED");
        }

        try {
            out.writeObject(my_state);
            out.flush();
            out.reset();
        } catch (IOException ex) {
            System.out.println("sendState failed");           
            //Logger.getLogger(WebCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    private void recvMessage() {
        if(inp == null) {
            throw new IllegalArgumentException("input stream is null");
        }
        
        System.out.println("state = " + my_state);
                
        if(my_state == Window.SimState.CLOSED) {
            throw new IllegalArgumentException("connection to me is closed - no receiving");
        }
        
        try {
            Object newmsg = inp.readObject();
            
            if(type == CLIENT && newmsg instanceof ArrayList<?>) {
                if(((ArrayList<?>) newmsg).size() == pl_syst.size()) {
                    if(((ArrayList<?>) newmsg).get(0) instanceof Point3d) {
                        pl_syst.setPlanetsPositions((ArrayList<Point3d>) newmsg);
                    }
                    else  {
                        throw new IllegalArgumentException("unrecongised "
                                + "array type - needs to be Point3D");
                    }
                }
                else {
                    throw new IllegalArgumentException("size of the "
                            + "received array of planet positions "
                            + "doesn't match the existing array's size");
                }

                return;

            }
            else if(newmsg instanceof Window.SimState) {
                recvCommand(his_state, (Window.SimState) newmsg);
            }
            else {
                throw new IllegalArgumentException("unrecognised type of object received");
            }
          
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("recv failed");
            //Logger.getLogger(WebCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
    private void recvCommand(Window.SimState his_old_state, Window.SimState his_new_state) {
        
        System.out.println("his old state = " + his_old_state + "  his_new_state = " + his_new_state);
        his_state = his_new_state;
        System.out.println("his_state = " + his_state);

        if(his_old_state == his_new_state) {
            return;
        }
        else {
            
            if(his_old_state == Window.SimState.PAUSED && 
               his_new_state == Window.SimState.RUNNING && 
               my_state == Window.SimState.PAUSED) {
                window.resumeSimulation();
            }
            
            if(his_old_state == Window.SimState.RUNNING && 
               his_new_state == Window.SimState.PAUSED && 
               my_state == Window.SimState.RUNNING) {
                window.pauseSimulation();
            }
            
            if(his_old_state != Window.SimState.CLOSED && 
               his_new_state == Window.SimState.CLOSED &&
               my_state != Window.SimState.CLOSED) {
                System.out.println("received closing command");
                window.closeWindow();
            }
            
        }
        
        his_state = his_new_state;

    }
       
    
   public void closeConnection() {
        System.out.println("closeConnection");
        stopWebTimer();
       
        if(his_state != Window.SimState.CLOSED) {
            System.out.println("informing of closing connection");
            sendState();
            System.out.println("informed");
        }

        if(client_sock == null) {   
            throw new IllegalArgumentException("client stream from socket is null");
        }
        if(type == SERVER) {
            if(server_sock == null) {
                throw new IllegalArgumentException("server_sock is null");
            }
        }
        try {
            if(inp != null) {
                inp.close();
                inp = null;
            }
            if(out != null) {
                out.close();
                out = null;
            }
            System.out.println("close client");
            client_sock.close();
            client_sock = null;
            System.out.println("close client 2");
            if(type == SERVER) {
                server_sock.close();
                server_sock = null;
            }
        } catch (IOException ex) {
            System.out.println("close failed");
            //Logger.getLogger(WebCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("done");

    }

    protected class ScheduleWebTask extends TimerTask {
        @Override
        public void run() {
            if(window == null) {
                throw new IllegalArgumentException("window = null");
            }
            
            System.out.println("schedule");
            if(my_state == Window.SimState.CLOSED) {
                throw new IllegalArgumentException("connection is closed");
            }
            
            switch(type) {
                case SERVER:
                    sendMessage();
                    if(my_state == Window.SimState.CLOSED || his_state == Window.SimState.CLOSED) {
                        return;
                    }
                    recvMessage();
                    break;
                case CLIENT:
                    recvMessage();
                    if(my_state == Window.SimState.CLOSED || his_state == Window.SimState.CLOSED) {
                        return;
                    }
                    sendMessage();
                    break;
                default:
                    throw new IllegalArgumentException("type = " + type +
                            " must be equal to " + CLIENT + " (CLIENT) or " +
                            SERVER + " (SERVER)");
            }
        }
    }        
    
        
    public void stopWebTimer() {
        if(timer_web != null) {
            timer_web.cancel();
            timer_web = null;
        }
    }

    final public void startWebTimer(int init_delay_, int int_web_) {
        
        if(init_delay_ < 0) {
            throw new IllegalArgumentException("init_delay_ < 0.");
        }
        if(int_web_ < 0) {
            throw new IllegalArgumentException("int_send_ < 0.");
        }   
                  
        System.out.println("STARTSRTRS");
        timer_web   = new Timer();
        timer_web.scheduleAtFixedRate(new WebCommunicator.ScheduleWebTask(), init_delay_, int_web_);             

    }
 
       
    /*class AboutDialog extends Dialog {
        public AboutDialog(Frame parent){
            super(parent, true);         
            setBackground(Color.gray);
            setLayout(new BorderLayout());
            Panel panel = new Panel();
            panel.add(new Button("Close"));
            add("South", panel);
            setSize(300, 200);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent){
                    dispose();
                }
            });
        } 

        public boolean action(Event evt, Object arg){
            if(arg.equals("Close")){
                dispose();
                return true;
            }
            return false;
        }

        public void paint(Graphics g){
            g.setColor(Color.white);
            g.drawString("Unable to open connection. Do you want to proceed?", 10, 10);
        }
    }*/

    /*public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }*/

        /*private void sendOffscreenImage() {
        if(offscreen == null) {
            sendString(".");
            return;
            //throw new IllegalArgumentException("offscreen is null");
        }
        
        checkPort();
                
        //ImageIO.write(toBufferedImage(offscreen), "png", out);
        String img_str = null;
        
        try {
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(toBufferedImage(offscreen), "png", bos);
            byte [] data = bos.toByteArray();
            img_str = new String(data);
            sendString(img_str);
            System.out.println("sending image size = " + img_str.length());

            byte [] data2 = img_str.getBytes();
            
            if(Arrays.equals(data, data2)) {
                System.out.println("byte arrays are not equal");
            } else {
                System.out.println("byte arrays ARE equal");
            }
            
            BufferedImage img123 = ImageIO.read(new ByteArrayInputStream(data));
            BufferedImage img321 = ImageIO.read(new ByteArrayInputStream(data2));
           
            if(img123 == null) {
                System.out.println("data is fucked");
            }
            if(img321 == null) {
                System.out.println("data2 is fucked");
            }

            

        } catch (IOException ex) {
            Logger.getLogger(WebPlanetPainter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //String img_str = offscreen.toString();
        //System.out.print("image = " + img_str);
        //out.flush();
       
    }*/

    /*//System.out.println("Received string = " + newmsg);

    //offscreen = ImageIO.read(inp);
    //System.out.println("Received image = " + offscreen);
    if(newmsg.length() > 10) {
        System.out.println("received image");
        byte data[] = newmsg.getBytes();
        //System.out.println("data = " + data.length);

        BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
        offscreen = (Image) img;
        if(img == null) {
            System.out.println("nullllll");
        }
        //System.out.println("wrote image width = offscreen width " + img2.toString());
        return;
    } */
    
}
