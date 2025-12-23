package com.ulp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {
   public static String getConnectionString() {
       return "jdbc:mysql://172.19.185.115:3306/ulpdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
   }
   public static String getUsername() {
       return "devplogin";
   }
   public static String getPassword() {
       return "devplogin";
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

// // 本地数据库配置
// public class DBHelper {
//     private static final String URL = "jdbc:mysql://localhost:3306/learning_platform?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
//     private static final String USER = "kerlap"; // 本地数据库用户名（自己改）
//     private static final String PASSWORD = "123456";
//     // 加载驱动
//     static {
//         try {
//             Class.forName("com.mysql.cj.jdbc.Driver");
//         } catch (ClassNotFoundException e) {
//             e.printStackTrace();
//         }
//     }
//     // 建立连接对象
//     public static Connection getConnection() throws SQLException {
//         return java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
//     }
//     // 关闭所有对象
//     public static void closeConnection(Connection conn) {
//         if (conn != null) {
//             try {
//                 conn.close();
//             } catch (SQLException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
// }