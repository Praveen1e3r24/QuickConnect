package com.example.quickconnect;

import com.example.customer.Transaction;

import java.util.List;

public class Customer extends User {

    private List<BankAccount> bankAccounts;
    private List<Card> cards;

    private List<Transaction> transactions;



    public Customer(){
        super();
    }; // Default constructor


    public Customer(User user, List<BankAccount> bankAccounts, List<Card> cards,List<Transaction> transaction) {
        super(user.getUserId(), user.getEmail(), user.getFirstName(), user.getLastName(),user.getPhonenumber(), user.getAddress());
        this.setUserType("Customer");
        this.bankAccounts = bankAccounts;
        this.cards = cards;
        this.transactions = transaction;
    }

    public Customer(User user, List<BankAccount> bankAccounts, List<Card> cards) {
        super(user.getUserId(), user.getEmail(), user.getFirstName(), user.getLastName(),user.getPhonenumber(), user.getAddress());
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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
