package com.example.quickconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.databinding.ActivityAddUsersBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

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
                    String team=binding.supportteam.getText().toString();


                    if(TextUtils.isEmpty(email)){
                        Toast.makeText(add_users.this, "Enter Email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(TextUtils.isEmpty(password)){
                        Toast.makeText(add_users.this, "Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (!firstName.isEmpty() && !lastName.isEmpty() && !password.isEmpty() && !userType.isEmpty() && !email.isEmpty()
                            && (userType.equals("customer") || userType.equals("employee"))){
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            String userId = mAuth.getCurrentUser().getUid();

                                            User user = new User(userId,email, firstName, lastName);

                                            // Determine where to save the user data based on userType
                                            DatabaseReference userTypeReference;
                                            if ("customer".equals(userType)) {
                                                userTypeReference = customersReference.child(userId);
                                                Card card = new Card("1234567890123456", "Debit", "12/24", 1000);
                                                Card card2 = new Card("1234567890123456", "Debit", "12/24", 1000, 1000);
                                                BankAccount account = new BankAccount("123-4567-890", "OCBC 360 Account", 1000);
                                                user = new Customer(user,Arrays.asList(account,account), Arrays.asList(card, card2));

                                            } else if ("employee".equals(userType)) {
                                                userTypeReference = employeesReference.child(userId);
                                                user = new Employee(user, "Chat Specialist", team, true);

                                            } else {
                                                Toast.makeText(add_users.this, "Invalid user type", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // Save user data under the appropriate reference
                                            userTypeReference.setValue(user);

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
                }

            });
        }
    }
