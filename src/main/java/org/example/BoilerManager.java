package org.example;

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

    public void addTemperature(int boilerIndex, double temperature) {
        if (boilerIndex >= 0 && boilerIndex < boilerMonitors.size()) {
            boilerMonitors.get(boilerIndex).addTemperature(temperature);
        } else {
        }
    }

    public boolean isTemperatureAnomaly(int boilerIndex, double currentTemp) {
        if (boilerIndex >= 0 && boilerIndex < boilerMonitors.size()) {
            return boilerMonitors.get(boilerIndex).isTemperatureAnomaly(currentTemp);
        } else {
            return false;
        }
    }
    public String getDevAndCurrent(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            sb.append("№"+String.valueOf(i)+" ");
            sb.append(boilerMonitors.get(i).getStandardDeviationTest());
            sb.append("\n");
        }

     return sb.toString();
    }
}