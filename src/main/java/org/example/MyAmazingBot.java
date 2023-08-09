package org.example;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import io.github.aminovmaksim.chatgpt4j.ChatGPTClient;
import io.github.aminovmaksim.chatgpt4j.model.ChatRequest;
import io.github.aminovmaksim.chatgpt4j.model.ChatResponse;

import org.checkerframework.checker.units.qual.A;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import parcers.OpenWeatherMapJsonParser;
import parcers.WeatherParser;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.image.CreateImageRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyAmazingBot extends TelegramLongPollingBot {
    public MyAmazingBot() throws IOException {

      clientsId.add(1102774002L);
      clientsId.add(6290939545L);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId("@BoilersAnadyr");
        ActualParams actualParams = new ActualParams(currentDir);

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
                // После выполнения действий, этот метод снова будет вызван через 10 минут
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId("@BoilersAnadyr");
                ActualParams actualParams = null;
                try {
                    actualParams = new ActualParams(currentDir);
                    LocalTime currentTime = LocalTime.now();
                    String formattedTime = currentTime.format(formatter);
                sendMessage.setText(formattedTime+"\n"+getCurrentParamsText(actualParams,errorsArray));
                sendMessage.setParseMode("Markdown");
                    Message message = execute(sendMessage);
                    Thread.sleep(1000);
                    SendMessage sendMessageTest = new SendMessage();
                    sendMessageTest.setChatId(clientsId.get(0));
                    sendMessageTest.setText(boilerManager.getDevAndCurrent());
                    Message message1 = execute(sendMessageTest);
                    Thread.sleep(1800);
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
     }
   BoilerManager boilerManager = new BoilerManager(12);
    static volatile boolean keepRunning = true;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
     private Integer[] avaryMessageID=new Integer[2];
    private Timer timer = new Timer();
    Integer[] avary2MessageID= new Integer[2];
    Integer[] avary3MessageID=new Integer[2];
    private long chatId = -1;
    private boolean [] errorsArray = {false, false, false, false, false, false, false, false, false, false, false, false};
    static volatile Integer messageId = -1;
    List<Long> clientsId = new ArrayList<>();
    Object lock= new Object();
    public boolean checkForAvary =true;
    Semaphore semaphore = new Semaphore(0);
    String currentDir = Paths.get("").toAbsolutePath().toString()+"/actualparams.txt";

    Thread monitorThread2 = new Thread(()->{
        while (keepRunning){
            try {
                ActualParams actualParams2 = null;
                try {
                    actualParams2 = new ActualParams(currentDir);
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
                        boilerManager.addTemperature(i,Float.parseFloat(actualParams2.getTPod()[i]));
                        if (errorsArray[i]){
                            continue;
                        }
                        if ((boilerManager.isTemperatureAnomaly(i,Float.parseFloat(actualParams2.getTPod()[i])))&&(!actualParams2.getPVx()[i].equals("-1000"))) {
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
    private void sendAttention(int boilerIndex, String comment) throws TelegramApiException {
        String currentDir = Paths.get("").toAbsolutePath().toString()+"/actualparams.txt";
        checkForAvary=false;
        ActualParams actualParams = null;
        errorsArray[boilerIndex]=true;
        try {
            actualParams = new ActualParams(currentDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Устанавливаем ID чата и текст сообщения
        for (int i = 0; i < clientsId.size() ; i++) {
            SendMessage message1 = new SendMessage();
            message1.setChatId(clientsId.get(i));//тут чат id
            message1.setText("Аварийное значение!"+"\n"+"Общие параметры на момент аварии:"+"\n"+boilerNames[boilerIndex]+"\n"
                    +"\uD83D\uDD25 Температура уходящей воды: " + actualParams.getTPod()[boilerIndex] +" °C"+"\n"
                    + "⚖️\uD83D\uDCA8 Давление в системе отопления: " + actualParams.getPVx()[boilerIndex]+" МПа"+"\n"+comment);
             // Отправляем сообщение
            Message message = execute(message1);
            avaryMessageID[i] = message.getMessageId();
            Message message2 = execute(avaryKeyboard(String.valueOf(clientsId.get(i))));
            avary3MessageID[i]= message2.getMessageId();
            // Возвращаемся к предыдущему экрану клавиатуры

        }
    }
    private WeatherParser weatherParser=new OpenWeatherMapJsonParser();

    public String[] boilerNames = {
            "Котельная «Склады Мищенко»",                   //t улицы кот№1 Склады Мищенко
            "Котельная «Выставка Ендальцева»",              //t улицы кот№2 Ендальцев         (датчик на базе)
            "Котельная «ЧукотОптТорг»",                     //t улицы кот№3 ЧукотОптТорг      (датчик на базе)
            "Котельная «ЧСБК новая»",                     //t улицы кот№4 "ЧСБК Новая"
            "Котельная «Офис СВТ»",               //t улицы кот№5 офис "СВТ"
            "Котельная «Общежитие на Южной»",                     //t улицы кот№6 общежитие на Южной
            "Котельная «Офис ЧСБК»",                   //t улицы кот№7 офис ЧСБК
            "Котельная «Рынок»",                     //t улицы кот№8 "Рынок"
            "Котельная «Макатровых»",                     //t улицы кот№9 Макатровых
            "Котельная ДС «Сказка»",                     //t  улицы кот№10  "Д/С Сказка"
            "Котельная «Полярный»",                     //t улицы  кот№11 Полярный
            "Котельная «Департамент»"                     //t улицы  кот№12 Департамент
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
        // Return bot username
        // If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        //return "@BoilerControlAN_bot";
        return "@BoilerControlAN_bot";
    }

    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return "your_token";
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
    public SendMessage backKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Обратно к котельным:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        InlineKeyboardButton sensorView = new InlineKeyboardButton();
        sensorView.setText("↩️Назад");
        sensorView.setCallbackData("backToBoilers");
        buttonList.add(sensorView);
        rowList.add(buttonList);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
    public SendMessage sensorViewKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите котельную:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();



        for(int i = 0; i < boilerNames.length; i++) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            InlineKeyboardButton boilerControl = new InlineKeyboardButton();
            boilerControl.setText(boilerNames[i]);
            boilerControl.setCallbackData("boiler" + i);
            buttonList.add(boilerControl);
            rowList.add(buttonList);
        }

        InlineKeyboardButton backControl = new InlineKeyboardButton();
        backControl.setText("↩️Назад");
        backControl.setCallbackData("backToGeneral");
        InlineKeyboardButton showAllBoilers = new InlineKeyboardButton();
        showAllBoilers.setText("Показать все");
        showAllBoilers.setCallbackData("showAllBoilers");
        List<InlineKeyboardButton> backButtonList = new ArrayList<>();
        List<InlineKeyboardButton> showAllBoilersList=new ArrayList<>();
        showAllBoilersList.add(showAllBoilers);
        backButtonList.add(backControl);
        rowList.add(backButtonList);
        rowList.add(showAllBoilersList);
        inlineKeyboardMarkup.setKeyboard(rowList);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
}

