package com.example.quickconnect;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Activity activity, String lang, int spinnerPosition) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = activity.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.putInt("Spinner_Position", spinnerPosition);
        editor.apply();
    }

    public static void loadLocale(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (!language.isEmpty()) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
        }
    }

    public static int getSpinnerPosition(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("Settings", MODE_PRIVATE);
        return prefs.getInt("Spinner_Position", 0); // Default to position 0
    }


}
