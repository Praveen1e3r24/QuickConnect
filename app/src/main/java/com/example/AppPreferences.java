package com.example;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.quickconnect.User;
import com.google.gson.Gson;

public class AppPreferences {

    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_USER_DATA = "user_data";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public AppPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveUserData(User user) {
        String userDataJson = gson.toJson(user);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_DATA, userDataJson);
        editor.apply();
    }

    public User getUserData() {
        String userDataJson = sharedPreferences.getString(KEY_USER_DATA, null);
        if (userDataJson != null) {
            return gson.fromJson(userDataJson, User.class);
        } else {
            return null;
        }
    }

    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_DATA);
        editor.apply();
    }

    public void clearSharedPrefrences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveNotificationStatus(Boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificationStatus", status);
        editor.apply();
    }

    public Boolean getNotificationStatus() {
        return sharedPreferences.getBoolean("notificationStatus", true);
    }

    public void saveChatLanguage(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("chatLanguage", language);
        editor.apply();
    }

    public String getChatLanguage() {
        return sharedPreferences.getString("chatLanguage", "None");
    }
}
