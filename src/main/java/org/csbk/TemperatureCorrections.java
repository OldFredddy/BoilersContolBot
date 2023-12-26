package org.csbk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;

public class TemperatureCorrections {

    private static String currentDir1 = Paths.get("").toAbsolutePath().toString();
    private String[] correctionTpod;
    public  void changeTpod (){
        writeToFile("corrector.txt", correctionTpod);
    }
    private String[] tAlarmCorrectionFromUsers;

    public String[] getTAlarmCorrectionFromUsers() {
        if (tAlarmCorrectionFromUsers==null){
            tAlarmCorrectionFromUsers= new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        }
        return tAlarmCorrectionFromUsers;
    }

    public void setTAlarmCorrectionFromUsers(String[] tAlarmCorrectionFromUsers) {
        if (tAlarmCorrectionFromUsers==null){
            this.tAlarmCorrectionFromUsers= new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        }
        else {
            this.tAlarmCorrectionFromUsers=tAlarmCorrectionFromUsers;
        }
    }



    public String[] getCorrectionTpod() {
        if (correctionTpod==null){
           correctionTpod= new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        }
            return correctionTpod;
    }

    public void setCorrectionTpod(String[] correctionTpod) {
        for (int i = 0;i <correctionTpod.length;i++){
            this.correctionTpod[i]= String.valueOf(Integer.parseInt(this.correctionTpod[i])+Integer.parseInt(correctionTpod[i]));
        }
    }

    private static void writeToFile(String fileName, String[] numbers) {
        try {
            File file = new File(currentDir1, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                for (String number : numbers) {
                    bw.write(number + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
