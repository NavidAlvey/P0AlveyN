package com.revature.P0AlveyN.entity;

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
    

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setLastFourDigits(String lastFourDigits) {
        if (lastFourDigits == null || lastFourDigits.length() != 4) {
            throw new IllegalArgumentException("Invalid last four digits of card number");
        }
        this.lastFourDigits = lastFourDigits;
    }

    public void setPrimaryCardholder(boolean primaryCardholder) {
        if (primaryCardholder == true && !name.equalsIgnoreCase("Alvey")) {
            throw new IllegalArgumentException("Only Alvey can be the primary cardholder");
        }
        this.primaryCardholder = primaryCardholder;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastFourDigits='" + lastFourDigits + '\'' +
                ", primaryCardholder=" + primaryCardholder +
                '}';
    }
}
