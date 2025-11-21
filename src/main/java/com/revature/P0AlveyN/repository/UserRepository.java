package com.revature.P0AlveyN.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.revature.P0AlveyN.entity.User;
import com.revature.P0AlveyN.util.DatabaseConnection;

public class UserRepository {
    
    //Saves a user to the database using SQL commands
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users (name, last_four, primary_cardholder) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getLastFourDigits());
            pstmt.setBoolean(3, user.isPrimaryCardholder());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        
        return user;
    }

    //Maps a ResultSet row to a User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setLastFourDigits(rs.getString("last_four"));
        user.setPrimaryCardholder(rs.getBoolean("primary_cardholder"));
        return user;
    }

    // Finds a user by ID
    public Optional<User> findById(Long id) throws SQLException {
            String sql = "SELECT id, name, last_four, primary_cardholder FROM users WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setLong(1, id);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToUser(rs));
                    }
                }
            }
            
            return Optional.empty();
        }

     //Finds a user by last four digits of card
    public Optional<User> findByLastFourDigits(String lastFourDigits) throws SQLException {
        String sql = "SELECT id, name, last_four, primary_cardholder FROM users WHERE last_four = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lastFourDigits);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    // Find all users
    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, name, last_four, primary_cardholder FROM users ORDER BY name";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    // Update an existing user
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, last_four = ?, primary_cardholder = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getLastFourDigits());
            pstmt.setBoolean(3, user.isPrimaryCardholder());
            pstmt.setLong(4, user.getId());
            
            pstmt.executeUpdate();
        }
    }
    // Deletes a user by ID
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    
}
