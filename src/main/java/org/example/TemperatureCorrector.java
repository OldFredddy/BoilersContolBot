package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;

public class TemperatureCorrector {

    private static String currentDir1 = Paths.get("").toAbsolutePath().toString();
    public static void changeTpod (int[] numbers1){
        writeToFile("corrector.txt", numbers1);
    }


    private static void writeToFile(String fileName, int[] numbers) {
        try {
            File file = new File(currentDir1, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                for (int number : numbers) {
                    bw.write(number + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
