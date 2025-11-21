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

}
