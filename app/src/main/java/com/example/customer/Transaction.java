package com.example.customer;

import java.util.Date;

public class Transaction {
    private String transactionId;
    private String userID;
    private String description;
    private String amount;
    private Date transactionDateTime;

    // Constructors
    public Transaction(String transactionId, String userID, String description, String amount) {
        this.transactionId = transactionId;
        this.userID = userID;
        this.description = description;
        this.amount = amount;
        this.transactionDateTime = new Date();
    }

    public Transaction() {
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(Date transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    // Other methods if needed

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", userID='" + userID + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", transactionDateTime=" + transactionDateTime +
                '}';
    }
}
