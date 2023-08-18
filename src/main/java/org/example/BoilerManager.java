package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoilerManager {

    private List<TemperatureMonitor> boilerMonitors;
    private static Random random = new Random();

    public static int generateRandomNumber(int base, int deviation) {
        int lowerBound = base - deviation;
        int upperBound = base + deviation;
        return random.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }

    public BoilerManager(int numberOfBoilers) {
        this.boilerMonitors = new ArrayList<>();

        for (int i = 0; i < numberOfBoilers; i++) {
            boilerMonitors.add(new TemperatureMonitor());
        }
        for (int i = 0; i < 2400; i++) {                   //TODO Remove after debug
            boilerMonitors.get(0).addTemperature(generateRandomNumber(40,6));//1
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(1).addTemperature(generateRandomNumber(40,6));//2
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(2).addTemperature(generateRandomNumber(40,6));//3
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(3).addTemperature(generateRandomNumber(45,10));//4
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(4).addTemperature(generateRandomNumber(47,10));//5
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(5).addTemperature(generateRandomNumber(45,10));//6
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(6).addTemperature(generateRandomNumber(45,10));//7
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(7).addTemperature(generateRandomNumber(40,5));//8
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(8).addTemperature(generateRandomNumber(40,5));//9
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(9).addTemperature(generateRandomNumber(45,10));//10
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(10).addTemperature(generateRandomNumber(40,5));//11
        }
        for (int i = 0; i < 2400; i++) {
            boilerMonitors.get(11).addTemperature(generateRandomNumber(40,5));//12
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