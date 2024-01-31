package com.example.quickconnect_employee_cc;

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
import com.example.quickconnect.CallRequest;
import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.ChatAdapter;
import com.example.quickconnect.ChatRequestItem;
import com.example.quickconnect.Customer_Profile;
import com.example.quickconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Employee_Home_Fragment extends Fragment implements OnClickInterface {
    private RecyclerView rv;
    private List<ChatRequestItem> chatRequestItemList= new ArrayList<>();

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private OnClickInterface getInterface() {
        return this;
    }

    String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee__home_, container, false);
        rv = v.findViewById(R.id.csSupport_home);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));


        dbRef.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatRequestItemList.clear();
                for (DataSnapshot s : snapshot.getChildren()){
                    CallRequest callRequest = s.getValue(CallRequest.class);
                    if (callRequest!= null && callRequest.getSupportId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        chatRequestItemList.add(new ChatRequestItem(null, callRequest));
                    }
                }
                ChatAdapter adapter = new ChatAdapter(getInterface(), chatRequestItemList);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dbRef.child("Users").child("Employees").child(userId).child("available").setValue(true);
        return v;
    }

    @Override
    public void onClick(int pos, ChatRequestItem chatRequestItem) {
        Intent intent = new Intent(getActivity(), Customer_Profile.class);
        CallRequest request = chatRequestItemList.get(pos).getCallRequest();
        intent.putExtra("chatRequest", request);
        startActivity(intent);
    }
}