package com.dmitrijch.bot.config;

import com.dmitrijch.bot.telegrambot.MyBot;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfiguration {

    private static final String CONFIG_FILE = "config.properties";

    public String getBotUsername() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                return null;
            }
            prop.load(input);
            return prop.getProperty("bot.username");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBotToken() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                return null;
            }
            prop.load(input);
            return prop.getProperty("bot.token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}