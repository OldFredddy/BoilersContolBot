package org.example;

import java.util.LinkedList;

public class TemperatureMonitor {
    private static final int FOUR_HOURS = 2400;
    private static final int TWO_HOUR = 1200;

    private LinkedList<Double> temperatures;
    private double sum;

    public TemperatureMonitor() {
        this.temperatures = new LinkedList<>();
        this.sum = 0;
    }

    public void addTemperature(double temp) {
        temperatures.add(temp);
        sum += temp;

        if (temperatures.size() > FOUR_HOURS) {
            sum -= temperatures.removeFirst();
        }
    }

    private double getAverage() {
        if (temperatures.size()==0){
            return 0;
        }
        if (temperatures.size() <= TWO_HOUR) {
            return sum / temperatures.size();
        } else {
            return sum / (temperatures.size() - TWO_HOUR);
        }
    }

    public double getStandardDeviation(double mean) {
        double variance = 0;
        int count = 0;
        for (int i = 0; i < temperatures.size() - TWO_HOUR; i++) {
            variance += Math.pow(temperatures.get(i) - mean, 2);
            count++;
        }

        return Math.sqrt(variance / count);
    }

    public boolean isTemperatureAnomaly(double currentTemp) {
        double average = getAverage();
        double stdDev = getStandardDeviation(average);

        return currentTemp < (average - 2 * stdDev) || currentTemp > (average + 2 * stdDev);
    }
    public String getStandardDeviationTest() {
        double average = getAverage();
        double stdDev1 = getStandardDeviation(average);
        String result= String.valueOf(average);
        result+=", "+String.valueOf(stdDev1);
        return result;
    }
}
