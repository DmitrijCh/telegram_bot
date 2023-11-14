package com.dmitrijch.bot.telegrambot;

import com.dmitrijch.bot.config.BotConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class MyBot extends TelegramLongPollingBot {

    private final BotConfiguration botConfig = new BotConfiguration();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                sendErrorMessage(chatId, "Привет! Я ваш бот погоды и праздников. Чтобы узнать погоду, отправьте" +
                        " команду /weather и укажите местоположение, чтобы узнать предстоящие праздники отправьте команду /holiday.");
            } else if (messageText.startsWith("/weather")) {
                String location = messageText.substring("/weather".length()).trim();
                if (!location.isEmpty()) {
                    getWeatherAndSendResponse(chatId, location);
                } else {
                    sendErrorMessage(chatId, "Пожалуйста, укажите местоположение для получения погоды.");
                }
            } else if (messageText.startsWith("/holiday")) {
                try {
                    String apiUrl = "https://date.nager.at/Api/v2/NextPublicHolidaysWorldwide";
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Accept", "application/json");
                    headers.set("Api-Key", "LzQs5BIjqN70Rg2ZQbBADQlwTX4tToRV");

                    HttpEntity<String> entity = new HttpEntity<>(headers);

                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

                    String responseBody = responseEntity.getBody();

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode holidays = objectMapper.readTree(responseBody);

                    sendHolidaysList(chatId, holidays);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void getWeatherAndSendResponse(long chatId, String location) {
        String apiUrl = "http://api.weatherstack.com/current?access_key=db91fe4694560cbe3263153f60af3fcb&query=" + location;

        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(apiUrl, String.class);

        if (jsonResponse != null) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if (jsonObject.has("current")) {
                JsonObject current = jsonObject.getAsJsonObject("current");

                if (current.has("temperature")) {
                    double temperature = current.get("temperature").getAsDouble();

                    String weatherMessage = "Погода в месте " + location + ":\n";
                    weatherMessage += "Температура: " + temperature + "°C\n";

                    SendMessage message = new SendMessage(String.valueOf(chatId), weatherMessage);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    sendErrorMessage(chatId, "Данные о температуре отсутствуют.");
                }
            }
        }
    }

    public void sendHolidaysList(long chatId, JsonNode holidays) {
        StringBuilder messageText = new StringBuilder("Список предстоящих праздников:\n");
        for (JsonNode holiday : holidays) {
            messageText.append("Дата: ").append(holiday.get("date").asText()).append("\n");
            messageText.append("Местное название: ").append(holiday.get("localName").asText()).append("\n");
            messageText.append("Международное название: ").append(holiday.get("name").asText()).append("\n");
            messageText.append("Страна: ").append(holiday.get("countryCode").asText()).append("\n\n");
        }

        SendMessage message = new SendMessage(String.valueOf(chatId), messageText.toString());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendErrorMessage(long chatId, String errorMessage) {
        SendMessage message = new SendMessage(String.valueOf(chatId), errorMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}