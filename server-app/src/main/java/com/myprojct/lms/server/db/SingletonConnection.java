package com.myprojct.lms.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SingletonConnection {

    private static SingletonConnection INSTANCE ;
    private Connection connection;


    private SingletonConnection() {

        Properties properties = new Properties();
        try {
//            properties.load(getClass().getResourceAsStream("/application.properties"));
            properties.load(getClass().getResourceAsStream("/application.properties"));

            String url = properties.getProperty("app.url");
            String username = properties.getProperty("app.username");
            String password = properties.getProperty("app.password");

            connection = DriverManager.getConnection(url, username, password);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static SingletonConnection getInstance() {
        return INSTANCE == null ? INSTANCE = new SingletonConnection() : INSTANCE;
    }

    public Connection getConnection() {
        return connection;
    }



    

}
