/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author 2014130020
 */
public class MySQLConnect {

    static MySQLConnect instance;

    public static MySQLConnect getInstance() {
        if (instance == null) {
            ClassLoader classLoader = MySQLConnect.class.getClassLoader();

            Properties properties = new Properties();
            try (InputStream inputStream = classLoader.getResourceAsStream("config.properties")) {
                properties.load(inputStream);
            } catch (IOException ignored) {
            }

            instance = new MySQLConnect(properties);
        }
        return instance;
    }

    private Connection connection;

    private final Properties properties;

    private MySQLConnect(Properties properties) {
        this.properties = properties;
    }

    public void start() {
        try {
            try {
                Class.forName(properties.getProperty("db.driver"));
            } catch (ClassNotFoundException ex) {
            }

            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
        }
        if (connection == null) {
            System.err.println("Connection failed");
        }
    }

    public ResultSet query(String sql) {
        try {
            return connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
        }
        return null;
    }

    public PreparedStatement statement(String sql) {
        try {
            return (connection.prepareStatement(sql));
        } catch (SQLException e) {
        }
        return null;
    }

    public void terminate() {
        try {
            connection.close();
        } catch (SQLException e) {
        }
    }
}
