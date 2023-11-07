package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GudimParamsParser {
    private String path;
    public String gettPodGorod() {
        return tPodGorod;
    }
    public String getGo() {
        return go;
    }
    public String[] gettNasosnaya() {
        return tNasosnaya;
    }
    public String gettStreet() {
        return tStreet;
    }
    public Float getNormalGo() {
        return normalGo;
    }
    public Float getNormalTPodGorod() {
        return normalTPodGorod;
    }
    private String tPodGorod = new String();
    private String go = new String();
    private String[] tNasosnaya = new String[4];
    private String tStreet =new String();
    public Float normalGo = 45.0f;
    public Float normalTPodGorod = 10.0f;
    private List<String> actualParametrs=new ArrayList<>();
    // Конструктор
    public GudimParamsParser(String path) throws IOException {
        this.path=path;
        parseTxtFile();
        //ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(this::parseTxtFile, 0, 13, TimeUnit.SECONDS);
    }
    //  парсинг .txt файла
    private void parseTxtFile() {
        File textFile = new File(path);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                actualParametrs.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseTPodGorod();
        parseTnasosnaya();
        parseGo();
        parseTStreet(); // Температура
        actualParametrs.clear();
    }
    private void parseTPodGorod() {
        tPodGorod=actualParametrs.get(0);   //t подачи воды в город
    }
    private void parseTnasosnaya() {
        tNasosnaya[0]=actualParametrs.get(2);  //температурные датчики 4 внизу --температура скважина1
        tNasosnaya[1]=actualParametrs.get(3);  //температурные датчики 4 внизу --давление скважина1
        tNasosnaya[2]=actualParametrs.get(4);  //температурные датчики 4 внизу --температура скважина2
        tNasosnaya[3]=actualParametrs.get(5);  //температурные датчики 4 внизу --давление скважина2
    }
    private void parseGo() {
        go=actualParametrs.get(1);   //расход подача в город
    }
    private void parseTStreet() {
        tStreet=actualParametrs.get(6);   //t подачи воды в город
    }
}