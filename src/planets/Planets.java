package planets;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.*;
import view.*;
import controller.*;
        
public class Planets {

    private static PlanetarySystem pl_syst;
    private static PlanetPainter pl_paint;
    private static Window main_wind;
    
    private static boolean getPropertiesBoolean(Properties prop, String name) {
        
        if(prop == null) {
            throw new IllegalArgumentException("Properties is null in getPropertiesBoolean"); 
        }
        
        String str = prop.getProperty(name);
        boolean num = false;
        
        try {
            num = Boolean.parseBoolean(str);
        } catch(java.lang.NumberFormatException err) {
            System.out.println("string " + str + " is not boolean");
        }
        
        return num;
        
    }

    private static Color getPropertiesHexColor(Properties prop, String name) {
        
        if(prop == null) {
            throw new IllegalArgumentException("Properties is null in getPropertiesHexColor"); 
        }
        
        String str = prop.getProperty(name);
        Color col = null;
        
        try {
            col = Color.decode(str);
        } catch(NumberFormatException err) {
            System.out.println("string " + str + " is not hex color");
        }
        
        return col;
        
    }
    
    private static int     getPropertiesInt(Properties prop, String name) {

        if(prop == null) {
            throw new IllegalArgumentException("Properties is null in getPropertiesInt"); 
        }
        
        String str = prop.getProperty(name);
        int num = 0;
        
        try {
            num = Integer.parseInt(str);
        } catch(java.lang.NumberFormatException err) {
            System.out.println("string " + str + " is not integer");
        }
        
        return num;

    }
    
    private static double  getPropertiesDouble(Properties prop, String name) {

        if(prop == null) {
            throw new IllegalArgumentException("Properties is null in getPropertiesDouble"); 
        }
        
        String str = prop.getProperty(name);
        double num = 0.0;
        
        try {
            num = Double.parseDouble(str);
        } catch(java.lang.NumberFormatException err) {
            System.out.println("string " + str + " is not double");
        } catch(Exception err) {
            System.out.println("error while parsing " + str + " to double");
        }
        
        return num;

    }
    
    private static void readConfigFile(String filename) {
        if(pl_syst == null) {
            throw new IllegalArgumentException("failed readConfigFile - pl_syst = null");
        }
        if(pl_paint == null) {
            throw new IllegalArgumentException("failed readConfigFile - pl_paint = null");
        }
        
        try (InputStream input = new FileInputStream(filename)) {

            Properties prop = new Properties();
            
            prop.load(input); // load a properties file
            //prop.list(System.out);
            
            int pl_num = getPropertiesInt(prop, "pl_num");

            for(int i = 0; i < pl_num; ++i) {

                Color   color  = getPropertiesHexColor(prop, i + ".color");
                double  mass   = getPropertiesDouble  (prop, i + ".mass");
                double  radius = getPropertiesDouble  (prop, i + ".radius");
                double  x      = getPropertiesDouble  (prop, i + ".x");
                double  y      = getPropertiesDouble  (prop, i + ".y");
                double  vx     = getPropertiesDouble  (prop, i + ".vx");
                double  vy     = getPropertiesDouble  (prop, i + ".vy");
                boolean fixed  = getPropertiesBoolean (prop, i + ".fixed");
                
                pl_syst.addPlanet(mass, radius, x, y, vx, vy, fixed);
                pl_paint.addPlanetColor(color);
                //System.out.println(color + " " + mass + " " + radius + " " + x + " " + y  + " " + vx  + " " + vy  + " " + fixed);

            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    
    public static void main(String[] args) {
    
        Dimension dim = new Dimension(700, 500);
        
        pl_syst  = null;
        InetAddress ip = null;
        
        try {
            ip = InetAddress.getByName("127.0.0.1"); //.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Planets.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int flag = 1;
        
        int port = 9129;
        
        double dt = 0.1;
        double paint_wait = 0.1;
        
        pl_syst = new PlanetarySystem(dt);

        if(flag == 0) {
            pl_paint = new PlanetPainter(dim.width - 100, dim.height, pl_syst, paint_wait, 100, 10, 1);
            readConfigFile("config.properties");
            ServerWebCommunicator server =  new ServerWebCommunicator(main_wind, 
                                                                      pl_syst,
                                                                      100, 3, 
                                                                      port);   
            main_wind = new Window(dim.width, dim.height, pl_syst, pl_paint, server);

        }
        else if(flag == 1) {
            pl_paint = new PlanetPainter(dim.width - 100, dim.height, pl_syst, paint_wait, 100, 10); // no calculations
            readConfigFile("config.properties");
            ClientWebCommunicator client =  new ClientWebCommunicator(main_wind,  
                                                                      pl_syst, 
                                                                      100, 3,
                                                                      port, ip);
            main_wind = new Window(dim.width, dim.height, pl_syst, pl_paint, client);

        }
        else {
            pl_paint = new PlanetPainter(dim.width - 100, dim.height, pl_syst, paint_wait, 100, 10, 1);
            readConfigFile("config.properties");
            main_wind = new Window(dim.width, dim.height, pl_syst, pl_paint); // no web comm
        }
                
        main_wind.start();
        
    }
    
}
