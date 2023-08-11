package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoilerManager {

    private List<TemperatureMonitor> boilerMonitors;
    private int getRandom(int min, int max){
        int diff = max - min;
        Random random = new Random();
        int i = random.nextInt(diff + 1);
        i += min;
        return i;
    }
    public BoilerManager(int numberOfBoilers) {
        this.boilerMonitors = new ArrayList<>();

        for (int i = 0; i < numberOfBoilers; i++) {
            boilerMonitors.add(new TemperatureMonitor());
        }
        for (int i = 0; i < 2400; i++) {                   //TODO Remove after debug
            boilerMonitors.get(0).addTemperature(getRandom(36,44));
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(1).addTemperature(-1000);
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(2).addTemperature(getRandom(36,48));
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(3).addTemperature(getRandom(40,53));
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(4).addTemperature(getRandom(40,56));
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(5).addTemperature(getRandom(36,58));
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(6).addTemperature(getRandom(36,58));
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(7).addTemperature(getRandom(36,50));
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(8).addTemperature(-1000);
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(9).addTemperature(-1000);
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(10).addTemperature(-1000);
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(11).addTemperature(-1000);
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