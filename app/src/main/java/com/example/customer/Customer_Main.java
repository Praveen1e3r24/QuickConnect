package com.example.customer;

import static com.example.quickconnect.R.id.nav_c_home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.AppPreferences;
import com.example.NotificationHandler;
import com.example.palmchatbot.Chatbot_Activity;
import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Login;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.ActivityCustomerMainBinding;
import com.example.utilities.UserData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

public class Customer_Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ActivityCustomerMainBinding binding;

    private DatabaseReference chatDbRef = FirebaseDatabase.getInstance().getReference().child("Chats");

    private String userId = FirebaseAuth.getInstance().getUid();
    private boolean firstEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firstEnter = true;

        binding.navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav);

        binding.drawerLayout.addDrawerListener(toggle);
        binding.toolbar.setTitle("Home");
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Customer_Home_Fragment()).commit();
            binding.navView.setCheckedItem(nav_c_home);
        }

        User user = getUserDetailsFromSharedPreferences();

        NotificationHandler notificationHandler = NotificationHandler.getInstance();
        notificationHandler.initialize(this);

        updateNavHeader(user);

        chatDbRef.addValueEventListener(notificationHandler.checkNewMessage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationHandler notificationHandler = NotificationHandler.getInstance();
        notificationHandler.initialize(this);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == nav_c_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Customer_Home_Fragment()).commit();
        }
        else if (itemId == R.id.nav_c_Messaging) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Customer_Messaging_Call_Fragment()).commit();
        }  else if (itemId == R.id.nav_c_quickconnect) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Customer_QuickConnect_Fragment()).commit();
        } else if (itemId == R.id.nav_c_profile_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Customer_Profile_Setting_Fragment()).commit();
        }
        else if (itemId == R.id.nav_c_Voice_Video_Call) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Customer_Call_Request()).commit();
        }
        else if (itemId == R.id.nav_c_chatbot) {
            Intent intent = new Intent(this, Chatbot_Activity.class);
            startActivity(intent);
        }
        else if (itemId == R.id.nav_c_logout) {
            logout();
            Toast.makeText(this, "Logged Out of Account", Toast.LENGTH_SHORT).show();
            ZegoUIKitPrebuiltCallInvitationService.unInit();
            AppPreferences appPreferences = new AppPreferences(this);
            appPreferences.clearSharedPrefrences();
            return true;
        }
        binding.toolbar.setTitle(item.getTitle());
        binding.drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }


    private void logout() {
        // Perform logout tasks here
        new UserData().removeUserDetails(this);
        // Example: Clear Firebase Authentication
        FirebaseAuth.getInstance().signOut();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
        // Redirect to the login screen
        Intent intent = new Intent(Customer_Main.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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


    // Add this method to update the header layout with user details
    private void updateNavHeader(User user) {
        if (user != null) {
            // Access the header view
            LinearLayout headerView = (LinearLayout) binding.navView.getHeaderView(0);

            // Access the TextViews in the header
            TextView userEmailTextView = headerView.findViewById(R.id.nav_header_email_customer);
            TextView userContactTextView = headerView.findViewById(R.id.nav_header_contact_customer);

            // Set user details in the TextViews
            userEmailTextView.setText(user.getEmail());
            userContactTextView.setText(user.getFullName());
        }
    }




}

//    public void createNotification(Chat chat) {
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
//            return;
//        }
//
//
//        List<Message> messageList = chat.getMessages();
//        Message msg = messageList.get(messageList.size() - 1);
//        String username = null;
//        if (!userId.equals(msg.getSenderId()))
//        {
//                username = chat.getCustomerName();
//        }
//
//        if (username != null)
//        {
//            CharSequence name = "QuickConnect";
//            String description = "Channel for Quickconnect notifications";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                channel = new NotificationChannel("notify", name, importance);
//                channel.setDescription(description);
//            }
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                notificationManager.createNotificationChannel(channel);
//            }
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify")
//                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .setContentTitle(username)
//                    .setContentText(msg.getText())
//                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//            notificationManager.notify(200, builder.build());
//        }
//    }