package com.example.quickconnect_employee_cc;


import static com.example.quickconnect.R.id.nav_e_cc_home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Login;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.ActivityEmployeeCallCentreMainBinding;
import com.example.utilities.UserData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class Employee_CallCentre_Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    ActivityEmployeeCallCentreMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeCallCentreMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav);

         binding.drawerLayout.addDrawerListener(toggle);

         toggle.syncState();

         if(savedInstanceState == null) {
             getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Home_Fragment()).commit();
             binding.navView.setCheckedItem(nav_e_cc_home);
         }

        User user = getUserDetailsFromSharedPreferences();

        updateNavHeader(user);


    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == nav_e_cc_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Home_Fragment()).commit();
        } else if (itemId == R.id.nav_e_cc_chatbot) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Call_Fragment()).commit();
        } else if (itemId == R.id.nav_e_cc_language) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Language_Fragment()).commit();
        } else if (itemId == R.id.nav_e_cc_profile_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Profile_Fragment()).commit();
        } else if (itemId == R.id.nav_e_cc_logout) {
            logout();
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
        }
      binding.drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private void logout() {
        // Perform logout tasks here
        new UserData().removeUserDetails(this);
        // Example: Clear Firebase Authentication
        FirebaseAuth.getInstance().signOut();
        // Redirect to the login screen
        Intent intent = new Intent(Employee_CallCentre_Main.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    private User getUserDetailsFromSharedPreferences() {
        // Obtain SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

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


    // Add this method to update the header layout with user details
    private void updateNavHeader(User user) {
        if (user != null) {
            // Access the header view
            LinearLayout headerView = (LinearLayout) binding.navView.getHeaderView(0);

            // Access the TextViews in the header
            TextView userEmailTextView = headerView.findViewById(R.id.nav_header_email_e_cc);
            TextView userContactTextView = headerView.findViewById(R.id.nav_header_contact_e_cc);

            // Set user details in the TextViews
            userEmailTextView.setText(user.getEmail());
            userContactTextView.setText(user.getFullName());
        }
    }
}