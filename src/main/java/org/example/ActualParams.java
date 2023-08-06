package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class ActualParams {

    // Переменные и пути
    private String path;
    private String[] tPod = new String[12];
    private String[] tObr = new String[12];
    private String[] pVx = new String[12];
    private String[] tStreet = new String[12];
    public Float[] normalPvxHigh={0.45f, 0.50f, 0.35f, 0.39f, 0.150f, 6.0f, 0.45f, 0.44f, 0.39f, 0.45f, 0.45f, 0.45f};
    public Float[] normalPvxLow= {0.35f, 0.25f, 0.23f, 0.31f, 0.011f, 1.0f, 0.02f, 0.35f, 0.35f, 0.35f, 0.35f, 0.35f};
    private String wind;

    private List<String> actualParametrs=new ArrayList<>();

    // Конструктор
    public ActualParams(String path) throws IOException {
        this.path=path;
        parseTxtFile();
        //ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(this::parseTxtFile, 0, 13, TimeUnit.SECONDS);
    }

    // Методы доступа
    public String[] getTPod() { return tPod; }
    public String[] getTObr() { return tObr; }
    public String[] getPVx() { return pVx; }
    public String[] getTStreet() { return tStreet; }



    // Метод, выполняющий парсинг .txt файла
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
        parseTPod();
        parsePVx();
        parseTStreet(); // Температура
        actualParametrs.clear();
    }
    private void parseTStreet() {
        tStreet[0]=actualParametrs.get(2);   //t улицы кот№1 Склады Мищенко
        tStreet[1]=actualParametrs.get(5);   //t улицы кот№2 Ендальцев         (датчик на базе)
        tStreet[2]=actualParametrs.get(8);   //t улицы кот№3 ЧукотОптТорг      (датчик на базе)
        tStreet[3]=actualParametrs.get(11);  //t улицы кот№4 "ЧСБК Новая"
        tStreet[4]=actualParametrs.get(14);  //t улицы кот№5 офис "СВТ"
        tStreet[5]=actualParametrs.get(17);  //t улицы кот№6 общежитие на Южной
        tStreet[6]=actualParametrs.get(20);  //t улицы кот№7 офис ЧСБК
        tStreet[7]=actualParametrs.get(23);  //t улицы кот№8 "Рынок"
        tStreet[8]=actualParametrs.get(26);  //t улицы кот№10 Макатровых
        tStreet[9]=actualParametrs.get(29);  //t  улицы кот№11  "Д/С Сказка"
        tStreet[10]=actualParametrs.get(32); //t улицы  кот№12 Полярный
        tStreet[11]=actualParametrs.get(35); //t улицы  кот№13 Департамент
    }
    // Отдельные методы для парсинга различных параметров
    private void parseTPod() {
        tPod[0]=actualParametrs.get(0);  //t подачи кот№1 Склады Мищенко
        tPod[1]=actualParametrs.get(3);  //t подачи кот№2 Ендальцев         (датчик на базе)
        tPod[2]=actualParametrs.get(6);  //t подачи кот№3 ЧукотОптТорг      (датчик на базе)
        tPod[3]=actualParametrs.get(9);  //t подачи кот№4 "ЧСБК Новая"
        tPod[4]=actualParametrs.get(12); //t подачи кот№5 офис "СВТ"
        tPod[5]=actualParametrs.get(15); //t подачи кот№6 общежитие на Южной
        tPod[6]=actualParametrs.get(18); //t подачи кот№7 офис ЧСБК
        tPod[7]=actualParametrs.get(21);  //t подачи кот№8 "Рынок"
        tPod[8]=actualParametrs.get(24);  //t подачи кот№10 Макатровых
        tPod[9]=actualParametrs.get(27);  //t подачи кот№11  "Д/С Сказка"
        tPod[10]=actualParametrs.get(30); //t подачи кот№12 Полярный
        tPod[11]=actualParametrs.get(33); //t подачи кот№13 Департамент
    }

    private void parsePVx() {
        pVx[0] = actualParametrs.get(1);   // давление  кот№1 Склады Мищенко
        pVx[1] = actualParametrs.get(4);   // давление кот№2 Ендальцев         (датчик на базе)
        pVx[2] = actualParametrs.get(7);   // давление кот№3 ЧукотОптТорг      (датчик на базе)
        pVx[3] = actualParametrs.get(10);  // давление  кот№4 "ЧСБК Новая"
        pVx[4] = actualParametrs.get(13);  // давление  кот№5 офис "СВТ"
        pVx[5] = actualParametrs.get(16);  // давление  кот№6 общежитие на Южной
        pVx[6] = actualParametrs.get(19);  // давление  кот№7 офис ЧСБК
        pVx[7] = actualParametrs.get(22);  // давление  кот№8 "Рынок"
        pVx[8] = actualParametrs.get(25);  // давление  кот№10 Макатровых
        pVx[9] = actualParametrs.get(28);  // давление  кот№11  "Д/С Сказка"
        pVx[10]=actualParametrs.get(31);   // давление  кот№12 Полярный
        pVx[11]=actualParametrs.get(34);   // давление  кот№13 Департамент
    }
}