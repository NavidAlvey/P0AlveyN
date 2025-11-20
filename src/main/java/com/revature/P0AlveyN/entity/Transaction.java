package com.revature.P0AlveyN.entity;

import java.math.BigDecimal;
import java.time.LocalDate;


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


}
