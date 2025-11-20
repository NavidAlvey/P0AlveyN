package com.revature.P0AlveyN.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Transaction {

    //Fields
    private Long id;
    private LocalDate transactionDate;
    private String vendor;
    private BigDecimal amount;
    private String cardLastFour;
    private int type;
    private String description;

    // Constructors
    public Transaction() {}

    public Transaction(LocalDate transactionDate, String vendor, BigDecimal amount, String cardLastFour, int type, String description) {
        this.transactionDate = transactionDate;
        this.vendor = vendor;
        this.amount = amount;
        this.cardLastFour = cardLastFour;
        this.type = type;
        this.description = description;
    }

    // Methods

    // GETTERS
    public Long getId() {
        return id;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public String getVendor() {
        return vendor;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    // SETTERS
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    // Same checks as from the user class
    // if there is a duplicate return the unique identifier
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
