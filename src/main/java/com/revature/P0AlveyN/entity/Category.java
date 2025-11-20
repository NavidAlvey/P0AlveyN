package com.revature.P0AlveyN.entity;

import java.util.Objects;

public class Category {

    // Fields
    private Long id;
    private String name;

    // Constructors
    public Category() {}

    public Category(String name) {
        setName(name);
    }

    // Methods ez getters & setters

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be blank");
        }
        this.name = name.trim();
    }

    // need to return category name as String
    @Override
    public String toString() {
        return name;
    }

    // Same duplicate checks from Transaction & User classes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
