package com.example.quickconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.Add_Customers;
import com.example.quickconnect.databinding.ActivityRegistrationSelectionPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class Registration_Selection_Page extends AppCompatActivity {

    ActivityRegistrationSelectionPageBinding binding;

    FirebaseDatabase db;
    DatabaseReference usersReference;
    DatabaseReference customersReference;
    DatabaseReference employeesReference;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationSelectionPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration_Selection_Page.this, Add_Customers.class);
                startActivity(intent);
            }
        });

        binding.buttonEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration_Selection_Page.this, Add_Employee.class);
                startActivity(intent);
            }
        });

    }
}