package com.example.quickconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.Transaction;
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

        Customer customer = new Customer();
        customer= (Customer) userDetails;

        binding.profileEmailaddress.setText(userDetails.getEmail());
        binding.profileMobileNumber.setText(userDetails.getPhonenumber());
        binding.profileTitle.setText(userDetails.getFirstName() + " " + userDetails.getLastName()+"'s Profile");
        for (Transaction transaction : customer.getTransactions()) {
            binding.ProfileRecentTransactionHistory.append(transaction.toString());
        }


        binding.imageView2.setOnClickListener(view -> {

            makePhoneCall();

        });

        binding.imageView4.setOnClickListener(view -> {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle(userDetails.getEmail());
            alert.setMessage("Please enter your message");

// Set an EditText view to get user input
            final EditText input = new EditText(this);
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



    }



    public void sendEmailToCustomer(String emailinput){

        Log.d("hi1234", "doInBackground: qdq434wrgggdq");


// Customize the email address (add some text, modify, etc.


        String mEmail = userDetails.getEmail();

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


        String phoneNumber = userDetails.getPhonenumber();

        String phoneNumber2 = "tel:" + phoneNumber; // Replace with the desired phone number



        // Create an intent with the ACTION_CALL action and the phone number URI
         Intent dialIntent = new Intent(Intent.ACTION_CALL);
         dialIntent.setData(Uri.parse(phoneNumber2));
         startActivity(dialIntent);

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