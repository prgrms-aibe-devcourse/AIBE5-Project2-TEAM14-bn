package com.aiegoo.comicrental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing MySQL JDBC connections using a static factory method.
 * Uses DriverManager to obtain connections and includes basic logging.
 */
public class DBConnectionUtil {
    
    private static final String URL = "jdbc:mysql://localhost:3306/comic_rental?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "ChangeMeRoot!";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[DBConnectionUtil] JDBC driver loaded.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnectionUtil] MySQL JDBC driver not found in classpath.");
        }
    }
    
    // Private constructor to prevent instantiation
    private DBConnectionUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Obtains a new database connection.
     * 
     * @return a Connection object to the comic_rental database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DBConnectionUtil] Connection established successfully to comic_rental database.");
            return conn;
        } catch (SQLException e) {
            System.err.println("[DBConnectionUtil] Failed to establish connection: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Closes the given connection gracefully.
     * 
     * @param conn the Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("[DBConnectionUtil] Connection closed successfully.");
            } catch (SQLException e) {
                System.err.println("[DBConnectionUtil] Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Register a shutdown hook to ensure any open connections are cleaned up.
     * This method can be called at application startup.
     */
    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[DBConnectionUtil] Shutdown hook invoked - cleaning up resources.");
            // If using a connection pool, close it here
            // For simple DriverManager connections, individual connections should be closed in finally blocks
        }));
    }
}
