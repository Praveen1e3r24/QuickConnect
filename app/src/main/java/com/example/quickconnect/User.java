package com.example.quickconnect;

public class User {

    private String userId;
    private String email;
    private String FirstName;
    private String LastName;

    private String fullName;

    private String userType;

    private String phonenumber;

    private String address;


    public User(String userId, String email, String firstName, String lastName, String phonenumber, String address) {
        this.userId = userId;
        this.email = email;
        FirstName = firstName;
        LastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.phonenumber = phonenumber;
        this.address = address;
    }

    public User() {

    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
