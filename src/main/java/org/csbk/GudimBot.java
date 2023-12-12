package org.csbk;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GudimBot extends TelegramLongPollingBot {
    public GudimBot(float[] fixedParamsGudim) throws IOException {

      clientsId.add(1102774002L);
      //clientsId.add(6290939545L);
      SendMessage sendMessage = new SendMessage();
      sendMessage.setChatId("@GudimChukotka");
      GudimParamsParser actualParams = new GudimParamsParser(currentDir);
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
                sendMessage.setChatId("@GudimChukotka");
                GudimParamsParser actualParams = null;
                try {
                    actualParams = new GudimParamsParser(currentDir);
                    LocalTime currentTime = LocalTime.now();
                    String formattedTime = currentTime.format(formatter);
                sendMessage.setText(formattedTime+"\n"+getCurrentParamsText(actualParams,errorsArray));
                sendMessage.setParseMode("Markdown");
                    Message message = execute(sendMessage);
                    Thread.sleep(1000);
                  // SendMessage sendMessageTest = new SendMessage();
                  // sendMessageTest.setChatId(clientsId.get(0));
                  // sendMessageTest.setText(boilerManager.getDevAndCurrent());
                  // Message message1 = execute(sendMessageTest);
                    Thread.sleep(1800);
                    DeleteMessage deleteMessage = new DeleteMessage("@GudimChukotka",messageId);
                    Thread.sleep(1800);
                    messageId = message.getMessageId();
                    Thread.sleep(1800);
                    execute(deleteMessage);
                    Thread.sleep(1800);
                    //TODO Сюда добавить проверку на отуствие связи, такс на 5 минут!
                } catch (TelegramApiException | InterruptedException |IOException e) {

                }
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000); // Первое значение - задержка перед первым запуском, второе - интервал между запусками
     }
   BoilerManager boilerManager = new BoilerManager(12);
    static volatile boolean keepRunning = true;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Integer[] avaryMessageID=new Integer[2];
    private Timer timer = new Timer();
    Integer[] avary2MessageID= new Integer[2];
    Integer[] avary3MessageID=new Integer[2];
    private long chatId = -1;
    private boolean [] errorsArray = {false, false, false, false, false, false, false};
    static volatile Integer messageId = -1;
    List<Long> clientsId = new ArrayList<>();
    Object lock= new Object();
    public boolean checkForAvary =true;
    float[] fixedParamsGudim;
    Semaphore semaphore = new Semaphore(0);
    String currentDir = Paths.get("").toAbsolutePath().toString()+"/gudim.txt";
    Thread monitorThread2 = new Thread(()->{
        while (keepRunning){
            try {
                GudimParamsParser actualParams2 = null;
                try {
                    actualParams2 = new GudimParamsParser(currentDir);
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

                    try {
                       if (errorsArray[0]){
                           continue;
                       }
                        String s =actualParams2.gettPodGorod() ;
                        if ((Float.parseFloat(actualParams2.gettPodGorod())<2)) { //TODO Скорректировать пределы
                            try {
                                sendAttention(0, "Проблема в температуре подачи в город!");
                                Thread.sleep(2000);
                            } catch (TelegramApiException e) {
                                continue;
                            }
                        }
                        if ((Float.parseFloat(actualParams2.getGo())<40)) { //TODO Скорректировать пределы
                            try {
                                sendAttention(1, "Отклонение в цифрах расхода");
                                Thread.sleep(2000);
                            } catch (TelegramApiException e) {
                                continue;
                            }
                        }
                        if ((Float.parseFloat(actualParams2.gettStreet())<-35)) {
                            try {
                                sendAttention(2, "Проблема в температуре подачи в город!");
                                Thread.sleep(2000);
                            } catch (TelegramApiException e) {
                                continue;
                            }
                        }
                        for (int i=0;i<actualParams2.gettNasosnaya().length;i++){
                            if ((Float.parseFloat(actualParams2.gettNasosnaya()[i])<30)) { //TODO Скорректировать пределы
                                try {
                                    sendAttention(i, "Проблема в температуре насосной (Датчики внизу)");
                                    Thread.sleep(2000);
                                } catch (TelegramApiException e) {
                                    continue;
                                }
                            }
                        }
                    }catch (NumberFormatException e){
                        continue;
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
        editMessage.setChatId("@GudimChukotka");
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
    public String getCurrentParamsText(GudimParamsParser actualParams, boolean[] errorsArray) {
        StringBuilder result = new StringBuilder();
        // Преобразование всех данных в строки
        String[][] data = {
                { "", "В гор.", "Ск.1", "Ск.2", "S" },
                { "Расход", String.valueOf(actualParams.getGo()), "-", "-", errorsArray[0] ? "🔴" : "🟢" },
                { "Тпод, °С", String.valueOf(actualParams.gettPodGorod()), String.valueOf(actualParams.gettNasosnaya()[0]),
                        String.valueOf(actualParams.gettNasosnaya()[2]), errorsArray[1] ? "🔴" : "🟢" },
                {"P, МПа", "-", String.valueOf(actualParams.gettNasosnaya()[1]),String.valueOf(actualParams.gettNasosnaya()[3]),errorsArray[1] ? "🔴" : "🟢" },
                {"t ул, °С", String.valueOf(actualParams.gettStreet()), "-", "-",errorsArray[1] ? "🔴" : "🟢" }
        };
        int[] maxLengths = new int[5];
        for (int i = 0; i < 5; i++) {
            for (String[] row : data) {
                maxLengths[i] = Math.max(maxLengths[i], (row[i] != null ? row[i].length() : 0));
            }
        }
        for (int i = 0; i < maxLengths.length; i++) {
            maxLengths[i] = Math.min(maxLengths[i] + 2, 25);  // Добавляем отступы и ограничиваем максимальную ширину
        }
        String format = "| %" + maxLengths[0] + "s | %" + maxLengths[1] + "s | %" + maxLengths[2] + "s | %" + maxLengths[3] + "s | %" + maxLengths[4] + "s \n";
        for (String[] row : data) {
            result.append(String.format(format, (Object[]) row));
        }
        result.append("\n");
        return result.toString();
    }
    private void sendAttention(int paramIndex, String comment) throws TelegramApiException {
        String currentDir = Paths.get("").toAbsolutePath().toString()+"/actualparams.txt";
        checkForAvary=false;
        GudimParamsParser actualParams = null;
        errorsArray[paramIndex]=true;
        try {
            actualParams = new GudimParamsParser(currentDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < clientsId.size() ; i++) {
            SendMessage message1 = new SendMessage();
            message1.setChatId(clientsId.get(i));//тут чат id
            message1.setText("Аварийное значение!"+"\n"+"Общие параметры на момент аварии:"+"\n"+ParamNames[paramIndex]+"\n"
                    +"\uD83D\uDD25 Температура уходящей воды: " + actualParams.getGo() +" °C"+"\n"
                    + "⚖️\uD83D\uDCA8 Давление в системе отопления: " + actualParams.gettPodGorod()+" МПа"+"\n"+comment);
             // Отправляем сообщение
            Message message = execute(message1);
            avaryMessageID[i] = message.getMessageId();
            Message message2 = execute(avaryKeyboard(String.valueOf(clientsId.get(i))));
            avary3MessageID[i]= message2.getMessageId();
        }
    }
    public String[] ParamNames = {
            "Температура подачи в город",
            "Расход воды (город)",
            "Температура улицы",
            "Температура скважина №1",
            "Давление скважина №1",
            "Температура скважина №1",
            "Давление скважина №2"
    };
    @Override
    public void onUpdateReceived(Update update) {

        Map<String, Integer> boilerIndices = new HashMap<>();
        for (int i = 0; i < 12; i++) {
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
                    execute(startKeyboard(chatId)); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        if (update.hasCallbackQuery()){
            if (update.getCallbackQuery().getData().equals("avaryReset")){
                for (int i = 0; i < errorsArray.length; i++) {
                    errorsArray[i]=false;
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

        }
    }
    @Override
    public String getBotUsername() {
        return "@GudimChukotka2_bot";
    }
    Tokens tokens;
    @Override
    public String getBotToken() {
        return tokens.getKey2();
    }
    public SendMessage startKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите действие:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList2 = new ArrayList<>();
        InlineKeyboardButton sensorView = new InlineKeyboardButton();
        sensorView.setText("\uD83C\uDF21Просмотр датчиков ");
        sensorView.setCallbackData("sensorView");
        InlineKeyboardButton boilerControl = new InlineKeyboardButton();
        boilerControl.setText("\uD83D\uDD79Управление котельными");
        boilerControl.setCallbackData("boilerControl");
        buttonList.add(sensorView);
        buttonList2.add(boilerControl);
        rowList.add(buttonList);
        rowList.add(buttonList2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
    public SendMessage avaryKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите действие:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        InlineKeyboardButton sensorView = new InlineKeyboardButton();
        sensorView.setText("Сброс аварии");
        sensorView.setCallbackData("avaryReset");
        buttonList.add(sensorView);
        rowList.add(buttonList);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
    public void stop() {
        timer.cancel();
        monitorThread2.interrupt();
        timer=null;
        monitorThread2=null;
        System.gc();
    }
}

