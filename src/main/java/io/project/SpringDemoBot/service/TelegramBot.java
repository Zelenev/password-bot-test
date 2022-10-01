package io.project.SpringDemoBot.service;

import io.project.SpringDemoBot.config.BotConfig;
import io.project.SpringDemoBot.model.User;
import io.project.SpringDemoBot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.ChatPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;

    final static String HELP_TEXT = "This bot was created to studies!\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /generate to create user data\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /deletedata to delete data about yourself\n\n" +
            "Type /settings to set your preferences\n\n" +
            "Type /help to see this message again";

    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<BotCommand>();
        listOfCommands.add(new BotCommand("/start","get a welcome message"));
        listOfCommands.add(new BotCommand("/generate","generate user data"));
        listOfCommands.add(new BotCommand("/mydata","get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata","delete my data"));
        listOfCommands.add(new BotCommand("/help","how to use this bot"));
        listOfCommands.add(new BotCommand("/settings","set your preferences"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(),null));
        }
        catch (TelegramApiException e){
           log.error("Error setting bot's command list" + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() { return config.getBotName(); }

    @Override
    public String getBotToken() { return config.getToken(); }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":

                    registerUser(update.getMessage());
                    sendMessage(chatId,"Я сгенерирую тебе Email и пароль. Напиши команду /generate");
                    //startCommandReceived(chatId, update.getMessage().getChat().getFirstName(),update.getMessage().getChat().getLastName());
                    break;
                case "/generate":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName(),update.getMessage().getChat().getLastName());
                    break;
                case "/help":
                    sendMessage(chatId,HELP_TEXT);
                    break;
               default: sendMessage(chatId, "Нет такого варианта!");
            }
        }
    }


    private void registerUser(Message msg) {
        if(userRepository.findById(msg.getChatId()).isEmpty()){

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("User saved: " + user);
        }
    }

    private void startCommandReceived(long chatId, String firstName, String lastName){
      Email email = new Email(firstName,lastName);
      String answer = email.getInfo();
      log.info("Replied to user: " + firstName);
      sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        }
        catch (TelegramApiException e){
           log.error("Error occurred: " + e.getMessage());
        }
    }
}
