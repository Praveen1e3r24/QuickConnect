package com.example.quickconnect;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.databinding.ActivityAddEmployeeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Add_Employee extends AppCompatActivity {
    ActivityAddEmployeeBinding binding;
    FirebaseDatabase db;
    DatabaseReference usersReference;
    DatabaseReference customersReference;
    DatabaseReference employeesReference;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        binding = ActivityAddEmployeeBinding.inflate(getLayoutInflater());
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
                String jobRole = binding.jobRole.getText().toString();
                String email=binding.email.getText().toString();
                String team=binding.supportteam.getText().toString();


                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Add_Employee.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(Add_Employee.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!firstName.isEmpty() && !lastName.isEmpty() && !password.isEmpty() && !jobRole.isEmpty() && !email.isEmpty() && !team.isEmpty()){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        String userId = mAuth.getCurrentUser().getUid();

                                        User user = new User(userId,email, firstName, lastName);

                                        // Determine where to save the user data based on userType
                                        DatabaseReference userTypeReference;
                                        if (jobRole.equals("CS")) {
                                            Log.d(TAG, "onComplete: 11");

                                            userTypeReference = employeesReference.child(userId);
                                            user = new Employee(user, "CS", team, 0, true);
                                            Log.d(TAG, "onComplete: 12");

                                        } else if (jobRole.equals("M")) {
                                            Log.d(TAG, "onComplete: 13");
                                            userTypeReference = employeesReference.child(userId);
                                            user = new Employee(user, "M", team, 0, true);

                                        } else {
                                            Log.d(TAG, "onComplete: 14");
                                            Toast.makeText(Add_Employee.this, "Invalid user type", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // Save user data under the appropriate reference
                                        userTypeReference.setValue(user);
                                        Log.d(TAG, "onComplete: 15");
                                        Toast.makeText(Add_Employee.this, "Employee Account is created", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(),Login.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Log.d(TAG, "onComplete: 16");
                                        Exception exception = task.getException();
                                        if (exception != null) {
                                            Log.e(TAG, "Authentication failed: " + exception.getMessage());
                                        }
                                        Toast.makeText(Add_Employee.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

        });
    }
}
