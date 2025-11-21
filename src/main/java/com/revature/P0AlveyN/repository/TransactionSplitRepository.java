package com.revature.P0AlveyN.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.revature.P0AlveyN.entity.TransactionSplit;
import com.revature.P0AlveyN.util.DatabaseConnection;

public class TransactionSplitRepository {
    
    // Save a transaction split into the DB
    public TransactionSplit save(TransactionSplit split) throws SQLException {
        String sql = "INSERT INTO transaction_splits (transaction_id, user_id, share_amount) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, split.getTransactionId());
            pstmt.setLong(2, split.getUserId());
            pstmt.setBigDecimal(3, split.getShareAmount());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction split failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    split.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating transaction split failed, no ID obtained.");
                }
            }
        }
        
        return split;
    }
}
