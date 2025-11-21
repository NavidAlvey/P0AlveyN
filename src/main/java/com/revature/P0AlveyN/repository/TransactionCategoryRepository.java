package com.revature.P0AlveyN.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.revature.P0AlveyN.entity.Category;
import com.revature.P0AlveyN.util.DatabaseConnection;

public class TransactionCategoryRepository {
    
    // Associate a category with a transaction
    public void addCategoryToTransaction(Long transactionId, Long categoryId) throws SQLException {
        String sql = "INSERT INTO transaction_categories (transaction_id, category_id) VALUES (?, ?) " +
                     "ON CONFLICT DO NOTHING";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, transactionId);
            pstmt.setLong(2, categoryId);
            pstmt.executeUpdate();
        }
    }
    
    // Get all categories for a transaction
    public List<Category> findCategoriesByTransactionId(Long transactionId) throws SQLException {
        String sql = "SELECT c.id, c.name " +
                     "FROM categories c " +
                     "JOIN transaction_categories tc ON c.id = tc.category_id " +
                     "WHERE tc.transaction_id = ? " +
                     "ORDER BY c.name";
        List<Category> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getLong("id"));
                    category.setName(rs.getString("name"));
                    categories.add(category);
                }
            }
        }
        
        return categories;
    }
}

