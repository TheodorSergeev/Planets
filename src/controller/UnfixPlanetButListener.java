package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.PlanetarySystem;
import view.Window;

public class UnfixPlanetButListener implements ActionListener{
    
    Window wind;
    PlanetarySystem pl_syst;
    int pl_num;
    
    public UnfixPlanetButListener(Window wind_, PlanetarySystem pl_syst_, int pl_num_) {
        if(pl_syst_ == null) {
            throw new IllegalArgumentException("pl_syst_ = null");
        }
        if(pl_num_ < 0) {
            throw new IllegalArgumentException("pl_num_ = null");
        }
        if(wind_ == null) {
            throw new IllegalArgumentException("wind_ = null");
        }

        wind = wind_;
        pl_num = pl_num_;
        pl_syst = pl_syst_;
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Unfix");
        pl_syst.changeFixPlanet(pl_num);
        wind.redrawButtons();
    }
};

