package com.example.quickconnect;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {

    private List<BankAccount> bankAccounts;
    private List<Card> cards;

    public Customer() {
    }

    public Customer(User user, List<BankAccount> bankAccounts, List<Card> cards) {
        super(user.getUserId(), user.getEmail(), user.getFirstName(), user.getLastName());
        this.setUserType("Customer");
        this.bankAccounts = bankAccounts;
        this.cards = cards;
    }

    public Customer(String userId, String email, String firstName, String lastName, ArrayList<BankAccount> bankAccounts, ArrayList<Card> cards) {
        super(userId, email, firstName, lastName);
        this.setUserType("Customer");
        this.bankAccounts = bankAccounts;
        this.cards = cards;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
