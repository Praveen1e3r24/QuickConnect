package com.example.customer;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String userID;
    private String description;
    private double amount;
    private LocalDateTime transactionDateTime;

    // Constructors
    public Transaction(String transactionId, String userID, String description, double amount, LocalDateTime transactionDateTime) {
        this.transactionId = transactionId;
        this.userID = userID;
        this.description = description;
        this.amount = amount;
        this.transactionDateTime = transactionDateTime;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(LocalDateTime transactionDateTime) {
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
