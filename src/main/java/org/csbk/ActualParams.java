package org.csbk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;


public class ActualParams {

    // Переменные и пути
    private String path;
    private String[] tPod = new String[14];
    private String[] tObr = new String[14];
    private String[] pVx = new String[14];
    private String[] tStreet = new String[14];
    private String[] alarm = new String[14];
    private String[] tPlan = new String[14];
    public float[] normalPvxHigh={0.5f, 0.5f, 0.5f, 0.5f, 0.35f, 6.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,0.5f, 0.4f};
    public float[] normalPvxLow= {0.32f, 0.32f, 0.23f, 0.29f, 0.12f, 1.0f, 0.02f, 0.32f, 0.30f, 0.32f, 0.32f, 0.32f, 0.02f, 0.22f};
    private String wind;

    private List<String> actualParametrs = new ArrayList<>();
    private List<String> actualTplan = new ArrayList<>();

    public ActualParams(String path,String readDataMode, float[] customPvxHigh, float[] customPvxLow) throws IOException {
        this.path=path;
        for (int i = 0; i < normalPvxHigh.length; i++) {
          if (customPvxHigh[i]!=-1f){
              normalPvxHigh[i]=customPvxHigh[i];
          }
        }
        if (readDataMode.equals("scadaImportTXT")){
            parseTxtFile();
        }
        if (readDataMode.equals("modbusTCPIP")){
            modbusRead();  //TODO read поменять на другое название
        }
    }
    // Методы доступа
    public String[] getTPod() { return tPod; }
    public String[] gettPlan() { return tPlan; }
    public String[] getTObr() { return tObr; }
    public String[] getPVx() { return pVx; }
    public String[] getTStreet() { return tStreet; }
    public String getAlarm(int[] fixedTpod,
                             String[] correctTplan,
                             int numberOfBoiler,
                           int[] correctFromUsers){
        int[] boilerCompTable={0, 1, 2, 3, 7, 9, 10, 11};
        int correct = 0;
        for (int i = 0; i < tStreet.length; i++) {
            double tempTstreet = Float.parseFloat(tStreet[i]);
            alarm[i]=String.valueOf(tempTstreet*tempTstreet*0.00886-0.803*tempTstreet+54);
            if (fixedTpod[i]!=-1) {
                alarm[i]= String.valueOf(fixedTpod[i]);
            }
            for (int j = 0; j < boilerCompTable.length; j++) {
                if (boilerCompTable[j]==numberOfBoiler){
                    correct=Integer.parseInt(correctTplan[j]);
                }
            }
            alarm[i]=String.valueOf(Float.parseFloat(alarm[i])-5+correct +correctFromUsers[numberOfBoiler]);
        }
        return String.valueOf(alarm[numberOfBoiler]);
    }


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
        String currentDirTPlan = Paths.get("").toAbsolutePath().toString()+"/actualtplan.txt";
        File textFileForTplan = new File(currentDirTPlan);
        try (BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(textFileForTplan), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br1.readLine()) != null) {
                actualTplan.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseTPod();
        parsePVx();
        parseTStreet(); // Температура
        parseTplan();
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
        tStreet[7]=actualParametrs.get(23);  //t улицы  кот№8 "Рынок"
        tStreet[8]=actualParametrs.get(26);  //t улицы  кот№9 Макатровых
        tStreet[9]=actualParametrs.get(29);  //t  улицы кот№10  "Д/С Сказка"
        tStreet[10]=actualParametrs.get(32); //t улицы  кот№11 Полярный
        tStreet[11]=actualParametrs.get(35); //t улицы  кот№12 Департамент
        tStreet[12]=tStreet[6]; //t улицы   кот№13 офис ЧСБК 2
        tStreet[13]=tStreet[8]; //t улицы   кот№14 Шишкин
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
        tPod[8]=actualParametrs.get(24);  //t подачи кот№9 Макатровых
        tPod[9]=actualParametrs.get(27);  //t подачи кот№10  "Д/С Сказка"
        tPod[10]=actualParametrs.get(30); //t подачи кот№11 Полярный
        tPod[11]=actualParametrs.get(33); //t подачи кот№12 Департамент
        tPod[12]=actualParametrs.get(36); //t подачи кот№13 офис ЧСБК 2
        tPod[13]=actualParametrs.get(38); //t подачи кот№14 Шишкин
    }

    private void parsePVx() {
        pVx[0] = actualParametrs.get(1);   // давление кот№1 Склады Мищенко
        pVx[1] = actualParametrs.get(4);   // давление кот№2 Ендальцев         (датчик на базе)
        pVx[2] = actualParametrs.get(7);   // давление кот№3 ЧукотОптТорг      (датчик на базе)
        pVx[3] = actualParametrs.get(10);  // давление кот№4 "ЧСБК Новая"
        pVx[4] = actualParametrs.get(13);  // давление кот№5 офис "СВТ"
        pVx[5] = actualParametrs.get(16);  // давление кот№6 общежитие на Южной
        pVx[6] = actualParametrs.get(19);  // давление кот№7 офис ЧСБК
        pVx[7] = actualParametrs.get(22);  // давление  кот№8 "Рынок"
        pVx[8] = actualParametrs.get(25);  // давление  кот№9 Макатровых
        pVx[9] = actualParametrs.get(28);  // давление  кот№10  "Д/С Сказка"
        pVx[10]=actualParametrs.get(31);   // давление  кот№11 Полярный
        pVx[11]=actualParametrs.get(34);   // давление  кот№12 Департамент
        pVx[12]=actualParametrs.get(37);   // давление  кот№13 офис ЧСБК 2
        pVx[13]=actualParametrs.get(39);   // давление  кот№14 Шишкин
    }
    private void modbusRead(){

    }
    private void parseTplan(){
        for (int i = 0; i < 14; i++) {
            tPlan[i]=actualTplan.get(i);
        }
    }
}