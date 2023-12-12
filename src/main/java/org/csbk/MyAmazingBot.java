package org.csbk;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class MyAmazingBot extends TelegramLongPollingBot {
    public MyAmazingBot(int[] fixedTpod, float[] fixedPpodHigh, float[] fixedPpodLow, String readDataMode, Controller controller) throws IOException {
      this.fixedPpodHigh=fixedPpodHigh;
      this.fixedPpodLow=fixedPpodLow;
      this.fixedTpod=fixedTpod;
      this.readDataMode=readDataMode;
      this.controller = controller;
      correctForScada=DataIO.loadData().getCorrectForScada();
      clientsId.add(1102774002L);
      clientsId.add(6290939545L);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId("@BoilersAnadyr");
        ActualParams actualParams = new ActualParams(currentDir,readDataMode, this.fixedPpodHigh,this.fixedPpodLow);

        sendMessage.setText(getCurrentParamsText(actualParams,errorsArray));
        sendMessage.setParseMode("Markdown");
        try {
            Message message = execute(sendMessage);
            messageId = message.getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        monitorThread2.start();
        //clientsId.add();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId("@BoilersAnadyr");
                ActualParams actualParams = null;
                try {
                    actualParams = new ActualParams(currentDir,readDataMode, fixedPpodHigh, fixedPpodLow);
                    LocalTime currentTime = LocalTime.now();
                    String formattedTime = currentTime.format(formatter);

                sendMessage.setText(formattedTime+"\n"+getCurrentParamsText(actualParams,errorsArray));
                sendMessage.setParseMode("Markdown");
                    Message message = execute(sendMessage);
                    Thread.sleep(2800);
                    DeleteMessage deleteMessage = new DeleteMessage("@BoilersAnadyr",messageId);
                    Thread.sleep(1800);
                    messageId = message.getMessageId();
                    Thread.sleep(1800);
                    execute(deleteMessage);
                    Thread.sleep(1800);
                } catch (TelegramApiException | InterruptedException |IOException e) {

                }
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000); // Первое значение - задержка перед первым запуском, второе - интервал между запусками
        timerForBreaks.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ActualParams actualParams = null;
                try {
                    actualParams = new ActualParams(currentDir,readDataMode, fixedPpodHigh, fixedPpodLow);
                    if (counter.equals(0)){
                        hangTpod0=actualParams.getTPod();
                    }
                    if (counter.equals(1)){
                        hangTpod1=actualParams.getTPod();
                    }
                    if (counter.equals(2)){
                        hangTpod2=actualParams.getTPod();
                    }
                    if (counter > 2){
                        counter=0;
                    }
                    if (arraysEquals(hangTpod0,hangTpod1,hangTpod2)) {
                        sendAttention(numErrBoiler,"Зависание! Значения не менялись 15 минут");
                    }
                    Thread.sleep(1800);
                } catch (TelegramApiException|InterruptedException | IOException e) {

                }
            }
        }, 5  * 1000, 5 * 60 * 1000); // Первое значение - задержка перед первым запуском, второе - интервал между запусками
     }
   BoilerManager boilerManager = new BoilerManager(13);
    Controller controller;
    String[] hangTpod0;
    String[] hangTpod1={ "1", "2","3","4","5","6","7","8","9","10","11","12","13","0"};
    String[] hangTpod2={"0", "1", "2","3","4","5","6","7","8","9","10","11","12","13"};
    static volatile boolean keepRunning = true;
    static volatile int boilerControlNum = -1;
    Integer counter =0;
    String readDataMode;
    private volatile int[] fixedTpod;
    private volatile float[] fixedPpodHigh;
    private volatile float[] fixedPpodLow;
    private volatile boolean enableCallService=false;
    public volatile int[] correctForScada = {0, 0, 0, 0, 0, 0, 0, 0};
    public volatile int[] correctFromUsers = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
     private Integer[] avaryMessageID=new Integer[2];
    private Timer timer = new Timer();
    private Timer timerForBreaks = new Timer();
    Integer[] avary2MessageID= new Integer[2];
    Integer[] avary3MessageID=new Integer[2];
    private long chatId = -1;
    public boolean [] errorsArray = {false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    static volatile Integer messageId = -1;
    List<Long> clientsId = new ArrayList<>();

    public boolean checkForAvary =true;
    String currentDir = Paths.get("").toAbsolutePath().toString()+"/actualparams.txt";

    Thread monitorThread2 = new Thread(()->{
        while (keepRunning){
            try {
                ActualParams actualParams2 = null;
                try {
                    actualParams2 = new ActualParams(currentDir,readDataMode, fixedPpodHigh, fixedPpodLow);
                } catch (IOException e) {
                    continue;
                }
                try {
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    continue;
                }
                refreshMessage(getCurrentParamsText(actualParams2, errorsArray));
                Thread.sleep(1500);
                //блок проверки аварий
               if (true) {
                for (int i = 0; i < actualParams2.getTPod().length; i++) {
                    try {
                       if (errorsArray[i]){
                           continue;
                       }
                        String s =actualParams2.getPVx()[i] ;
                        if ((Float.parseFloat(actualParams2.getTPod()[i])<30)&&(!actualParams2.getPVx()[i].equals("-1000"))) {
                            try {
                                sendAttention(i, "Проблема в температуре подачи!");
                                Thread.sleep(2000);
                            } catch (TelegramApiException e) {
                                continue;
                            }
                        }
                    }catch (NumberFormatException e){
                        continue;
                    }
                }
                for (int i = 0; i < actualParams2.getTPod().length; i++) {
                    try {
                        if (errorsArray[i]){
                            continue;
                        }
                        if ((boilerManager.isTemperatureAnomaly(i,Float.parseFloat(actualParams2.getTPod()[i]),
                                Float.parseFloat(actualParams2.getTStreet()[i]),fixedTpod, correctForScada, correctFromUsers))&&(!actualParams2.getPVx()[i].equals("-1000"))) {
                            try {
                                sendAttention(i, "Проблема в температуре подачи!");
                                Thread.sleep(2000);
                            } catch (TelegramApiException e) {
                                continue;
                            }
                        }
                    }catch (NumberFormatException e){
                        continue;
                    }
                }
                for (int i = 0; i < actualParams2.getPVx().length; i++) {
                    try {
                        if (errorsArray[i]){
                            continue;
                        }
                        if ((Float.parseFloat(actualParams2.getPVx()[i]) < actualParams2.normalPvxLow[i])&&(!actualParams2.getPVx()[i].equals("-1000"))) {
                            sendAttention(i, "Проблема в давлении! Ниже допустимого!");
                            Thread.sleep(2000);
                        }
                    }catch (NumberFormatException e){
                        continue;
                    } catch (TelegramApiException e) {
                        continue;
                    }
                }
                for (int i = 0; i < actualParams2.getPVx().length; i++) {
                    try {
                        if (errorsArray[i]){
                            continue;
                        }
                        if ((Float.parseFloat(actualParams2.getPVx()[i]) > actualParams2.normalPvxHigh[i])&&(!actualParams2.getPVx()[i].equals("-1000"))) {
                            sendAttention(i, "Проблема в давлении! Превышение!");
                            Thread.sleep(2000);
                        }
                    }catch (NumberFormatException e){
                        continue;
                    } catch (TelegramApiException e) {
                        continue;
                    }
                }
            }
               actualParams2 = null;
                System.gc();
            } catch (RuntimeException e) {
                continue;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });
    private void refreshMessage(String text){
        LocalTime currentTime = LocalTime.now();
        String formattedTime = currentTime.format(formatter);
        text=formattedTime+"\n"+text;
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId("@BoilersAnadyr");
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setParseMode("Markdown");
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public String getCurrentParamsText(ActualParams actualParams, boolean[] errorsArray) {
        StringBuilder result = new StringBuilder();

        String[][] data = {
                actualParams.getTPod(),
                actualParams.getPVx(),
                actualParams.getTStreet()
        };
        int[] maxLengths = new int[5];
        maxLengths[0] = String.valueOf(boilerNames.length).length();
        maxLengths[1] = Arrays.stream(data[0]).filter(Objects::nonNull).mapToInt(String::length).max().orElse(0);
        maxLengths[2] = Arrays.stream(data[1]).filter(Objects::nonNull).mapToInt(String::length).max().orElse(0);
        maxLengths[3] = Arrays.stream(data[2]).filter(Objects::nonNull).mapToInt(String::length).max().orElse(0);
        maxLengths[4] = 1;  // Длина эмодзи

        int maxWidth = 25; // Максимальная ширина столбца

        // Проверяем, не превышает ли ширина столбца максимальное значение, и если превышает, устанавливаем максимальную ширину
        for (int i = 0; i < maxLengths.length; i++) {
            maxLengths[i] = Math.min(maxLengths[i], maxWidth);
        }

        String format = "| %" + maxLengths[0] + "s | %" + maxLengths[1] + "s | %" + maxLengths[2] + "s | %" + maxLengths[3] + "s | %" + maxLengths[4] + "s \n";

        String header = String.format(format, "№", "Tпод", "Pпод", "Tул", "S");
        result.append("```\n").append(header);
        String units = String.format(format, "", "°C", "МПа", "°C", "");
        result.append(units);
        for (int i = 0; i < boilerNames.length; i++) {
            String emoji = errorsArray[i] ? "🔴" : "🟢";
            String formattedRow = String.format(format, (i + 1), data[0][i] == null ? "" : data[0][i], data[1][i] == null ? "" : data[1][i], data[2][i] == null ? "" : data[2][i], emoji);
            result.append(formattedRow);
        }
        result.append("```\n");
        return result.toString();
    }
    private void sendAttention(int boilerIndex, String comment) throws TelegramApiException, InterruptedException {
        String currentDir = Paths.get("").toAbsolutePath().toString()+"/actualparams.txt";
        checkForAvary=false;
        ActualParams actualParams = null;
        errorsArray[boilerIndex]=true;
        try {
            actualParams = new ActualParams(currentDir,readDataMode, fixedPpodHigh, fixedPpodLow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
       if (enableCallService){
           ZvonokPostService.call("+79140808817");
           ZvonokPostService.call("+79145353244");
       }
        for (int i = 0; i < clientsId.size() ; i++) {
            SendMessage message1 = new SendMessage();
            message1.setChatId(clientsId.get(i));//тут чат id
            message1.setText("Аварийное значение!"+"\n"+"Общие параметры на момент аварии:"+"\n"+boilerNames[boilerIndex]+"\n"
                    +"\uD83D\uDD25 Температура уходящей воды: " + actualParams.getTPod()[boilerIndex] +" °C"+"\n"
                    + "⚖️\uD83D\uDCA8 Давление в системе отопления: " + actualParams.getPVx()[boilerIndex]+" МПа"+"\n"+comment);
             // Отправляем сообщение
            Message message = execute(message1);
            avaryMessageID[i] = message.getMessageId();
            Message message2 = execute(Messages.avaryKeyboard(String.valueOf(clientsId.get(i))));
            avary3MessageID[i]= message2.getMessageId();
            // Возвращаемся к предыдущему экрану клавиатуры
           if (!secondAttempt[boilerIndex]){
               Thread.sleep(10000);
               for (int j = 0; j < errorsArray.length; j++) {
                   errorsArray[j]=false;
               }
               checkForAvary=true;
               secondAttempt[boilerIndex]=true;
           }

        }
    }
private boolean[] secondAttempt={false,false,false,false,false,false,false,false,false,false,false,false,false,false};
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

    @Override
    public void onUpdateReceived(Update update) {

        Map<String, Integer> boilerIndices = new HashMap<>();
        for (int i = 0; i < 13; i++) {
            boilerIndices.put(String.format("boiler%d", i), i);
        }
        String callbackData="-1";
        if (update.getCallbackQuery()!=null) {
             callbackData = update.getCallbackQuery().getData(); // Получаем данные обратного вызова
        }
        if (update.hasMessage()){
            if (update.getMessage().toString().contains("Добавь меня")) {
                Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                Matcher matcher = pattern.matcher(update.getMessage().toString());
                while (matcher.find()) {
                    try {
                        int id = Integer.parseInt(matcher.group(1));
                        clientsId.add((long) id);
                    } catch (NumberFormatException e) {
                        System.out.println("Найденный текст между скобками не является числом: " + matcher.group(1));
                    }
                }
            } else {
                try {
                    String chatId=update.getMessage().getChatId().toString();
                    execute(Messages.startKeyboard(chatId)); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        }
        if (update.hasCallbackQuery()){
            if (update.getCallbackQuery().getData().equals("avaryReset")){
                for (int i = 0; i < errorsArray.length; i++) {
                    errorsArray[i]=false;
                    secondAttempt[i]=false;
                }
                checkForAvary=true;
                for (int i = 0; i < clientsId.size(); i++) {
                    DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(clientsId.get(i)),avaryMessageID[i]);
                    SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),"Готово");
                    try {
                        Message message2 = execute(message);
                        avary2MessageID[i]= message2.getMessageId();
                        Thread.sleep(2000);
                    } catch (TelegramApiException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    DeleteMessage deleteMessage1 = new DeleteMessage(String.valueOf(clientsId.get(i)),avary2MessageID[i]);
                    DeleteMessage deleteMessage2 = new DeleteMessage(String.valueOf(clientsId.get(i)),avary3MessageID[i]);
                    try {
                        execute(deleteMessage2);
                        execute(deleteMessage1);
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (update.getCallbackQuery().getData().equals("enableCallService")){

                    for (int i = 0; i < clientsId.size(); i++) {
                        SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Звонки включены!");
                        try {
                            Message message2 = execute(message);
                            avary2MessageID[i] = message2.getMessageId();
                            Thread.sleep(2000);
                        } catch (TelegramApiException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        enableCallService = true;
                    }
            }
            if (update.getCallbackQuery().getData().equals("disableCallService")){
                    for (int i = 0; i < clientsId.size(); i++) {
                        SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Звонки выключены!");
                        try {
                            Message message2 = execute(message);
                            avary2MessageID[i] = message2.getMessageId();
                            Thread.sleep(2000);
                        } catch (TelegramApiException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        enableCallService = false;
                    }
            }
            if (update.getCallbackQuery().getData().equals("increaseTpod")||update.getCallbackQuery().getData().equals("decreaseTpod")){

                    try {
                        if (update.getCallbackQuery().getData().equals("increaseTpod")) { //TODO ЗАЩИТУ от НСД!
                            SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Запрос на +3 отправлен!");
                            execute(message);
                            correctForScada[boilerControlNum] += 3;
                            controller.data.setCorrectForScada(correctForScada);
                            DataIO.saveData(controller.data);
                            TemperatureCorrector.changeTpod(correctForScada);
                            Thread.sleep(2000);
                        }
                        if (update.getCallbackQuery().getData().equals("decreaseTpod")) {
                            SendMessage message = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Запрос на -3 отправлен!");
                            execute(message);//TODO дописать защиту по ClientsID
                            correctForScada[boilerControlNum] -= 3;
                            controller.data.setCorrectForScada(correctForScada);
                            DataIO.saveData(controller.data);
                            TemperatureCorrector.changeTpod(correctForScada);
                            Thread.sleep(2000);
                        }
                    } catch (TelegramApiException | InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    enableCallService = false;
            }
            if (update.getCallbackQuery().getData().equals("bControl")){
                    try {
                        Message message2 = execute(Messages.chooseBoilerKeyboard(update.getCallbackQuery().getMessage().getChatId().toString()));
                        Thread.sleep(2000);
                    } catch (TelegramApiException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    enableCallService=false;
            }
            if (update.getCallbackQuery().getData().contains("boiler")){
                try {
                    boilerControlNum=extractBoilerControlNum(update.getCallbackQuery().getData());
                    Message message2 = execute(Messages.controlKeyboard(String.valueOf(clientsId.get(0))));
                    Thread.sleep(4000);
                    Message message3 = execute(Messages.controlKeyboard(String.valueOf(clientsId.get(1))));
                    Thread.sleep(2000);
                } catch (TelegramApiException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                enableCallService=false;
            }
        }
    }
    @Override
    public String getBotUsername() {
        return "@BoilerControlAN_bot";
    }
    Tokens tokens=new Tokens();
    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return tokens.getKey1();
    }

    public void stop() {
        timer.cancel();
        monitorThread2.interrupt();
        timer=null;
        monitorThread2=null;
        System.gc();
    }
    int numErrBoiler=0;
    public boolean arraysEquals(String[] a1, String[] a2, String[] a3){
        boolean allEquals=true;
        for (int i = 0; i < a1.length; i++) {
         if(!a1[i].equals(a2[i]))  {allEquals=false;numErrBoiler=i;}
         if(!a1[i].equals(a3[i]))  {allEquals=false;numErrBoiler=i;}
         if(!a2[i].equals(a3[i]))  {allEquals=false;numErrBoiler=i;}
        }
        return allEquals;
    }
    public int extractBoilerControlNum(String data) {
        Pattern pattern = Pattern.compile("boiler(\\d+)");
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return -1;
        }
    }
    public int[] getCorrectForScada() {
        return correctForScada;
    }
    public void resetAvary(){
        for (int i = 0; i < errorsArray.length; i++) {
            errorsArray[i]=false;
            secondAttempt[i]=false;
        }
        checkForAvary=true;
    }
    public void setCorrectForScada(int[] arrForCorrectScada) throws IOException {
        int[] boilerCompTable={0, 1, 2, 3, 7, 9, 10, 11};
        int correctIndex = -1;
        int correct = -1;
        for (int i = 0; i < arrForCorrectScada.length; i++) {
            if(arrForCorrectScada[i]!=0){
                correctIndex = i;
                correct = arrForCorrectScada[i];
                break;
            }
        }
        for (int i = 0; i < boilerCompTable.length; i++) {
            if (boilerCompTable[i]==correctIndex){
                correctForScada[i]+=correct;
                break;
            }
        }
        controller.data.setCorrectForScada(correctForScada);
        DataIO.saveData(controller.data);
        TemperatureCorrector.changeTpod(correctForScada);
    }
    public  void setCorrectFromUsers(int[] arrCorrectFromUsers){
        for (int i = 0; i < arrCorrectFromUsers.length; i++) {
            if(arrCorrectFromUsers[i]!=0) {
                correctFromUsers[i]+=arrCorrectFromUsers[i];
            }
        }
    }
}

