package com.example.customer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.quickconnect.LocaleHelper;
import com.example.quickconnect.Login;
import com.example.quickconnect.R;
import com.example.quickconnect.databinding.FragmentLanguageChangeBinding;

public class Language_Change extends Fragment {


    FragmentLanguageChangeBinding binding;
    private boolean userIsInteracting;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLanguageChangeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (new DarkModePrefManager(getContext()).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onViewCreated(view, savedInstanceState);
        LocaleHelper.loadLocale(getActivity());  // Load the locale first


        Spinner languageSpinner = binding.languageSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Restore the spinner position
        SharedPreferences prefs = getContext().getSharedPreferences("Settings", MODE_PRIVATE);
        int spinnerPosition = prefs.getInt("Spinner_Position", 0);
        languageSpinner.setSelection(spinnerPosition, false);  // Set the position without triggering the listener
        languageSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userIsInteracting = true;
                return false; // let the event pass to the spinner
            }
        });
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (userIsInteracting) {
                    String[] languageCodes = getResources().getStringArray(R.array.language_codes);
                    String selectedLanguageCode = languageCodes[position];
                    LocaleHelper.setLocale(getActivity(), selectedLanguageCode, position);
                    restartApp();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    public void setUserIsInteracting(boolean isInteracting) {
        this.userIsInteracting = isInteracting;
    }



    private void restartApp() {
        Intent intent = new Intent(getContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}