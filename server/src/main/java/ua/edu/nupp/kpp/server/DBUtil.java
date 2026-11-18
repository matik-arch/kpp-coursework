package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/student_projects?useUnicode=true&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        String url = envOrDefault("DB_URL", DEFAULT_URL);
        String user = envOrDefault("DB_USER", DEFAULT_USER);
        String password = envOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
