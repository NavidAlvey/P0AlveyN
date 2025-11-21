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

    // Map result set row to a transactionSplit object
    private TransactionSplit mapResultSetToSplit(ResultSet rs) throws SQLException {
        TransactionSplit split = new TransactionSplit();
        split.setId(rs.getLong("id"));
        split.setTransactionId(rs.getLong("transaction_id"));
        split.setUserId(rs.getLong("user_id"));
        split.setShareAmount(rs.getBigDecimal("share_amount"));
        return split;
    }

    // Find a split by ID
    public Optional<TransactionSplit> findById(Long id) throws SQLException {
        String sql = "SELECT id, transaction_id, user_id, share_amount FROM transaction_splits WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSplit(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    // Find all splits for a transaction
    public List<TransactionSplit> findByTransactionId(Long transactionId) throws SQLException {
        String sql = "SELECT id, transaction_id, user_id, share_amount FROM transaction_splits WHERE transaction_id = ?";
        List<TransactionSplit> splits = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    splits.add(mapResultSetToSplit(rs));
                }
            }
        }
        
        return splits;
    }

    // Find all splits for a user
    public List<TransactionSplit> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT id, transaction_id, user_id, share_amount FROM transaction_splits WHERE user_id = ?";
        List<TransactionSplit> splits = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    splits.add(mapResultSetToSplit(rs));
                }
            }
        }
        
        return splits;
    }

    // Find splits for transactions within a date range
    public List<TransactionSplit> findByTransactionDateBetween(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT ts.id, ts.transaction_id, ts.user_id, ts.share_amount " +
                     "FROM transaction_splits ts " +
                     "JOIN transactions t ON ts.transaction_id = t.id " +
                     "WHERE t.transaction_date BETWEEN ? AND ? " +
                     "ORDER BY t.transaction_date DESC";
        List<TransactionSplit> splits = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(start));
            pstmt.setDate(2, java.sql.Date.valueOf(end));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    splits.add(mapResultSetToSplit(rs));
                }
            }
        }
        
        return splits;
    }
}
