package com.revature.P0AlveyN.entity;

import java.util.Objects;

public class User {

    // Fields
    private Long id;
    private String name;
    private String lastFourDigits;
    private boolean primaryCardholder;

    // Constructor
    public User() {
       
    }

    public User(String name, String lastFourDigits, boolean primaryCardholder) {
        setName(name);
        setLastFourDigits(lastFourDigits);
        this.primaryCardholder = primaryCardholder;
    }

    // Methods

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public boolean isPrimaryCardholder() {
        return primaryCardholder;
    }
    

    // SETTER FOR ID
    public void setId(Long id) {
        this.id = id;
    }

    // SETTER FOR NAME
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name.trim();
    }

    // SETTER FOR LAST FOUR DIGITS
    public void setLastFourDigits(String lastFourDigits) {
        if (lastFourDigits == null || lastFourDigits.length() != 4) {
            throw new IllegalArgumentException("Invalid last four digits of card number");
        }
        this.lastFourDigits = lastFourDigits;
    }

    // SETTER FOR PRIMARY CARDHOLDER
    public void setPrimaryCardholder(boolean primaryCardholder) {
        if (primaryCardholder == true && !name.equalsIgnoreCase("Alvey")) {
            throw new IllegalArgumentException("Only Alvey can be the primary cardholder");
        }
        this.primaryCardholder = primaryCardholder;
    }

    // Create an ordered output for when user is created
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastFourDigits='" + lastFourDigits + '\'' +
                ", primaryCardholder=" + primaryCardholder +
                '}';
    }

    // What to do when two users appear identical?
    @Override
    public boolean equals(Object o) { 
        if (this == o) return true; // If the objects are the same, return true
        if (!(o instanceof User)) return false; // If the objects are not the same type, return false
        User user = (User) o;
        return Objects.equals(id, user.id); // If the ids are the same, return true
    }

    // What to do when a user is printed?
    @Override
    public int hashCode() {
        return Objects.hash(id); // Return the unique hash code of the id
    }
}
