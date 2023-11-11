package com.example.quickconnect;
public class User {

    private String userId;
    private String email;
    private String FirstName;
    private String LastName;

    private String fullName;

    private String userType;

    public User() {
    }

    public User(String userId, String email, String firstName, String lastName) {
        this.userId = userId;
        this.email = email;
        FirstName = firstName;
        LastName = lastName;
        fullName = firstName + " " + lastName;
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
}