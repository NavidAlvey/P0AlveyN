package com.revature.P0AlveyN.services;

import java.sql.SQLException;
import java.util.List;

import com.revature.P0AlveyN.entity.Category;
import com.revature.P0AlveyN.repository.CategoryRepository;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Find or create a category by name
    public Category findOrCreate(String name) throws SQLException {
        return categoryRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> {
                    try {
                        return categoryRepository.save(new Category(name));
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to create category", e);
                    }
                });
    }

    // Get all categories
    public List<Category> getAll() throws SQLException {
        return categoryRepository.findAll();
    }
}
