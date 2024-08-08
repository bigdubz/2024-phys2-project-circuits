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
    public double current = -1;
    public double resistance = -1;
    public int voltage = -1;
    public boolean canShow = false;

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
                voltage = mainBattery.getVoltage();
                return;
            }
        }
    }

    public double getEquivalentResistance() {
        Array<Series> markedForRemoval = new Array<>();
        Series markedForAddition = null;
        Array<Series> currentChecking = new Array<>(allSerieses);

        while (true) {
            boolean found = false;
            for (Series ser1 : currentChecking) {
                for (int i = 0; i < currentChecking.size; i++) {
                    Series ser2 = currentChecking.get(i);
                    if (ser1 == ser2) continue;
                    if (ser1.getStartingPoint().equals(ser2.getStartingPoint()) &&
                            ser1.getEndPoint().equals(ser2.getEndPoint())) {
                        found = true;
                        markedForRemoval.add(ser1, ser2);
                        float r = 1 / ((1 / ser1.getResistance()) + (1 / ser2.getResistance()));
                        markedForAddition = new Series(ser1.getStartingPoint(), ser1.getEndPoint(), r);
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) break;
            currentChecking.removeAll(markedForRemoval, true);
            markedForRemoval.clear();
            currentChecking.add(markedForAddition);

            // unfinished :(
//            while (true) {
//                boolean found1 = false;
//                for (Series ser1 : currentChecking) {
//                    for (int i = 0; i < currentChecking.size; i++) {
//                        Series ser2 = currentChecking.get(i);
//                        if (ser1 == ser2) continue;
//                        boolean inSeries =
//                                ser1.getEndPoint().equals(markedForAddition.getStartingPoint()) &&
//                                        ser2.startingPoint.equals(markedForAddition.getEndPoint());
//                        if (inSeries) {
//                            markedForRemoval.add(ser1, ser2);
//                            markedForAddition = new Series(
//                                    ser1.getStartingPoint(),
//                                    ser2.getEndPoint(),
//                                    ser1.getResistance() + markedForAddition.getResistance() + ser2.getResistance()
//                            );
//                            found1 = true;
//                        }
//                        if (found1) break;
//                    }
//                    if (found1) break;
//                }
//                currentChecking.removeAll(markedForRemoval, true);
//                markedForRemoval.clear();
//                if (!currentChecking.contains(markedForAddition, true))
//                    currentChecking.add(markedForAddition);
//                if (!found1) break;
//            }
//            currentChecking.removeAll(markedForRemoval, true);
//            markedForRemoval.clear();
//            if (!currentChecking.contains(markedForAddition, true))
        }

        float r = 0;
        for (Series ser : currentChecking) {
            r += ser.getResistance();
        }

        resistance = r;
        current = voltage / resistance;
        canShow = true;
        return resistance;
    }

    public double getCurrent() {
        current = voltage / resistance;
        return current;
    }

    public boolean setDir(Component comp) {
        Array<Series> circuito = new Array<>();
        Array<Series> markedForRemoval = new Array<>();
        circuito.add(new Series(main, this, comp, markedForRemoval));
        allSerieses.addAll(circuito);
        while (!(waitingList.isEmpty() && circuito.isEmpty())) {
            for (Series ser : circuito) {
                if (!ser.setSeriesDir()) {
                    return false;
                }
            }
            for (Series ser : circuito) {
                ser.move();
            }
            circuito.removeAll(markedForRemoval, true);
            circuito.addAll(waitingList);
            allSerieses.addAll(waitingList);
            waitingList.clear();
            markedForRemoval.clear();
        }
        return true;
    }

    public boolean start() {
        try {
            return mainBattery.setDir();
        } catch (Exception exception) {
            return false;
        }
    }

    public Array<Series> getWaitingList() {
        return this.waitingList;
    }
}
