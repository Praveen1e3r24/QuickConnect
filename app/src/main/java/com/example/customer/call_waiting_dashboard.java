package com.example.customer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.R;
import com.example.quickconnect.databinding.ActivityCallWaitingDashboardBinding;

public class call_waiting_dashboard extends AppCompatActivity {
ActivityCallWaitingDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_waiting_dashboard);
    }
}