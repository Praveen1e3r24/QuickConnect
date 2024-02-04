package com.example.Employee_M;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.customer.DarkModePrefManager;
import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.FragmentEmployeeMHomeBinding;
import com.google.gson.Gson;


public class Employee_M_Home_ extends Fragment {
    FragmentEmployeeMHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmployeeMHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (new DarkModePrefManager(getContext()).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onViewCreated(view, savedInstanceState);
        User user = getUserDetailsFromSharedPreferences();
        binding.name.setText("Welcome, " + user.getFullName());

        binding.firstbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_M_Requests_Fragment()).commit();
            }
        });

        binding.thirdbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_M_Settings_Profile_Fragment()).commit();
            }
        });




    }

    private User getUserDetailsFromSharedPreferences() {
        // Obtain SharedPreferences instance
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);

        // Retrieve the stored JSON string
        String userJson = sharedPreferences.getString("UserDetails", null);

        // Check if the JSON string is not null
        if (userJson != null) {
            // Use Gson to deserialize the JSON string into your User object
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            if (user.getUserType().equals("Customer")) {
                // If the user is a Customer, store it as a Customer object
                Customer customer = gson.fromJson(userJson, Customer.class);
                return customer;
            } else if (user.getUserType().equals("Employee")) {
                // If the user is an Employee, store it as an Employee object
                Employee employee = gson.fromJson(userJson, Employee.class); // Replace 'Employee' with your actual Employee class
                return employee;
            }

            return null; // Return null if the user type is neither Customer nor Employee
        }

        return null; // Return null if the JSON string is null or if there's an error in deserialization
    }

}