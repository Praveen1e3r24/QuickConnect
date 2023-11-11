package com.example.quickconnect;

public class User {

      String usertype,FirstName,LastName,password,username;

    public User() {
    }

    public User(String usertype, String firstName, String lastName, String password, String username) {
        this.usertype = usertype;
        FirstName = firstName;
        LastName = lastName;
        this.password = password;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

