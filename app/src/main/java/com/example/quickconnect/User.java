package com.example.quickconnect;

public class User {

      String usertype,FirstName,LastName,password;

    public User() {
    }

    public User(String usertype, String firstName, String lastName, String password) {
        this.usertype = usertype;
        FirstName = firstName;
        LastName = lastName;
        this.password = password;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
