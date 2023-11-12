package com.example.quickconnect;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AppPreferences;
import com.example.Employee_M.Employee_M_Main;
import com.example.customer.Customer_Main;
import com.example.quickconnect.databinding.ActivityAddUsersBinding;
import com.example.quickconnect.databinding.ActivityLoginBinding;
import com.example.quickconnect_employee_cc.Employee_CallCentre_Main;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;

    FirebaseAuth mAuth;
    FirebaseDatabase db;

    ProgressBar progressBar;

    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String authentication_uid = mAuth.getCurrentUser().getUid();
            Log.d(TAG, "onComplete: " + authentication_uid);
            checkUserRole(authentication_uid);
            Log.d(TAG, "onStart: 1");

        }
        Log.d(TAG, "onStart: 2");


        binding.RedirectSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registration_Selection_Page.class);
                startActivity(intent);
                Log.d(TAG, "onStart: 3");
                finish();
            }
        });

        binding.Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = binding.loginUsername.getText().toString();
                password = binding.loginPassword.getText().toString();
                Log.d(TAG, "onStart: 4");

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "onStart: 5");
//                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                    mAuth.getCurrentUser().getUid();

                                    String authentication_uid = mAuth.getCurrentUser().getUid();
                                    Log.d(TAG, "onComplete: " + authentication_uid);
                                    checkUserRole(authentication_uid);
                                    Log.d(TAG, "onStart: 6");


                                } else {

                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


            }


        });
    }


    private void checkUserRole(String uid) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Check if the user is an employee
        usersRef.child("Employees").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot employeeSnapshot) {
                if (employeeSnapshot.exists()) {
                    // User is an employee, check employeeRole
                    Employee employee = employeeSnapshot.getValue(Employee.class);
                    handleEmployee(employee);
                    storeUserDetails(employee);
                } else {
                    // User is not an employee, check if they are a customer
                    usersRef.child("Customers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot customerSnapshot) {
                            if (customerSnapshot.exists()) {
                                // User is a customer
                                Customer customer = customerSnapshot.getValue(Customer.class);
                                handleCustomer(customer);
                                storeUserDetails(customer);
                            } else {
                                // The user data doesn't exist in either "customer" or "employee" nodes
                                Toast.makeText(Login.this, "User data not found in the database", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Error fetching user details: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user details: " + databaseError.getMessage());
            }
        });
    }

    private void handleEmployee(Employee employee) {
        // User is an employee, handle based on employeeRole
        String employeeRole = employee.getEmployeeRole();
        if ("CS".equals(employeeRole)) {
            // Employee is assigned to call service
            Intent callServiceIntent = new Intent(getApplicationContext(), Employee_CallCentre_Main.class);
            startActivity(callServiceIntent);
        } else if ("M".equals(employeeRole)) {
            // Employee is assigned to chat specialist
            Intent chatSpecialistIntent = new Intent(getApplicationContext(), Employee_M_Main.class);
            startActivity(chatSpecialistIntent);
        }
        finish(); // Close the current login activity
    }

    private void handleCustomer(Customer customer) {
        // User is a customer, redirect to customer activity
        Intent customerIntent = new Intent(getApplicationContext(), Customer_Main.class);
        startActivity(customerIntent);
        finish(); // Close the current login activity
    }

    private void storeUserDetails(User user) {
        // Use Gson to serialize the User object to a JSON string
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        // Obtain SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        // Store the JSON string in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserDetails", userJson);
        editor.apply();
    }
}

