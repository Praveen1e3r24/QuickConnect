package com.example.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.OnClickInterface;
import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.ChatAdapter;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Message;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
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


public class Customer_Home_Fragment extends Fragment implements OnClickInterface {

    private User user;
    private String topic = "";
    private DatabaseReference dbRef;

    private List<Chat> chatList = new ArrayList<>();

    private OnClickInterface getInterface() {
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_customer__home_, container, false);
        user = new UserData().getUserDetailsFromSharedPreferences(getContext());
        dbRef = FirebaseDatabase.getInstance().getReference();

        v.findViewById(R.id.newsms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat();
            }
        });

        RecyclerView recyclerView = v.findViewById(R.id.customer_allchats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbRef.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot s : snapshot.getChildren()){
                    Chat chat = s.getValue(Chat.class);
                    chatList.add(chat);
                    }
                if (!chatList.isEmpty())
                {
                    ChatAdapter adapter = new ChatAdapter(getInterface(), chatList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void createChat(){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Toast.makeText(getContext(), "lmai", Toast.LENGTH_SHORT).show();
                    Employee employee = snapshot.getValue(Employee.class);
                    List<Message> messages = new ArrayList<>();
                    if(employee.getAvailable() && employee.getEmployeeRole().equals("M")){
                        Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
                        chat.setChatId(dbRef.child("Chats").push().getKey());
                        dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("chat", chat);
                        startActivity(intent);
                        return;
                    }
                    else {
                        Toast.makeText(getContext(), "No available employees", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Enter topic:");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                topic = input.getText().toString();
                dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(valueEventListener);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onClick(int pos) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chat",chatList.get(pos) );
        startActivity(intent);
    }
}