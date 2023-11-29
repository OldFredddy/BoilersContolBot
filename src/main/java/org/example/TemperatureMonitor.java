package org.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TemperatureMonitor {
    private LinkedList<Double> temperatures;
    public TemperatureMonitor() {
        this.temperatures = new LinkedList<>();

    }
    public String[] boilerNames = {
            "Котельная «Склады Мищенко»",                   //0   кот№1 Склады Мищенко
            "Котельная «Выставка Ендальцева»",              //1   кот№2 Ендальцев         (датчик на базе)
            "Котельная «ЧукотОптТорг»",                     //2   кот№3 ЧукотОптТорг      (датчик на базе)
            "Котельная «ЧСБК новая»",                       //3   кот№4 "ЧСБК Новая"
            "Котельная «Офис СВТ»",                         //4   кот№5 офис "СВТ"
            "Котельная «Общежитие на Южной»",               //5   кот№6 общежитие на Южной
            "Котельная «Офис ЧСБК»",                        //6   кот№7 офис ЧСБК
            "Котельная «Рынок»",                            //7   кот№8 "Рынок"
            "Котельная «Макатровых»",                       //8   кот№9 Макатровых
            "Котельная ДС «Сказка»",                        //9   кот№10  "Д/С Сказка"
            "Котельная «Полярный»",                         //10  кот№11 Полярный
            "Котельная «Департамент»",                      //11  кот№12 Департамент
            "Котельная «Офис ЧСБК квартиры»",               //12  кот№13 квартиры в офисе
            "Котельная Шишкина"                             //13  кот№14 ТО Шишкина
    };
    int[] boilerCompTable={0, 1, 2, 3, 7, 9, 10, 11};

    private static final int[] tGrad = new int[81];
    public boolean isTemperatureAnomaly(double currentTemp, double tStreet,int numberOfBoiler, int[] fixedTpod,
                                        int[] correctTplan) {
        int correct = 0;
        for (int i = 0; i < boilerCompTable.length; i++) {
            if (boilerCompTable[i]==numberOfBoiler){
                correct=correctTplan[i];
            }
        }
        for (int i = 0; i < 81; i++) {
            tGrad[i] = i - 51;
        }
        double tPlan=tStreet*tStreet*0.00886-0.803*tStreet+54;
        if (tStreet < -50) {
            tPlan = 50;
        }
        if (fixedTpod[numberOfBoiler]!=-1) {
            tPlan=fixedTpod[numberOfBoiler];
        }
            tPlan-=5;
            tPlan+=correct;
            return currentTemp < (tPlan - 12) || currentTemp > (tPlan + 12);
    }

}
