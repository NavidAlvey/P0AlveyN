package com.revature.P0AlveyN.entity;
/*
 * This class will determine how the balance changes based on the transaction type
 * PURCHASE: increase balance
 * REFUND: decrease balance
 * PAYMENT: decrease balance
 * 
 * Get the type of transaction and return what kind of balance update
 */
public class TransactionType {

    // Fields
    public static final int PURCHASE = 1;
    public static final int REFUND = 2;
    public static final int PAYMENT = 3;

    // Constructor
    private TransactionType() {
        // Empty constructor makes the class non-instantiable
    }

    // Method

    // Getters

    // Multiplies the transaction by either +1 or -1 to identify whether the
    // transaction increases or decreases the balance
    public static int getMultiplier(int type) {
        switch(type) {
            case PURCHASE:
                return 1; // Increase balance
            case REFUND:
                return -1; // Decrease balance
            case PAYMENT:
                return -1; // Decrease balance
            default:
                throw new IllegalArgumentException("Invalid transaction type:" + type);
        }
    }

    //Converts a string to a transaction type constant
    public static int valueOf(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        String normalized = typeName.trim().toUpperCase();
        if ("PURCHASE".equals(normalized)) {
            return PURCHASE;
        } else if ("REFUND".equals(normalized)) {
            return REFUND;
        } else if ("PAYMENT".equals(normalized)) {
            return PAYMENT;
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + typeName);
        }
    }

    // Converts a transaction type constant (1-3) to its string name
    public static String name(int type) {
        switch(type) {
            case PURCHASE:
                return "PURCHASE";
            case REFUND:
                return "REFUND";
            case PAYMENT:
                return "PAYMENT";
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }
}