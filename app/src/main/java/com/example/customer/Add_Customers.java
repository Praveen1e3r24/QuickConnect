package com.example.customer;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.BankAccount;
import com.example.quickconnect.Card;
import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Login;
import com.example.quickconnect.User;
import com.example.quickconnect.add_users;
import com.example.quickconnect.databinding.ActivityAddCustomersBinding;
import com.example.quickconnect.databinding.ActivityAddUsersBinding;
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
public class Add_Customers extends AppCompatActivity {

    ActivityAddCustomersBinding binding;
    FirebaseDatabase db;
    DatabaseReference usersReference;
    DatabaseReference customersReference;
    DatabaseReference employeesReference;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        binding = ActivityAddCustomersBinding.inflate(getLayoutInflater());
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
                String email=binding.email.getText().toString();



                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Add_Customers.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(Add_Customers.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!firstName.isEmpty() && !lastName.isEmpty() && !password.isEmpty()  && !email.isEmpty()
                ){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        String userId = mAuth.getCurrentUser().getUid();

                                        User user = new User(userId,email, firstName, lastName);

                                        // Determine where to save the user data based on userType
                                        DatabaseReference userTypeReference;

                                            userTypeReference = customersReference.child(userId);
                                            Card card = new Card("1234567890123456", "Debit", "12/24", 1000);
                                            Card card2 = new Card("1234567890123456", "Debit", "12/24", 1000, 1000);
                                            BankAccount account = new BankAccount("123-4567-890", "OCBC 360 Account", 1000);
                                            user = new Customer(user,Arrays.asList(account,account), Arrays.asList(card, card2));




                                        // Save user data under the appropriate reference
                                        userTypeReference.setValue(user);

                                        Toast.makeText(Add_Customers.this, "Account is created", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(), Customer_Main.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(Add_Customers.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

        });
    }
}
