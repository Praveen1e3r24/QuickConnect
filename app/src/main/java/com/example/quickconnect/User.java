package com.example.quickconnect;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {

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


    protected User(Parcel in) {
        userId = in.readString();
        email = in.readString();
        FirstName = in.readString();
        LastName = in.readString();
        fullName = in.readString();
        userType = in.readString();
        phonenumber = in.readString();
        address = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(fullName);
        dest.writeString(userType);
        dest.writeString(phonenumber);
        dest.writeString(address);
    }
}
