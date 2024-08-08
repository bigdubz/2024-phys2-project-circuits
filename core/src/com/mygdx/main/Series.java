package com.mygdx.main;

import com.badlogic.gdx.utils.Array;
import com.mygdx.main.component.Battery;
import com.mygdx.main.component.Component;
import com.mygdx.main.utils.Point;

public class Series {

    Main main;
    Circuit circuit;
    Component startingComp;
    Component currentComp;
    Array<Series> mkPointer;
    Point startingPoint;
    Point endingPoint;
    boolean size1;
    float resistance = -1;

    public Series(Main main, Circuit circuit, Component startC, Array<Series> marked) {
        this.main = main;
        this.circuit = circuit;
        this.startingComp = startC;
        size1 = startingComp.to.size > 1;
        if (!size1) {
            this.currentComp = startingComp.to.first();
        }
        else {
            this.currentComp = startingComp;
        }
        mkPointer = marked;
        getStartingPoint();
    }

    public Series(Point startingPoint, Point endingPoint, float resistance) {
        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;
        this.resistance = resistance;
    }

    public void move() {
        mkPointer.add(this);
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

                    circuit.getWaitingList().add(new Series(main, circuit, comp, mkPointer));
                }
            }
        }
    }

    public boolean setSeriesDir() {
        boolean check = currentComp.to != null && currentComp.to.size > 1; // if junction found
        double dur = System.currentTimeMillis();
        while (!check && !(currentComp instanceof Battery)) {
            Component con1comp = currentComp.con1.first();
            Component con2comp = currentComp.con2.first();
            if (con1comp.to != null && con1comp.to.size == 1) {
                check = setDir(con1comp);
            } else if (con2comp.to != null && con2comp.to.size == 1) {
                check = setDir(con2comp);
            }
            if (System.currentTimeMillis() - dur >= 1000) {
                return false;
            }
        }
        return true;
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

    public float getResistance() {
        if (resistance >= 0) {
            return resistance;
        }

        float r = 0;
        currentComp = startingComp;
        r += currentComp.resistance;

        while (currentComp.to != null && currentComp.to.size == 1 && !(currentComp instanceof Battery)) {
            currentComp = currentComp.to.first();
            r += currentComp.resistance;
        }

        resistance = r;
        return resistance;
    }

    public Point getStartingPoint() {
        if (startingPoint != null) return startingPoint;
        if (startingComp.toPnt.equals(startingComp.getPos1())) {
            startingPoint = startingComp.getPos2();
        } else {
            startingPoint = startingComp.getPos1();
        }
        return startingPoint;
    }

    public Point getEndPoint() {
        if (endingPoint != null) return endingPoint;
        currentComp = startingComp;

        while (currentComp.to != null && currentComp.to.size == 1 && !(currentComp instanceof Battery)) {
            if (currentComp.to.first().toPnt == null) {
                endingPoint = currentComp.toPnt;
                return endingPoint;
            }
            currentComp = currentComp.to.first();
        }

        endingPoint = currentComp.toPnt;
        return endingPoint;
    }
}
