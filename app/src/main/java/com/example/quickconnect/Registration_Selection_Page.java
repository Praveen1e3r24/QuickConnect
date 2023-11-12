package com.example.quickconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.Add_Customers;
import com.example.quickconnect.BankAccount;
import com.example.quickconnect.Card;
import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Login;
import com.example.quickconnect.User;
import com.example.quickconnect.add_users;
import com.example.quickconnect.databinding.ActivityAddCustomersBinding;
import com.example.quickconnect.databinding.ActivityAddUsersBinding;
import com.example.quickconnect.databinding.ActivityRegistrationSelectionPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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