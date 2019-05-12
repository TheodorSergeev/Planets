/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package planets;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import model.PlanetarySystem;
import view.PlanetPainter;

/**
 *
 * @author root
 */
public class Planets {

    private static PlanetarySystem pl_syst;
    private static PlanetPainter pl_paint;
    private static Window main_wind;
    
    /**
     * @param args the command line arguments
     */ 
    
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

    private static Color   getPropertiesHexColor(Properties prop, String name) {
        
        if(prop == null) {
            throw new IllegalArgumentException("Properties is null in getPropertiesHexColor"); 
        }
        
        String str = prop.getProperty(name);
        Color col = null;
        
        try {
            col = Color.decode(str);
        } catch(Exception err) {
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
    
    private static void printConfigFile(String filename) {
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
        
        pl_syst = new PlanetarySystem();
        pl_paint = new PlanetPainter(dim.width - 100, dim.height, pl_syst, 100, 10, 1, 0.1);
        printConfigFile("config.properties");
        main_wind = new Window(dim.width, dim.height, pl_syst, pl_paint);
        
    }
    
}
