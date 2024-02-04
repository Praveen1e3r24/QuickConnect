package com.example.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.AppPreferences;
import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.LocaleHelper;
import com.example.quickconnect.Login;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.FragmentCustomerProfileSettingBinding;
import com.example.utilities.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;


public class Customer_Profile_Setting_Fragment extends Fragment {
    private Switch darkModeSwitch;
    private Switch notificationSwitch;
    FragmentCustomerProfileSettingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocaleHelper.loadLocale(getActivity());

        binding = FragmentCustomerProfileSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (new DarkModePrefManager(getActivity()).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setDarkModeSwitch();
        setNotifcationSwitch();
        User user = getUserDetailsFromSharedPreferences();


        binding.usernameTextView.setText(user.getFullName());
        binding.profileRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new Self_profile() ).commit();
            }
        });

        binding.languageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new Language_Change()).commit();
            }
        });
        binding.logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout tasks here
                new UserData().removeUserDetails(getActivity());
                // Example: Clear Firebase Authentication
                FirebaseAuth.getInstance().signOut();
                ZegoUIKitPrebuiltCallInvitationService.unInit();
                // Redirect to the login screen
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });


    }

    private User getUserDetailsFromSharedPreferences() {
        // Obtain SharedPreferences instance
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);

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


    private void setDarkModeSwitch() {
        darkModeSwitch = binding.darkModeSwitch;
        darkModeSwitch.setChecked(new DarkModePrefManager(getActivity()).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DarkModePrefManager darkModePrefManager = new DarkModePrefManager(getActivity());
                darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                AppCompatDelegate.setDefaultNightMode(
                        isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
               getActivity().recreate();
                Toast.makeText(requireContext(), "Dark Mode Turned " + (isChecked?"On":"Off"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNotifcationSwitch() {
        notificationSwitch = binding.notificationsSwitch;
        AppPreferences appPreferences = new AppPreferences(getActivity());

        notificationSwitch.setChecked(appPreferences.getNotificationStatus());
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appPreferences.saveNotificationStatus(isChecked);
                Toast.makeText(requireContext(), "Notifications Turned " + (isChecked?"On":"Off"), Toast.LENGTH_SHORT).show();
            }
        });
    }





}
