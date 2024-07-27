package com.mygdx.main;

import com.badlogic.gdx.utils.Array;
import com.mygdx.main.component.Battery;
import com.mygdx.main.component.Component;

public class Circuit {
    Array<Component> circuit;
    Main main;
    Battery mainBattery;

    public Circuit(Main main, Array<Component> circuit) {
        this.main = main;
        this.circuit = new Array<>(circuit);
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
        while (!(nextComponent instanceof Battery)) {
            try {
                nextComponent = nextComponent.con1.first();
            } catch (IllegalStateException ignored) {
                return false;
            } catch (NullPointerException ignored) {
                System.out.println(nextComponent.getClass());
            }
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
            nextComponent = nextComponent.con1.first();
            resistance += nextComponent.resistance;
        }
        return resistance;
    }

    public void updateCircuit(Array<Component> newCircuit) {
        circuit = new Array<>(newCircuit);
    }

    public void addComponent(Component comp) {
        circuit.add(comp);
    }
}
