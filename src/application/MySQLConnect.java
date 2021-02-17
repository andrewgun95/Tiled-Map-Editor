/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.sql.*;

/**
 *
 * @author 2014130020
 */
public class MySQLConnect {

    static MySQLConnect instance;

    public static MySQLConnect getInstance() {
        if (instance == null) {
            instance = new MySQLConnect();
        }
        return instance;
    }
    
    private Connection connection;

    private MySQLConnect() {
    }

    public void start() {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
            }

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/UAS2017A_2014130020","root","andregokil");
        } catch (SQLException e) {
        }
        if (connection == null) {
            System.out.println("Gagal koneksi");
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
