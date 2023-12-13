package org.csbk;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoilersHangMonitor {
    private CopyOnWriteArrayList<Boiler> boilers;
    private Timer hangTimer;
    private Map<Boiler, String[]> historyMap;
    private boolean[] errors={false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    public BoilersHangMonitor(CopyOnWriteArrayList<Boiler> boilers) {
        this.boilers = boilers;
        this.historyMap = new HashMap<>();
        for (Boiler boiler : boilers) {
            historyMap.put(boiler, new String[3]);
        }
    }

    public void startMonitoring() {
        hangTimer = new Timer();
        hangTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkBoilers();
            }
        }, 0, 120000); // 2 минуты
    }
    public void updateBoilers(CopyOnWriteArrayList<Boiler> newBoilers) {
        if (hangTimer != null) {
            hangTimer.cancel();
        }
        boilers.clear();
        boilers.addAll(newBoilers);
        historyMap.clear();
        for (Boiler boiler : boilers) {
            historyMap.put(boiler, new String[3]);
        }

        startMonitoring();
    }
    private void checkBoilers() {
        for (int i = 0; i < boilers.size(); i++) {
            Boiler boiler = boilers.get(i);
            String currentTemp = boiler.gettPod();
            String[] tempHistory = historyMap.get(boiler);
            System.arraycopy(tempHistory, 1, tempHistory, 0, tempHistory.length - 1);
            tempHistory[tempHistory.length - 1] = currentTemp;
            if (tempHistory[0] != null && tempHistory[0].equals(tempHistory[1]) && tempHistory[1].equals(tempHistory[2])) {
               errors[i]=true;
            }
        }
    }
    public int getAvaryState(){
        for (int i = 0; i < errors.length; i++) {
            if(errors[i]==true){
                return i;
            }
        }
        return -1;
    }

}