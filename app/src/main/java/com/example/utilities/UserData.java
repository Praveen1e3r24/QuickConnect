package com.example.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.User;
import com.google.gson.Gson;

public class UserData {


    public User getUserDetailsFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("UserDetails", null);

        if (userJson != null) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            // Check user type and cast accordingly
            if (user != null) {
                if (user.getUserType().equals("Customer")) {
                    return gson.fromJson(userJson, Customer.class);
                } else if (user.getUserType().equals("Employee")) {
                    return gson.fromJson(userJson, Employee.class);
                }
            }
        }

        return null;
    }


    public void storeUserDetails(Context c, User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        SharedPreferences sharedPreferences = c.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserDetails", userJson);
        editor.apply();
    }

    public void removeUserDetails(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("UserDetails");
        editor.apply();
    }
}
