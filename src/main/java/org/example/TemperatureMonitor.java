package org.example;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TemperatureMonitor {
    private LinkedList<Double> temperatures;
    private static final int MAKATROVYH=-6;
    public TemperatureMonitor() {
        this.temperatures = new LinkedList<>();
    }
    int[] boilerCompTable={0, 1, 2, 3, 7, 9, 10, 11};

    public boolean isTemperatureAnomaly(double currentTemp, double tStreet,int numberOfBoiler, int[] fixedTpod,
                                        int[] correctTplan, int[] correctFromUsers) {
        int correct = 0;
        for (int i = 0; i < boilerCompTable.length; i++) {
            if (boilerCompTable[i]==numberOfBoiler){
                correct=correctTplan[i];
            }
        }

        double tPlan=tStreet*tStreet*0.00886-0.803*tStreet+54;
        if (tStreet < -50) {
            tPlan = 50;
        }
        if (fixedTpod[numberOfBoiler]!=-1) {
            tPlan=fixedTpod[numberOfBoiler];
        }
        if (numberOfBoiler == 8){
            LocalTime currentTime = LocalTime.now();
            LocalTime start = LocalTime.of(22, 20);
            LocalTime end = LocalTime.of(7, 40);

            if (currentTime.isAfter(start) && currentTime.isBefore(end)) {
                tPlan+=MAKATROVYH;
            }
        }
        tPlan-=5;
            tPlan+=correct;
            tPlan+=correctFromUsers[numberOfBoiler];
            return currentTemp < (tPlan - 15) || currentTemp > (tPlan + 12);
    }

}
