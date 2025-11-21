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

import com.revature.P0AlveyN.entity.Transaction;
import com.revature.P0AlveyN.entity.TransactionType;
import com.revature.P0AlveyN.util.DatabaseConnection;

public class TransactionRepository {
    
    // Saves a transaction to the database
    public Transaction save(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_date, vendor, amount, card_last_four, type, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(transaction.getTransactionDate()));
            pstmt.setString(2, transaction.getVendor());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getCardLastFour());
            pstmt.setInt(5, transaction.getType());
            pstmt.setString(6, transaction.getDescription());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
        }
        
        return transaction;
    }

    // Map a result set row to a transaction object, similar to mapping to a user object from the user entity
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
        transaction.setVendor(rs.getString("vendor"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setCardLastFour(rs.getString("card_last_four"));
        transaction.setType(rs.getInt("type"));
        transaction.setDescription(rs.getString("description"));
        return transaction;
    }

    // Finds a transaction by ID
    public Optional<Transaction> findById(Long id) throws SQLException {
        String sql = "SELECT id, transaction_date, vendor, amount, card_last_four, type, description " +
                    "FROM transactions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    // Find transactions between two dates
    public List<Transaction> findByTransactionDateBetween(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT id, transaction_date, vendor, amount, card_last_four, type, description " +
                     "FROM transactions WHERE transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(start));
            pstmt.setDate(2, java.sql.Date.valueOf(end));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        
        return transactions;
    }
    // Find all transactions
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT id, transaction_date, vendor, amount, card_last_four, type, description " +
                        "FROM transactions ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        
        return transactions;
    }
    // Update an exisiting transaction
    public void update(Transaction transaction) throws SQLException {
        String sql = "UPDATE transactions SET transaction_date = ?, vendor = ?, amount = ?, " +
                        "card_last_four = ?, type = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(transaction.getTransactionDate()));
            pstmt.setString(2, transaction.getVendor());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getCardLastFour());
            pstmt.setString(5, TransactionType.name(transaction.getType()));
            pstmt.setString(6, transaction.getDescription());
            pstmt.setLong(7, transaction.getId());
            
            pstmt.executeUpdate();
        }
    }
}
