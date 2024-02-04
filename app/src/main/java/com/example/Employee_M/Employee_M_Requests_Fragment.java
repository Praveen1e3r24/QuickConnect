package com.example.Employee_M;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.OnClickInterface;
import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.ChatAdapter;
import com.example.quickconnect.ChatRequestItem;
import com.example.quickconnect.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Employee_M_Requests_Fragment extends Fragment implements OnClickInterface {

    private RecyclerView rv;
    private List<ChatRequestItem> chatRequestItemList = new ArrayList<>();

    private List<ChatRequestItem> filteredList = new ArrayList<>();

    private MaterialButtonToggleGroup toggleGroup;

    private ChatAdapter adapter;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private OnClickInterface getInterface() {
        return this;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee__m__requests_, container, false);
        rv = v.findViewById(R.id.msupport_home_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        toggleGroup = v.findViewById(R.id.m_filter_situation_grp);

        dbRef.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatRequestItemList.clear();
                for (DataSnapshot s : snapshot.getChildren()){
                    Chat chat = s.getValue(Chat.class);
                    if (chat!= null && chat.getSupportId() .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) )
                    {
                        chatRequestItemList.add(new ChatRequestItem(chat, null));
                    }
                }

                if (!filteredList.isEmpty()) {
                    applyFilters();
                    return;
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

        toggleGroup.setSingleSelection(true);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                applyFilters();

        });

        return v;
    }

    @Override
    public void onClick(int pos, ChatRequestItem chatRequestItem) {
        Chat chat = chatRequestItemList.get(pos).getChat();
        if (!filteredList.isEmpty()) {
            chat = filteredList.get(pos).getChat();
        }
        Intent intent = new Intent(getActivity(), ChatActivity.class);;
        intent.putExtra("chat", chat);
        startActivity(intent);

    }

    private void resetFilters() {
        adapter = new ChatAdapter(getInterface(), chatRequestItemList);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        filteredList.clear();
    }

    private void applyFilters() {
        filteredList.clear();

        for (ChatRequestItem item : chatRequestItemList) {

            boolean isUnresolved = (!item.getChat().getClosed());
            boolean isResolved = (item.getChat().getClosed());

            boolean addToList = true;

            if (toggleGroup.getCheckedButtonId() == R.id.m_filter_unresolved && !isUnresolved) {
                addToList = false;
            } else if (toggleGroup.getCheckedButtonId() == R.id.m_filter_resolved && !isResolved) {
                addToList = false;
            }

            if (addToList) {
                filteredList.add(item);
            }
        }

        adapter = new ChatAdapter(getInterface(), filteredList);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
