package com.example.quickconnect;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.Transaction;
import com.example.quickconnect.databinding.ActivityCustomerProfileBinding;
import com.example.quickconnect.databinding.EmailPopupBinding;
import com.example.utilities.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Customer_Profile extends AppCompatActivity {

    ActivityCustomerProfileBinding binding;

    EmailPopupBinding binding2;

    CallRequest callRequest;
    DatabaseReference dbRef;

    Customer reqCustomer;

    private Context getContext() {
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Context context = this; // or use getApplicationContext() or another valid context

        callRequest = getIntent().getParcelableExtra("chatRequest");

        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("Users").child("Customers").child(callRequest.getCustomerId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot customerSnapshot) {
                if (customerSnapshot.exists()) {
                    Customer customer = customerSnapshot.getValue(Customer.class);
                    reqCustomer = customerSnapshot.getValue(Customer.class);

                    binding.profileEmailaddress.setText(reqCustomer.getEmail());
                    binding.profileMobileNumber.setText(reqCustomer.getPhonenumber());
                    binding.profileTitle.setText(reqCustomer.getFirstName() + " " + reqCustomer.getLastName()+"'s Profile");


                    binding.imageView2.setOnClickListener(view -> {

                        makePhoneCall();

                    });

                    binding.pGotochat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Customer_Profile.this, ChatActivity.class);
                            intent.putExtra("chat", callRequest.getChat());
                            intent.putExtra("callRequest", callRequest);
                            intent.putExtra("isRequest", true);
                            startActivity(intent);
                            finish();
                        }
                    });

                    binding.acceptcall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makePhoneCall();
                        }
                    });

                    binding.imageView4.setOnClickListener(view -> {

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setTitle(reqCustomer.getEmail());
                        alert.setMessage("Please enter your message");

// Set an EditText view to get user input
                        final EditText input = new EditText(getContext());
                        alert.setView(input);

                        alert.setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                // Do something with value!
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });

                        alert.show();



                    });

                    binding.ProfileRecentTransactionHistory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Customer_Profile.this, transactions.class);
                            intent.putExtra("callRequest", callRequest);
                            startActivity(intent);
                        }
                    });

                } else {
                    // The user data doesn't exist in either "customer" or "employee" nodes
                    Toast.makeText(getApplicationContext(), "User data not found in the database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user details: " + databaseError.getMessage());
            }
        });


//        binding.profileEmailaddress.setText(reqCustomer.getEmail());
//        binding.profileMobileNumber.setText(reqCustomer.getPhonenumber());
//        binding.profileTitle.setText(reqCustomer.getFirstName() + " " + reqCustomer.getLastName()+"'s Profile");
//        for (Transaction transaction : reqCustomer.getTransactions()) {
//            binding.ProfileRecentTransactionHistory.append(transaction.toString());
//        }
//
//
//        binding.imageView2.setOnClickListener(view -> {
//
//            makePhoneCall();
//
//        });
//
//        binding.imageView4.setOnClickListener(view -> {
//
//            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//
//            alert.setTitle(reqCustomer.getEmail());
//            alert.setMessage("Please enter your message");
//
//// Set an EditText view to get user input
//            final EditText input = new EditText(getContext());
//            alert.setView(input);
//
//            alert.setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//
//                    // Do something with value!
//                }
//            });
//
//            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    // Canceled.
//                }
//            });
//
//            alert.show();
//
//
//
//        });
//
//        binding.ProfileRecentTransactionHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Customer_Profile.this, transactions.class);
//                intent.putExtra("customer", reqCustomer);
//                startActivity(intent);
//            }
//        });


    }

    public void sendEmailToCustomer(String emailinput){

        Log.d("hi1234", "doInBackground: qdq434wrgggdq");


// Customize the email address (add some text, modify, etc.


        String mEmail = reqCustomer.getEmail();

        String mSubject = "OCBC Customer Service";

// Get the custom message from another EditText (assuming you have one named "CustomMessageEditText")
//        String customMessage =emailinput;

// Build the email message using the custom content
        String message = "<html><body><h2>Dear Customer, this message is from OCBC Customer Service.</h2>";
        message += "<p>" + emailinput + "</p></body></html>";

        Log.d("hi1234", "doInBackground: qdq434wrgggdqfcdcfda");

        JavaMailApi javaMailApi = new JavaMailApi(this, mEmail, mSubject, message);
        javaMailApi.execute();

    }

    // Method to handle the button click and make a phone call
    private void makePhoneCall() {
        // Phone number to dial


        String phoneNumber = reqCustomer.getPhonenumber();

        String phoneNumber2 = "tel:" + phoneNumber; // Replace with the desired phone number



        // Create an intent with the ACTION_CALL action and the phone number URI
         Intent dialIntent = new Intent(Intent.ACTION_CALL);
         dialIntent.setData(Uri.parse(phoneNumber2));

        // Check if the CALL_PHONE permission is granted before making the call
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            dbRef.child("Requests").child(callRequest.getRequestId()).child("accepted").setValue(true);
            // Start the phone call
            startActivity(dialIntent);
        } else {
            // If permission is not granted, request it from the user
            requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }
    }
}