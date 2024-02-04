package com.example.customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.palmchatbot.Nullable;
import com.example.quickconnect.Customer;
import com.example.quickconnect.Employee;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.FragmentSelfProfileBinding;
import com.google.gson.Gson;



public class Self_profile extends Fragment {

 private @NonNull FragmentSelfProfileBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelfProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = getUserDetailsFromSharedPreferences();
        if (user != null) {
            binding.fullName.setText(user.getFullName());
            binding.FirstNameInput.setText(user.getFirstName());
            binding.LastNameInput.setText(user.getLastName());
            binding.EmailInput.setText(user.getEmail());
            binding.numberInput.setText(user.getPhonenumber());
            binding.AddressInput.setText(user.getAddress());
        }

        // Assuming floatingActionButton is a button to close or go back
        binding.floatingActionButton.setOnClickListener(v -> {
getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Customer_Profile_Setting_Fragment()).commit();
        });
    }

    private User getUserDetailsFromSharedPreferences() {
        Context context = getContext(); // getContext() is used to get the context in a fragment
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
            String userJson = sharedPreferences.getString("UserDetails", null);
            if (userJson != null) {
                Gson gson = new Gson();
                User user = gson.fromJson(userJson, User.class);
                if ("Customer".equals(user.getUserType())) {
                    return gson.fromJson(userJson, Customer.class);
                } else if ("Employee".equals(user.getUserType())) {
                    return gson.fromJson(userJson, Employee.class);
                }
            }
        }
        return null;
    }
}
