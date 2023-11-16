package com.example.quickconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.databinding.ActivityCustomerProfileBinding;
import com.example.quickconnect.databinding.EmailPopupBinding;
import com.example.utilities.UserData;

public class Customer_Profile extends AppCompatActivity {

    ActivityCustomerProfileBinding binding;

    EmailPopupBinding binding2;
    User userDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Context context = this; // or use getApplicationContext() or another valid context

        // Create an instance of UserData
        UserData userData = new UserData();

        // Now you can call the method
        userDetails = userData.getUserDetailsFromSharedPreferences(context);

        binding.imageView2.setOnClickListener(view -> {

        });

        binding.imageView4.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Get the layout inflater
            LayoutInflater inflater = LayoutInflater.from(this);
            View view1 = inflater.inflate(R.layout.email_popup, null);

            // Find the EditText and Button in the custom layout
            final EditText emailMessageEditText = view.findViewById(binding2.emailEdittext.getId());
            Button sendButton = view.findViewById(binding2.emailSubmitBtn.getId());

            // Set up the AlertDialog
            builder.setView(view);
            builder.setTitle("Send Email to Customer");

            // Set up the "Send" button click listener
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String emailMessage = emailMessageEditText.getText().toString();
                    sendEmailToCustomer(emailMessage);
                }
            });

            // Set up the "Cancel" button
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // Show the AlertDialog
            builder.show();

        });
    }



    public void sendEmailToCustomer(String emailinput){

        Log.d("hi1234", "doInBackground: qdq434wrgggdq");


// Customize the email address (add some text, modify, etc.


        String mEmail = userDetails.getEmail();

        String mSubject = "OCBC Customer Service";

// Get the custom message from another EditText (assuming you have one named "CustomMessageEditText")
        String customMessage =emailinput;

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
        String phoneNumber = "tel:" + "123456789"; // Replace with the desired phone number

        // Create an intent with the ACTION_CALL action and the phone number URI
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));

        // Check if the CALL_PHONE permission is granted before making the call
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Start the phone call
            startActivity(dialIntent);
        } else {
            // If permission is not granted, request it from the user
            requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }
    }
}