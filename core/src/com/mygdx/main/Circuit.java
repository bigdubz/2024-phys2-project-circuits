package com.mygdx.main;

import com.badlogic.gdx.utils.Array;
import com.mygdx.main.component.Battery;
import com.mygdx.main.component.Component;

public class Circuit {
    Array<Component> circuit;
    Main main;
    Battery mainBattery;
    Array<Series> waitingList;

    public Circuit(Main main, Array<Component> circuit) {
        this.main = main;
        this.circuit = new Array<>(circuit);
        this.waitingList = new Array<>();
    }

    public boolean checkValid() {
        return findMainBattery() && checkCompleteCircuit();
    }

    public boolean findMainBattery() {
        for (Component comp : circuit) {
            if (comp instanceof Battery) {
                mainBattery = (Battery) comp;
                return true;
            }
        }
        return false;
    }

    public boolean checkCompleteCircuit() {
        Component nextComponent = mainBattery.pos_term;
        try {
            while (!(nextComponent.to.first() instanceof Battery)) {
                nextComponent = nextComponent.to.first();
            }
        } catch (IllegalStateException | NullPointerException ignored) {
            return false;
        }
        return true;
    }

    public float getTotalResistance() {
        float resistance = 0;
        if (!checkCompleteCircuit()) {
            return -1;
        }

        Component nextComponent = mainBattery.pos_term;
        while (!(nextComponent instanceof Battery)) {
            resistance += nextComponent.resistance;
            nextComponent = nextComponent.to.first();
        }
        return resistance;
    }

    public void setDir(Component comp) {
        Array<Series> circuito = new Array<>();
        new Series(main, this, comp, circuito, true);
        while (!(waitingList.isEmpty() && circuito.isEmpty())) {
            for (Series ser : circuito) {
                ser.setSeriesDir();
            }
            for (Series ser : circuito) {
                ser.move();
            }
            circuito.addAll(waitingList);
            waitingList.clear();
        }
    }

    public Array<Series> getWaitingList() {
        return this.waitingList;
    }
}
