package com.example.customer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.utilities.UserData;


public class Customer_Home_Fragment extends Fragment {

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_customer__home_, container, false);
        user = new UserData().getUserDetailsFromSharedPreferences(getContext());

        return v;
    }

    private void createChat(){

    }


}