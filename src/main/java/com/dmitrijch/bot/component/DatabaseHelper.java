package com.dmitrijch.bot.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class DatabaseHelper {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    public String getCountryNameByCode(String regionIndex) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT city FROM region WHERE index = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, regionIndex);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("city");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}