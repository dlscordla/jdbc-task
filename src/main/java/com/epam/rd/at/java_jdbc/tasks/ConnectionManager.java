package com.epam.rd.at.java_jdbc.tasks;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    public static final String PATH_TO_PROPERTIES = "src/main/resources/config.properties";
    private static Connection connection = null;

    public static Connection openConnection() {

        FileInputStream fileInputStream;
        Properties properties = new Properties();

        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            properties.load(fileInputStream);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
            ConsoleMessages.messageOpen();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            if (connection == null) {
                ConsoleMessages.messageNoConnection();
                System.exit(0);
            }
        }
        return connection;
    }
}