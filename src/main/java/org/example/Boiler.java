package org.example;

public class Boiler {
    public String gettPod() {
        return tPod;
    }

    public void settPod(String tPod) {
        this.tPod = String.format("%.1f", Double.parseDouble(tPod)) +" °C";
    }

    public String getpPod() {
        return pPod;
    }

    public void setpPod(String pPod) {
        if (pPod != null && pPod.length() >= 4) {
            this.pPod = pPod.substring(0, 4)+ " МПа";
        } else {
            this.pPod = pPod+ " МПа"; // или присвойте какое-то другое значение по умолчанию
        } ;
    }

    public String gettUlica() {
        return tUlica;
    }

    public void settUlica(String tUlica) {
        this.tUlica = String.format("%.1f", Double.parseDouble(tUlica)) + " °C";
    }
    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    boolean isOk;
    String tPod;
    String pPod;
    String tUlica;
    String tPlan;
    String tAlarm;
    int imageResId;
    String correctionTpod;

    public String getCorrectionTpod() {
        return correctionTpod;
    }

    public void setCorrectionTpod(String correctionTpod) {
        this.correctionTpod = correctionTpod;
    }



    public String gettPlan() {
        return tPlan;
    }

    public void settPlan(String tPlan) {
        this.tPlan = "Контроллер: " + String.format("%.1f", Double.parseDouble(tPlan)) + " °C";
    }

    public String gettAlarm() {
        return tAlarm ;
    }

    public void settAlarm(String tAlarm) {
        this.tAlarm = "Ср. т. аварии: " + String.format("%.1f", Double.parseDouble(tAlarm)) + " °C";
    }


}
