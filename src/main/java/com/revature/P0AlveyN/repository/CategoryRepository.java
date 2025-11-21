package com.revature.P0AlveyN.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.revature.P0AlveyN.entity.Category;
import com.revature.P0AlveyN.util.DatabaseConnection;

public class CategoryRepository {

    // Save a category to the DB
    public Category save(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getName());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }
        }
        
        return category;
    }

    // Map result set row to a category object
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        return category;
    }

    // Find a category by ID
    public Optional<Category> findById(Long id) throws SQLException {
        String sql = "SELECT id, name FROM categories WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    // Find a category by name
    public Optional<Category> findByNameIgnoreCase(String name) throws SQLException {
        String sql = "SELECT id, name FROM categories WHERE LOWER(name) = LOWER(?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    // Find all categories
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT id, name FROM categories ORDER BY name";
        List<Category> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }
        
        return categories;
    }

    // Update an exisiting category
    public void update(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setLong(2, category.getId());
            
            pstmt.executeUpdate();
        }
    }
}
