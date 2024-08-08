package com.mygdx.main;

import com.badlogic.gdx.utils.Array;
import com.mygdx.main.component.Battery;
import com.mygdx.main.component.Component;

public class Circuit {
    Array<Component> circuit;
    Main main;
    Battery mainBattery;
    Array<Series> waitingList;
    Array<Series> allSerieses;
    double current = 0;

    public Circuit(Main main, Array<Component> circuit) {
        this.main = main;
        this.circuit = new Array<>(circuit);
        this.waitingList = new Array<>();
        this.allSerieses = new Array<>();
        findMainBattery();
    }

    public void findMainBattery() {
        for (Component comp : circuit) {
            if (comp instanceof Battery) {
                mainBattery = (Battery) comp;
            }
        }
    }

    public double getTotalResistance() {
        double resistance = 0;
        for (Series ser : allSerieses) {
            double r = ser.getResistance();
            if (r != 0) {
                resistance += (1/r);
            }
        }
        return (1 / resistance);
    }

    public double getCurrent() {
        current = mainBattery.getVoltage() / getTotalResistance();
        return current;
    }

    public void setDir(Component comp) {
        Array<Series> circuito = new Array<>();
        new Series(main, this, comp, circuito, true);
        allSerieses.addAll(circuito);
        while (!(waitingList.isEmpty() && circuito.isEmpty())) {
            for (Series ser : circuito) {
                ser.setSeriesDir();
            }
            for (Series ser : circuito) {
                ser.move();
            }
            circuito.addAll(waitingList);
            allSerieses.addAll(waitingList);
            waitingList.clear();
        }
    }

    public Array<Series> getWaitingList() {
        return this.waitingList;
    }
}
