package com.example.customer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
        super.onViewCreated(view, savedInstanceState);

        if (new DarkModePrefManager(getContext()).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        LocaleHelper.loadLocale(getActivity());  // Load the locale first

        AutoCompleteTextView languageSpinner = binding.languageSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.language_array, android.R.layout.simple_spinner_dropdown_item);

        languageSpinner.setAdapter(adapter);

        // Restore the spinner position
        SharedPreferences prefs = getContext().getSharedPreferences("Settings", MODE_PRIVATE);
        String selectedLanguage = prefs.getString("Selected_Language", "English");
        languageSpinner.setText(selectedLanguage, false);  // Set the text without triggering the listener

        languageSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            String[] languageCodes = getResources().getStringArray(R.array.language_codes);
            String selectedLanguageCode = languageCodes[position];
            LocaleHelper.setLocale(getActivity(), selectedLanguageCode, position);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Selected_Language", (String) parent.getItemAtPosition(position));
            editor.apply();
            restartApp();
        });

        // To detect the first user interaction
        view.findViewById(R.id.languageSpinnerContainer).setOnClickListener(v -> userIsInteracting = true);
    }

    private void restartApp() {
        Intent intent = new Intent(getContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
