package com.example.quickconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.quickconnect.databinding.ActivityAddUsersBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class add_users extends AppCompatActivity {


        ActivityAddUsersBinding binding;
        FirebaseDatabase db;
        DatabaseReference usersReference;
        DatabaseReference customersReference;
        DatabaseReference employeesReference;

        FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mAuth=FirebaseAuth.getInstance();
            binding = ActivityAddUsersBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            db = FirebaseDatabase.getInstance();
            usersReference = db.getReference("Users");
            customersReference = usersReference.child("Customers");
            employeesReference = usersReference.child("Employees");

            binding.addUser1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String firstName = binding.firstName.getText().toString();
                    String lastName = binding.lastName.getText().toString();
                    String password = binding.password.getText().toString();
                    String userType = binding.usertype.getText().toString();
                    String email=binding.email.getText().toString();


                    if (!firstName.isEmpty() && !lastName.isEmpty() && !password.isEmpty() && !userType.isEmpty()) {
                        User user = new User(userType, firstName, lastName, password);

                        // Determine where to save the user data based on userType
                        DatabaseReference userTypeReference;
                        if ("customer".equals(userType)) {
                            userTypeReference = customersReference.child(firstName);
                        } else if ("employee".equals(userType)) {
                            userTypeReference = employeesReference.child(firstName);
                        } else {
                            // Handle other user types as needed
                            userTypeReference = usersReference.child("Other").child(firstName);
                        }

                        // Save user data under the appropriate reference
                        userTypeReference.setValue(user);
                    }




                    if(TextUtils.isEmpty(email)){
                        Toast.makeText(add_users.this, "Enter Email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(TextUtils.isEmpty(password)){
                        Toast.makeText(add_users.this, "Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(add_users.this, "Account is created", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(),Login.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(add_users.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            });
        }
    }
