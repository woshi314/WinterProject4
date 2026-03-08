package com.java.demo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtils {
    private static Properties prop = new Properties();
    static {
        try {
            ClassLoader classLoader = JDBCUtils.class.getClassLoader();
            prop.load(classLoader.getResourceAsStream("application.properties"));

            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                prop.getProperty("url"),
                prop.getProperty("user"),
                prop.getProperty("password")
        );
    }

    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
