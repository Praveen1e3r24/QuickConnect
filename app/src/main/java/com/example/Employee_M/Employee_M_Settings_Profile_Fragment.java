package com.example.Employee_M;

import android.content.Intent;
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

import com.example.customer.DarkModePrefManager;
import com.example.customer.Language_Change;
import com.example.quickconnect.Customer;
import com.example.quickconnect.LocaleHelper;
import com.example.quickconnect.Login;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.FragmentEmployeeMProfileSettingsBinding;
import com.example.utilities.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Employee_M_Settings_Profile_Fragment extends Fragment {

    private Switch darkModeSwitch;
    FragmentEmployeeMProfileSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEmployeeMProfileSettingsBinding.inflate(inflater, container, false);


        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocaleHelper.loadLocale(getActivity());
        if (new DarkModePrefManager(getActivity()).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setDarkModeSwitch();

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
}