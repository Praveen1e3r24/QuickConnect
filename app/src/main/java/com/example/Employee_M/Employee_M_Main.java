package com.example.Employee_M;


import static com.example.quickconnect.R.id.nav_e_m_home;

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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.NotificationHandler;
import com.example.customer.Language_Change;
import com.example.quickconnect.Chat;
import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.LocaleHelper;
import com.example.quickconnect.Login;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.ActivityEmployeeMmainBinding;
import com.example.utilities.UserData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Employee_M_Main extends  AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

         private DrawerLayout drawer;

         private String userId = FirebaseAuth.getInstance().getUid();

         private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


        ActivityEmployeeMmainBinding binding;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                LocaleHelper.loadLocale(this);
                binding = ActivityEmployeeMmainBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());

                binding.navView.setNavigationItemSelectedListener(this);

                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav);

                binding.drawerLayout.addDrawerListener(toggle);
                binding.toolbar.setTitle("Home");

                toggle.syncState();

                if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_M_Home_()).commit();
                binding.navView.setCheckedItem(nav_e_m_home);
                }
                User user = getUserDetailsFromSharedPreferences();

                updateNavHeader(user);

                dbRef.child("Users").child("Employees").child(userId).child("available").setValue(true);

                dbRef.child("Chats").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int count = 0;
                                for (DataSnapshot s : snapshot.getChildren()) {
                                        Chat chat = s.getValue(Chat.class);
                                        if (chat != null && chat.getSupportId().equals(userId) && !chat.getClosed()) {
                                                count ++;
                                        }
                                }
                                dbRef.child("Users").child("Employees").child(userId).child("numChats").setValue(count);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                });

                NotificationHandler notificationHandler = NotificationHandler.getInstance();
                notificationHandler.initialize(this);

                dbRef.child("Chats").addValueEventListener(notificationHandler.checkNewMessage());
        }

//public void createNotification(Chat chat) {
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
//                return;
//        }
//
//        List<Message> messageList = chat.getMessages();
//        Message msg = messageList.get(messageList.size() - 1);
//        String username = null;
//        if (!userId.equals(msg.getSenderId()))
//        {
//                username = chat.getSupportName();
//        }
//
//        if (username != null)
//        {
//                CharSequence name = "QuickConnect";
//                String description = "Channel for Quickconnect notifications";
//                int importance = NotificationManager.IMPORTANCE_DEFAULT;
//                NotificationChannel channel = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        channel = new NotificationChannel("notify", name, importance);
//                        channel.setDescription(description);
//                }
//
//                NotificationManager notificationManager = getSystemService(NotificationManager.class);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        notificationManager.createNotificationChannel(channel);
//                }
//
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify")
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setContentTitle(username)
//                        .setContentText(msg.getText())
//                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//                notificationManager.notify(200, builder.build());
//        }
//}
        @Override
        protected void onResume() {
                super.onResume();
                NotificationHandler notificationHandler = NotificationHandler.getInstance();
                notificationHandler.initialize(this);
                dbRef.child("Users").child("Employees").child(userId).child("available").setValue(true);
        }

        @Override
        protected void onStart() {
                dbRef.child("Users").child("Employees").child(userId).child("available").setValue(true);
                super.onStart();
        }

        @Override
        protected void onDestroy() {
                dbRef.child("Users").child("Employees").child(userId).child("available").setValue(false);
                super.onDestroy();
        }


        @SuppressLint("NonConstantResourceId")

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == nav_e_m_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_M_Home_()).commit();
                } else if (itemId == R.id.nav_e_m_requests) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_M_Requests_Fragment()).commit();
                }  else if (itemId == R.id.nav_e_m_language) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Language_Change()).commit();
                }

                else if (itemId == R.id.nav_e_m_profile_settings) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_M_Settings_Profile_Fragment()).commit();
                }

                else if (itemId == R.id.nav_e_m_logout) {
                        logout();
                        Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
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
                // Redirect to the login screen
                Intent intent = new Intent(Employee_M_Main.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                dbRef.child("Users").child("Employees").child(userId).child("available").setValue(false);
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
                        TextView userEmailTextView = headerView.findViewById(R.id.nav_header_email_e_m);
                        TextView userContactTextView = headerView.findViewById(R.id.nav_header_contact_e_m);

                        // Set user details in the TextViews
                        userEmailTextView.setText(user.getEmail());
                        userContactTextView.setText(user.getFullName());
                }
        }
}

//else if (itemId == R.id.nav_e_m_chatbot) {
//        Intent intent = new Intent(Employee_M_Main.this, Chatbot_Activity.class);
//        startActivity(intent);
//        }
