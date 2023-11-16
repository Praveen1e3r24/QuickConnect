package com.example.quickconnect;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.customer.Transaction;

import java.util.List;

public class Customer extends User implements Parcelable {

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


    protected Customer(Parcel in) {
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
    }
}
