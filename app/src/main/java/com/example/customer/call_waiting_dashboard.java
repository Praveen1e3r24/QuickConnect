package com.example.customer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.CallRequest;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.R;
import com.example.quickconnect.databinding.ActivityCallWaitingDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class call_waiting_dashboard extends AppCompatActivity {
    ActivityCallWaitingDashboardBinding binding;
    DatabaseReference dbRef;
    CallRequest callRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_waiting_dashboard);

        binding = ActivityCallWaitingDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CallRequest request = getIntent().getParcelableExtra("callRequest");

        binding.dashboardGotochat.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("chatRequest", request);
            intent.putExtra("chat", request.getChat());
            intent.putExtra("isRequest", true);
            startActivity(intent);
            finish();
        });

        binding.dashboardBack.setOnClickListener(v -> {
            finish();
        });

        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("Requests").child(request.getRequestId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    callRequest = snapshot.getValue(CallRequest.class);
                    if (callRequest.getAccepted())
                    {
                        binding.dashboardGotochat.setVisibility(View.VISIBLE);
                        binding.dashboardReady.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(call_waiting_dashboard.this, "An error occurred: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}