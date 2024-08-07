package com.mygdx.main;

import com.badlogic.gdx.utils.Array;
import com.mygdx.main.component.Battery;
import com.mygdx.main.component.Component;

public class Series {

    Main main;
    Circuit circuit;
    Component startingComp;
    Component currentComp;
    Array<Series> arPointer;

    public Series(Main main, Circuit circuit, Component startC, Array<Series> circuito, boolean add) {
        this.main = main;
        this.circuit = circuit;
        this.startingComp = startC;
        this.currentComp = startingComp.to.first();
        arPointer = circuito;
        if (add) {
            arPointer.add(this);
        }
    }

    public void move() {
        arPointer.removeValue(this, true);
        if (!(currentComp instanceof Battery)) {
            for (Component comp : currentComp.to) {
                if (comp.to == null) {
                    if (currentComp.toPnt.equals(comp.getPos1())) {
                        comp.to = comp.con2;
                        comp.toPnt = comp.getPos2();
                    } else {
                        comp.to = comp.con1;
                        comp.toPnt = comp.getPos1();
                    }

                    circuit.getWaitingList().add(new Series(main, circuit, comp, arPointer, false));
                }
            }
        }
    }

    public void setSeriesDir() {
        boolean check = currentComp.to != null && currentComp.to.size > 1; // if junction observed
        while (!check && !(currentComp instanceof Battery)) {
            Component con1comp = currentComp.con1.first();
            Component con2comp = currentComp.con2.first();
            if (con1comp.to != null && con1comp.to.size == 1) {
                check = setDir(con1comp);
            } else {
                check = setDir(con2comp);
            }
        }
    }

    private boolean setDir(Component concomp) {
        boolean check;
        if (concomp.toPnt.equals(currentComp.getPos1())) {
            currentComp.to = currentComp.con2;
            currentComp.toPnt = currentComp.getPos2();
            check = currentComp.to != null && currentComp.to.size > 1;
            if (check) {
                return true;
            }
            currentComp = currentComp.con2.first();
        }
        else {
            currentComp.to = currentComp.con1;
            currentComp.toPnt = currentComp.getPos1();
            check = currentComp.to != null && currentComp.to.size > 1;
            if (check) {
                return true;
            }
            currentComp = currentComp.con1.first();
        }
        return false;
    }
}
