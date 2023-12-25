package com.example.jokebot.bot;

import com.example.jokebot.configuration.ExchangeRatesBotConfiguration;
import com.example.jokebot.exception.ServiceException;
import com.example.jokebot.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {


    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String HELP = "/help";
    @Autowired
    private ExchangeRatesService exchangeRatesService;



    public ExchangeRatesBot(@Value("${bot.token}")String botToken){
            super(botToken);
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/usd", "get an exchange rate of dollar"));
        listofCommands.add(new BotCommand("/eur", "get an exchange rate of dollar"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
        }

    }
    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()){
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        switch (message){
            case START -> {
            startMessage(chatId,update.getMessage().getChat().getFirstName());
            }
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case HELP -> helpCommand(chatId);
            case "Привет" -> sendMessage(chatId,"Привет!");
            default -> sendMessage(chatId,"Не удалось распознать комманду");
        }
    }
    public void startMessage(Long chatId, String userName){
        var text = """
  Добро пожаловать, %s!
  Здесь Вы можете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
     Для этого воспользуйтесь командами:
     /usd - курс доллара
     /eur - курс евро
     
     Дополнительные команды:
     /help - получение справки                          
                """;
        var formattedText = String.format(text,userName);
        sendMessage(chatId,formattedText);
    }

    public void helpCommand(Long chatId){
        var text = """
                Для получения текующих курсов валют воспользуйтесь коммандами: 
                /usd - курс доллара 
                /eur - курс евро 
                """;
        sendMessage(chatId,text);
    }
    private void usdCommand(Long chatId)  {
        String formattedText;
        try{
            var usd = exchangeRatesService.getUSDExchangeRate();
            var text = "Курс доллара на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(),usd);
        }catch(ServiceException | IOException e){
        formattedText = "Impossible to get current usd currency. Try later";
        }
        sendMessage(chatId,formattedText);
    }
    private void eurCommand(Long chatId)  {
        String formattedText;
        try{
            var eur = exchangeRatesService.getEURExchangeRate();
            var text = "Курс евро на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(),eur);
        }catch(ServiceException | IOException e){
            formattedText = "Impossible to get current eur currency. Try later";
        }
        sendMessage(chatId,formattedText);
    }


    @Override
    public String getBotUsername() {
        return "CurrencyBot";
    }
    private void sendMessage(Long chatId, String text){
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr,text);
        try{
            execute(sendMessage);
        }catch(TelegramApiException e){

        }
    }
}
