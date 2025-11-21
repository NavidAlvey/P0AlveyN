package com.revature.P0AlveyN.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/expense_tracker";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    
    /**
     * Gets a connection to the PostgreSQL database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    // Set up a connection to the PostgreSQL DB
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
    }
    
    
    // Close connection
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}

