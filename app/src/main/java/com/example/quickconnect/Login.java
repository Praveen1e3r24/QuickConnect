package com.example.quickconnect;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Employee_M.Employee_M_Main;
import com.example.customer.Customer_Main;
import com.example.quickconnect.databinding.ActivityLoginBinding;
import com.example.quickconnect_employee_cc.Employee_CallCentre_Main;
import com.example.utilities.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode;
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutPictureInPictureConfig;
import com.zegocloud.uikit.components.common.ZegoShowFullscreenModeToggleButtonRules;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener;
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig;
import com.zegocloud.uikit.prebuilt.call.config.ZegoHangUpConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.call.config.ZegoMenuBarButtonName;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

import java.util.Arrays;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;

    FirebaseAuth mAuth;
    FirebaseDatabase db;

    ProgressBar progressBar;

    TextView textView;

    long appID = 479817296;
    String appSign = "db0f7d073f55e3553552fe098454ae400f0b80572a520bce6ef56058a53d3728";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseDatabase.getInstance();



        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String authentication_uid = mAuth.getCurrentUser().getUid();
            Log.d(TAG, "onComplete: " + authentication_uid);
            checkUserRole(authentication_uid);
            Log.d(TAG, "onStart: 1");

        }
        Log.d(TAG, "onStart: 2");


        binding.RedirectSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registration_Selection_Page.class);
                startActivity(intent);
                Log.d(TAG, "onStart: 3");
                finish();
            }
        });

        binding.Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = binding.loginUsername.getText().toString();
                password = binding.loginPassword.getText().toString();
                Log.d(TAG, "onStart: 4");

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "onStart: 5");
//                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                    mAuth.getCurrentUser().getUid();

                                    String authentication_uid = mAuth.getCurrentUser().getUid();
                                    Log.d(TAG, "onComplete: " + authentication_uid);
                                    checkUserRole(authentication_uid);
                                    Log.d(TAG, "onStart: 6");


                                } else {

                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


            }


        });
    }



    @Override
    protected void onStart() {
        super.onStart();
    }



    private void checkUserRole(String uid) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Check if the user is an employee
        usersRef.child("Employees").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot employeeSnapshot) {
                if (employeeSnapshot.exists()) {
                    // User is an employee, check employeeRole
                    Employee employee = employeeSnapshot.getValue(Employee.class);
                    handleEmployee(employee);
                    new UserData().storeUserDetails(getApplicationContext(),employee);
                } else {
                    // User is not an employee, check if they are a customer
                    usersRef.child("Customers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot customerSnapshot) {
                            if (customerSnapshot.exists()) {
                                // User is a customer
                                Customer customer = customerSnapshot.getValue(Customer.class);
                                handleCustomer(customer);
                                new UserData().storeUserDetails(getApplicationContext(),customer);
                            } else {
                                // The user data doesn't exist in either "customer" or "employee" nodes
                                Toast.makeText(Login.this, "User data not found in the database", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Error fetching user details: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user details: " + databaseError.getMessage());
            }
        });
    }

    private void handleEmployee(Employee employee) {
        // User is an employee, handle based on employeeRole
        String employeeRole = employee.getEmployeeRole();
        if ("CS".equals(employeeRole)) {
            // Employee is assigned to call service

            String userID = employee.getFirstName();
            String userName = employee.getFirstName()+"_name";

            initCallInviteService(appID, appSign, userID, userName);

            Intent callServiceIntent = new Intent(getApplicationContext(), Employee_CallCentre_Main.class);
            startActivity(callServiceIntent);
        } else if ("M".equals(employeeRole)) {
            // Employee is assigned to chat specialist


            String userID = employee.getFirstName();
            String userName = employee.getFirstName()+"_name";

            initCallInviteService(appID, appSign, userID, userName);

            Intent chatSpecialistIntent = new Intent(getApplicationContext(), Employee_M_Main.class);
            startActivity(chatSpecialistIntent);
        }
        finish(); // Close the current login activity
    }

    private void handleCustomer(Customer customer) {
        // User is a customer, redirect to customer activity

        String userID = customer.getFirstName();
        String userName = customer.getFirstName()+"_name";

        initCallInviteService(appID, appSign, userID, userName);
        Intent customerIntent = new Intent(getApplicationContext(), Customer_Main.class);
        startActivity(customerIntent);
        finish();

         // Close the current login activity
    }



    private User getUserDetailsFromSharedPreferences() {
        // Obtain SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        // Retrieve the stored JSON string
        String userJson = sharedPreferences.getString("UserDetails", null);

        // Check if the JSON string is not null
        if (userJson != null) {
            // Use Gson to deserialize the JSON string into your User object
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            if (user.getUserType().equals("Customer")) {
                // If the user is a Customer, store it as a Customer object
                Customer customer = gson.fromJson(userJson, Customer.class);
                return customer;
            } else if (user.getUserType().equals("Employee")) {
                // If the user is an Employee, store it as an Employee object
                Employee employee = gson.fromJson(userJson, Employee.class); // Replace 'Employee' with your actual Employee class
                return employee;
            }

            return null; // Return null if the user type is neither Customer nor Employee
        }

        return null; // Return null if the JSON string is null or if there's an error in deserialization
    }


    public void initCallInviteService(long appID, String appSign, String userID, String userName) {
        Log.d("HI", "requireConfig: 11");
        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        Log.d("HI", "requireConfig: 10");
//        callInvitationConfig.innerText.incomingCallPageDeclineButton = "Decline";
//        Log.d("HI", "requireConfig: 9");
//        callInvitationConfig.innerText.incomingCallPageAcceptButton = "Accept";
//        Log.d("HI", "requireConfig: 8");


        callInvitationConfig.provider = invitationData -> {
            Log.d("HI", "requireConfig: 7");
            ZegoUIKitPrebuiltCallConfig config;
            Log.d("HI", "requireConfig: 6");
            boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
            Log.d("HI", "requireConfig: 09");
            boolean isGroupCall = invitationData.invitees.size() > 1;
            Log.d("HI", "requireConfig: 907");
            if (isVideoCall && isGroupCall) {

                Log.d("HI", "requireConfig: 1");
                config = ZegoUIKitPrebuiltCallConfig.groupVideoCall();
            } else if (!isVideoCall && isGroupCall) {
                Log.d("HI", "requireConfig: 2");
                config = ZegoUIKitPrebuiltCallConfig.groupVoiceCall();
            } else if (!isVideoCall) {
                Log.d("HI", "requireConfig: 3");
                config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
            } else {
                Log.d("HI", "requireConfig: 4");
                config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
            }
            // Modify your custom calling configurations here.

            if(isGroupCall) {
                Log.d("HI", "requireConfig: 5");
                ZegoLayoutGalleryConfig galleryConfig = new ZegoLayoutGalleryConfig();
                galleryConfig.addBorderRadiusAndSpacingBetweenView = false;
                config.layout.mode = ZegoLayoutMode.GALLERY;
                config.layout.config = galleryConfig;
                config.hangUpConfirmDialogInfo = new ZegoHangUpConfirmDialogInfo();
                config.hangUpConfirmDialogInfo.title = "Hangup confirm";
                config.hangUpConfirmDialogInfo.message = "Do you want to hangup?";
                config.hangUpConfirmDialogInfo.cancelButtonName = "Cancel";
                config.hangUpConfirmDialogInfo.confirmButtonName = "Confirm";
                config.durationConfig = new ZegoCallDurationConfig();
                config.durationConfig.isVisible = true;
                config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                    @Override
                    public void onDurationUpdate(long seconds) {
                        Log.d(TAG, "onDurationUpdate() called with: seconds = [" + seconds + "]");
                        if (seconds == 60 * 5) {
                            ZegoUIKitPrebuiltCallInvitationService.endCall();
                        }
                    }
                };
                config.bottomMenuBarConfig.buttons = Arrays.asList(ZegoMenuBarButtonName.TOGGLE_CAMERA_BUTTON,ZegoMenuBarButtonName.SWITCH_CAMERA_BUTTON, ZegoMenuBarButtonName.HANG_UP_BUTTON,ZegoMenuBarButtonName.TOGGLE_MICROPHONE_BUTTON, ZegoMenuBarButtonName.SCREEN_SHARING_TOGGLE_BUTTON);
                galleryConfig.removeViewWhenAudioVideoUnavailable = true;
                galleryConfig.showNewScreenSharingViewInFullscreenMode = true;
                galleryConfig.showScreenSharingFullscreenModeToggleButtonRules = ZegoShowFullscreenModeToggleButtonRules.SHOW_WHEN_SCREEN_PRESSED;
                config.layout = new ZegoLayout(ZegoLayoutMode.GALLERY, galleryConfig);
                config.topMenuBarConfig.isVisible = true;
                config.topMenuBarConfig.buttons.add(ZegoMenuBarButtonName.MINIMIZING_BUTTON);
                return config;


            } else {
                Log.d("HI", "requireConfig: 6");

                ZegoLayoutPictureInPictureConfig pipConfig = new ZegoLayoutPictureInPictureConfig();
                pipConfig.isSmallViewDraggable = true;
                pipConfig.switchLargeOrSmallViewByClick = true;
                config.layout.mode = ZegoLayoutMode.PICTURE_IN_PICTURE;
                config.layout.config = pipConfig;
                config.hangUpConfirmDialogInfo = new ZegoHangUpConfirmDialogInfo();
                config.hangUpConfirmDialogInfo.title = "Hangup confirm";
                config.hangUpConfirmDialogInfo.message = "Do you want to hangup?";
                config.hangUpConfirmDialogInfo.cancelButtonName = "Cancel";
                config.hangUpConfirmDialogInfo.confirmButtonName = "Confirm";
                config.durationConfig = new ZegoCallDurationConfig();
                config.durationConfig.isVisible = true;
                config.topMenuBarConfig.isVisible = true;
                config.topMenuBarConfig.buttons.add(ZegoMenuBarButtonName.MINIMIZING_BUTTON);
                config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                    @Override
                    public void onDurationUpdate(long seconds) {
                        Log.d(TAG, "onDurationUpdate() called with: seconds = [" + seconds + "]");
                        if (seconds == 60 * 5) {
                            ZegoUIKitPrebuiltCallInvitationService.endCall();
                        }
                    }

                };

                return config;
            }
        };

        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(),appID,appSign,userID,userName,callInvitationConfig);


    };
//public void initCallInviteService(long appID, String appSign, String userID, String userName) {
//    Log.d("HI", "requireConfig: 4");
//
//    ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
//    Log.d("HI", "requireConfig: 5");
//
//
//    ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,
//            callInvitationConfig);
//    Log.d("HI", "requireConfig: 6");
//}




}

