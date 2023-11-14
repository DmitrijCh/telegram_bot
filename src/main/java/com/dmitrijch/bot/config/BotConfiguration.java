package com.dmitrijch.bot.config;

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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}