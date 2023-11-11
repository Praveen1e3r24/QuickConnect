package com.example.quickconnect;

public class Card {
    private String cardType;
    private String cardTypeName;

    private String expiryDate;
    private double cardLimit;
    private double waiverFees;

    public Card() {
    }

    public Card(String cardTypeName, String cardType, String expiryDate, double cardLimit) {
        this.cardType = cardType;
        this.cardTypeName = cardTypeName;
        this.expiryDate = expiryDate;
        this.cardLimit = cardLimit;
    }

    public Card(String cardTypeName, String cardType,  String expiryDate, double cardLimit, double waiverFees) {
        this.cardType = cardType;
        this.cardTypeName = cardTypeName;
        this.expiryDate = expiryDate;
        this.cardLimit = cardLimit;
        this.waiverFees = waiverFees;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public double getWaiverFees() {
        return waiverFees;
    }

    public void setWaiverFees(Double waiverFees) {
        this.waiverFees = waiverFees;
    }

    public double getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(double cardLimit) {
        this.cardLimit = cardLimit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
