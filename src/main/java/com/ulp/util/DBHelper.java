package com.ulp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {
    public static String getConnectionString() {
        return "jdbc:mysql://localhost:3306/learning_platform?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }
    public static String getUsername() {
        return "root";
    }
    public static String getPassword() {
        return "1111";
    }
    public static String getDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(getDriverClassName());
            connection = DriverManager.getConnection(getConnectionString(), getUsername(), getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
