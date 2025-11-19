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

    // Getter
    public static int getBalanceUpdate(int type) {
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
}