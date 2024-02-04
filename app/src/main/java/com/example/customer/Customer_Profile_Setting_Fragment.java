package com.example.customer;

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
import com.example.quickconnect.LocaleHelper;
import com.example.quickconnect.R;
import com.example.quickconnect.databinding.FragmentCustomerProfileSettingBinding;


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

        binding.languageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new Language_Change()).commit();
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