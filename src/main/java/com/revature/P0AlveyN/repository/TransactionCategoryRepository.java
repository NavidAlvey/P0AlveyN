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

}

