package com.example.quickconnect;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.databinding.ActivityCustomerProfileBinding;
import com.example.quickconnect.databinding.EmailPopupBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.List;

public class Customer_Profile extends AppCompatActivity {

    ActivityCustomerProfileBinding binding;

    EmailPopupBinding binding2;

    CallRequest callRequest;
    DatabaseReference dbRef;
    Customer reqCustomer;


    private boolean hasEmployee;

    String topic;



    private Context getContext() {
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        callRequest = getIntent().getParcelableExtra("chatRequest");

        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("Users").child("Customers").child(callRequest.getCustomerId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot customerSnapshot) {


                if (customerSnapshot.exists()) {
                  reqCustomer = customerSnapshot.getValue(Customer.class);

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


                    //initialising the activity with the data
                    binding.AddressInput.setText(reqCustomer.getAddress());
                    binding.FirstNameInput.setText(reqCustomer.getFirstName());
                    binding.LastNameInput.setText(reqCustomer.getLastName());

                    binding.EmailInput.setText(reqCustomer.getEmail());
                    binding.numberInput.setText(reqCustomer.getPhonenumber());
                    binding.profileTitle.setText(reqCustomer.getFirstName() + " " + reqCustomer.getLastName() + "'s Profile");

                    Log.d(TAG, "onDataChange123: "+callRequest.getCallType());

                    // type of call to show

                    if (callRequest.getCallType().equals("Video")) {
                        binding.newVoiceCall.setVisibility(View.GONE);
                    } else {
                        binding.newVideoCall.setVisibility(View.GONE);
                    }


                    //click listener for the phone call
                    //email

                    binding.EmailInput.setOnClickListener(view -> {

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setTitle(reqCustomer.getEmail());
                        alert.setMessage("Please enter your message");


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

        initVoiceButton(reqCustomer);

        initVideoButton(reqCustomer);


    }








    private void initVideoButton (Customer reqCustomer) {
        ZegoSendCallInvitationButton newVideoCall = binding.newVideoCall;
        newVideoCall.setIsVideoCall(true);
        newVideoCall.setOnClickListener(v -> {
            dbRef.child("Requests").child(callRequest.getRequestId()).child("accepted").setValue(true);
            String targetUserID = "white1";
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVideoCall.setInvitees(users);
        });
    }

    private void initVoiceButton (Customer reqCustomer) {
        ZegoSendCallInvitationButton newVoiceCall = binding.newVoiceCall;
        newVoiceCall.setIsVideoCall(false);
        newVoiceCall.setOnClickListener(v -> {
            dbRef.child("Requests").child(callRequest.getRequestId()).child("accepted").setValue(true);
            String targetUserID = "white1";
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVoiceCall.setInvitees(users);
        });
    }

//    private ValueEventListener addRequestToDB(String CallType, String department){
//        hasEmployee = false;
//        if (topic == null) {
//            topic = "General Inquiries";
//        }
//        ValueEventListener eventListener = new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot s : snapshot.getChildren()) {
//                    Employee employee = s.getValue(Employee.class);
//                    if (employee != null && employee.getAvailable() && employee.getEmployeeRole().equals("CS") && employee.getDepartment().equals(department)) {
//                        hasEmployee = true;
//                        User user = new UserData().getUserDetailsFromSharedPreferences(getContext());
//                        String query = binding.editTextComplaint.getText().toString();
//
//                        dbRef.child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                int queueNo = 0;
//
//                                if (snapshot.exists()) {
//                                    queueNo = (int) snapshot.getChildrenCount();
//                                }
//                                List<Message> messages = new ArrayList<>();
//                                Chat chat = new Chat(UUID.randomUUID().toString(), employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
//                                CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(), user.getUserId(), user.getFullName(), employee.getUserId(), employee.getFullName(), chat, query, topic, queueNo, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), false, false, CallType);
//                                callRequest.setRequestId(dbRef.child("Requests").push().getKey());
//                                callRequest.getChat().setCallRequestId(callRequest.getRequestId());
//                                dbRef.child("Requests").child(callRequest.getRequestId()).setValue(callRequest);
//                                dbRef.child("Users").child("Employees").child(employee.getUserId()).child("available").setValue(false);
//                                dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
//                                Toast.makeText(getContext(), "Call Request Sent", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getContext(), call_waiting_dashboard.class);
//                                intent.putExtra("callRequest", callRequest);
//                                startActivity(intent);
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        break;
//                    }
//                }
//
//                if (!hasEmployee)
//                {
//                    Toast.makeText(getContext(), "No Available Employees", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//
//
//        return eventListener;
//    }

   

}













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




//    public void sendEmailToCustomer(String emailinput){
//
//        Log.d("hi1234", "doInBackground: qdq434wrgggdq");
//
//
//// Customize the email address (add some text, modify, etc.
//
//
//        String mEmail = reqCustomer.getEmail();
//
//        String mSubject = "OCBC Customer Service";
//
//// Get the custom message from another EditText (assuming you have one named "CustomMessageEditText")
////        String customMessage =emailinput;
//
//// Build the email message using the custom content
//        String message = "<html><body><h2>Dear Customer, this message is from OCBC Customer Service.</h2>";
//        message += "<p>" + emailinput + "</p></body></html>";
//
//        Log.d("hi1234", "doInBackground: qdq434wrgggdqfcdcfda");
//
//        JavaMailApi javaMailApi = new JavaMailApi(this, mEmail, mSubject, message);
//        javaMailApi.execute();
//
//    }
//
//    // Method to handle the button click and make a phone call
//    private void makePhoneCall() {
//        // Phone number to dial
//
//
//        String phoneNumber = reqCustomer.getPhonenumber();
//
//        String phoneNumber2 = "tel:" + phoneNumber; // Replace with the desired phone number
//
//
//
//        // Create an intent with the ACTION_CALL action and the phone number URI
//         Intent dialIntent = new Intent(Intent.ACTION_CALL);
//         dialIntent.setData(Uri.parse(phoneNumber2));
//
//        // Check if the CALL_PHONE permission is granted before making the call
//        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//            dbRef.child("Requests").child(callRequest.getRequestId()).child("accepted").setValue(true);
//            // Start the phone call
//            startActivity(dialIntent);
//        } else {
//            // If permission is not granted, request it from the user
//            requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
//        }
//    }

