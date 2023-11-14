package com.example.Employee_M;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.OnClickInterface;
import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.ChatAdapter;
import com.example.quickconnect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Employee_M_Home_Fragment extends Fragment implements OnClickInterface {

    private RecyclerView rv;
    private List<Chat> chatList = new ArrayList<>();

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private OnClickInterface getInterface() {
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee__m__home_, container, false);
        rv = v.findViewById(R.id.msupport_home_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        dbRef.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()){
                    Chat chat = s.getValue(Chat.class);
                    if (chat!= null)
                    {
                        chatList.add(chat);
                    }
                }
                if (!chatList.isEmpty())
                {
                    ChatAdapter adapter = new ChatAdapter(getInterface(), chatList);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onClick(int pos) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chat",chatList.get(pos));
        startActivity(intent);
    }
}