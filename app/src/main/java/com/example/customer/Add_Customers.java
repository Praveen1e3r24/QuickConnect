package com.example.customer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.quickconnect.BankAccount;
import com.example.quickconnect.Card;
import com.example.quickconnect.Customer;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.ActivityAddCustomersBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

                                        User user = new User(userId,email, firstName, lastName, "96542408", "Singapore");

                                        // Determine where to save the user data based on userType
                                        DatabaseReference userTypeReference;

                                            userTypeReference = customersReference.child(userId);
                                            Card card = new Card("1234567890123456", "Credit", "12/24", 4000, 200,"Active");
                                            Card card2 = new Card("9934567844663456", "Debit", "12/24", 2000, 100, "Active");
                                            Card card3 = new Card("333567844663456", "Debit", "12/24", 1000, 100, "Active");
                                            BankAccount account = new BankAccount("123-4567-890", "OCBC 360 Account", 1000);
                                            List<Transaction> transactions = createTransactionList();
                                            user = new Customer(user,Arrays.asList(account,account), Arrays.asList(card, card2,card3),transactions);




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


    public List<Transaction> createTransactionList() {
        List<Transaction> transactionList = new ArrayList<>();

        ArrayList<String> descriptionList = new ArrayList<>();
        descriptionList.add("Groceries");
        descriptionList.add("Shopping");
        descriptionList.add("Transport");
        descriptionList.add("Food");
        descriptionList.add("Entertainment");
        descriptionList.add("Bills");
        descriptionList.add("Others");


        for (int i = 0; i < 7; i++) {
            Transaction t = new Transaction("1234", "1234", descriptionList.get(i), "1234");
            transactionList.add(t); // Add the transaction to the list
        }

        return transactionList;
    }

}
