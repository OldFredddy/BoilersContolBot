package org.csbk;

import java.util.ArrayList;
import java.util.List;

public class BoilerManager {

    private List<TemperatureMonitor> boilerMonitors;
    public BoilerManager(int numberOfBoilers) {
        this.boilerMonitors = new ArrayList<>();

        for (int i = 0; i < numberOfBoilers; i++) {
            boilerMonitors.add(new TemperatureMonitor());
        }

    }


    public boolean isTemperatureAnomaly(int boilerIndex, double currentTemp,double tStreet, int[] fixedTpod,
                                        int[] correctTplan,int[] correctFromUsers) {
        if (boilerIndex >= 0 && boilerIndex < boilerMonitors.size()) {
            return boilerMonitors.get(boilerIndex).isTemperatureAnomaly(currentTemp,tStreet,boilerIndex,fixedTpod,correctTplan,correctFromUsers);
        } else {
            return false;
        }
    }

}