package org.example;

import java.util.LinkedList;

public class TemperatureMonitor {
    private LinkedList<Double> temperatures;
    public TemperatureMonitor() {
        this.temperatures = new LinkedList<>();

    }
    private static final int[] tPlanArray = {
            94, 94, 94, 94, 94, 94, 94, 94, 94, 94,
            94, 93, 92, 91, 90, 89, 88, 87, 86, 85,
            84, 82, 81, 80, 79, 78, 77, 76, 75, 74,
            73, 72, 71, 69, 68, 67, 66, 65, 64, 63,
            62, 60, 59, 58, 57, 56, 55, 53, 52, 51,
            50, 48, 47, 46, 45, 43, 42, 41, 40, 40,
            40, 40, 40, 40, 40, 40, 40, 40, 40, 40,
            40, 40, 40, 40, 40, 40, 40, 40, 40, 40,
            40
    };
    private static final int[] tGrad = new int[81];
    public boolean isTemperatureAnomaly(double currentTemp, double tStreet,int numberOfBoiler, int[] fixedTpod) {
        for (int i = 0; i < 81; i++) {
            tGrad[i] = i - 51;
        }
        int tPlan=50;
        for (int i = 0; i < 81; i++) {
            if (Math.round(tStreet) == tGrad[i]) {
                tPlan = tPlanArray[i];
                break;
            }
        }
        if (tStreet < -50) {
            tPlan = 50;
        }
        if ((numberOfBoiler==4)||(numberOfBoiler==5)||(numberOfBoiler==6)){
            tPlan=50;
        }
        if ((numberOfBoiler==2)){
            tPlan=48;
        }
        if ((numberOfBoiler==2)){
            tPlan=50;
        }
        if ((numberOfBoiler==3)){
            tPlan=63;
        }
        if ((numberOfBoiler==8)){
            tPlan=60;
        }
        if (fixedTpod[numberOfBoiler]!=-1) {
            tPlan=fixedTpod[numberOfBoiler];
        }
            tPlan-=5;
            return currentTemp < (tPlan - 12) || currentTemp > (tPlan + 12);
    }

}
