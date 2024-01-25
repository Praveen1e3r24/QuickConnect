//package com.example.quickconnect;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class translate_test extends AppCompatActivity {
//    private boolean userIsInteracting;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        LocaleHelper.loadLocale(this);  // Load the locale first
//        setContentView(R.layout.activity_translate_test);
//
//        Spinner languageSpinner = findViewById(R.id.language_spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.language_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        languageSpinner.setAdapter(adapter);
//
//        // Restore the spinner position
//        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
//        int spinnerPosition = prefs.getInt("Spinner_Position", 0);
//        languageSpinner.setSelection(spinnerPosition, false);  // Set the position without triggering the listener
//
//        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (userIsInteracting) {
//                    String[] languageCodes = getResources().getStringArray(R.array.language_codes);
//                    String selectedLanguageCode = languageCodes[position];
//                    LocaleHelper.setLocale(translate_test.this, selectedLanguageCode, position);
//                    restartApp();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
//
//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();
//        userIsInteracting = true;
//    }
//
//    private void restartApp() {
//        Intent intent = new Intent(getApplicationContext(), Login.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//    }
//}
