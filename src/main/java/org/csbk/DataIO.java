package org.csbk;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class DataIO {
    private static  String FILE_NAME = "";

    public static void saveData(AppData data) throws IOException {
        String userDir = System.getProperty("user.dir");
        FILE_NAME=userDir+"/"+"data.json";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(FILE_NAME), data);
    }

    public static AppData loadData() throws IOException {
        String userDir = System.getProperty("user.dir");
        FILE_NAME=userDir+"/"+"data.json";
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(FILE_NAME), AppData.class);
    }
}