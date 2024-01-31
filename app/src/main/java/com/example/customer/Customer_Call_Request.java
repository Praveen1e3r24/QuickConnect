package com.example.customer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.quickconnect.CallRequest;
import com.example.quickconnect.Chat;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Message;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.FragmentCustomerCallRequestBinding;
import com.example.utilities.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Customer_Call_Request extends Fragment {

    FragmentCustomerCallRequestBinding binding;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private boolean hasEmployee;

    String topic;

    private ImageButton voiceCallButton;
    private ImageButton videoCallButton;
    private boolean isVoiceCallSelected = false;
    private boolean isVideoCallSelected = false;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomerCallRequestBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.voiceCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVoiceCallSelected = true;
                isVideoCallSelected = false; // Add this line
                updateButtonStates();
            }
        });

        binding.videoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVoiceCallSelected = false;
                isVideoCallSelected = true; // Add this line
                updateButtonStates();
            }
        });


        binding.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.departmentSpinner.getSelectedItem().toString().equals("Select Department")){
                    binding.departmenterrorText.setVisibility(View.VISIBLE);
                    return;
                } else if (binding.callDescription.getText().toString().trim().isEmpty()) {
                    binding.descriptionerrorText.setVisibility(View.VISIBLE);
                    return;

                }  else if (!isVoiceCallSelected && !isVideoCallSelected) { // Add this check
                    binding.callTypeerrorText.setVisibility(View.VISIBLE);
                    return;
                } else {

                    binding.descriptionerrorText.setVisibility(View.GONE);
                    binding.departmenterrorText.setVisibility(View.GONE);
                    String department = binding.departmentSpinner.getSelectedItem().toString();
                    topic=binding.descriptionText.getText().toString();
                    dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(addRequestToDB(topic, department ));

                }
            }
        });

    }






    private ValueEventListener addRequestToDB(String topic, String department){
        hasEmployee = false;
        if (topic == null) {
            topic = "General Inquiries";
        }
        String finalTopic = topic;
        ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Employee employee = s.getValue(Employee.class);
                    if (employee != null && employee.getAvailable() && employee.getEmployeeRole().equals("CS")) {
                        hasEmployee = true;
                        User user = new UserData().getUserDetailsFromSharedPreferences(getContext());
                        String query = binding.descriptionText.getText().toString();

                        dbRef.child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int queueNo = 0;

                                if (snapshot.exists()) {
                                    queueNo = (int) snapshot.getChildrenCount();
                                }
                                List<Message> messages = new ArrayList<>();
                                Chat chat = new Chat(UUID.randomUUID().toString(), employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), finalTopic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
                                CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(), user.getUserId(), user.getFullName(), employee.getUserId(), employee.getFullName(), chat, query, finalTopic, queueNo, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), false, false);
                                callRequest.setRequestId(dbRef.child("Requests").push().getKey());
                                callRequest.getChat().setCallRequestId(callRequest.getRequestId());
                                dbRef.child("Requests").child(callRequest.getRequestId()).setValue(callRequest);
                                dbRef.child("Users").child("Employees").child(employee.getUserId()).child("available").setValue(false);
                                dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
                                Toast.makeText(getContext(), "Call Request Sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), call_waiting_dashboard.class);
                                intent.putExtra("callRequest", callRequest);
                                startActivity(intent);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }

                if (!hasEmployee)
                {
                    Toast.makeText(getContext(), "No Available Employees", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        return eventListener;
    }

    private void updateButtonStates() {
        binding.voiceCallButton.setSelected(isVoiceCallSelected);

        binding.videoCallButton.setSelected(!isVoiceCallSelected);
    }







}