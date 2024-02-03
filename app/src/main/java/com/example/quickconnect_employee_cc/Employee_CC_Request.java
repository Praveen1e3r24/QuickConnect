package com.example.quickconnect_employee_cc;

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
import com.example.quickconnect.CallRequest;
import com.example.quickconnect.ChatAdapter;
import com.example.quickconnect.ChatRequestItem;
import com.example.quickconnect.Customer_Profile;
import com.example.quickconnect.R;
import com.example.quickconnect.databinding.FragmentEmployeeHomeBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Employee_CC_Request extends Fragment implements OnClickInterface {
    private RecyclerView rv;
    private List<ChatRequestItem> chatRequestItemList= new ArrayList<>();

    private List<ChatRequestItem> filteredList = new ArrayList<>();

    private FragmentEmployeeHomeBinding binding;

    private ChatAdapter chatAdapter;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private OnClickInterface getInterface() {
        return this;
    }

    private MaterialButtonToggleGroup filterSituation;

    private MaterialButtonToggleGroup filterAccepted;


    String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee__home_, container, false);
        rv = v.findViewById(R.id.csSupport_home);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        filterSituation = v.findViewById(R.id.cs_filter_situation_grp);
        filterAccepted = v.findViewById(R.id.filter_acceptance_grp);

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
                chatAdapter = new ChatAdapter(getInterface(), chatRequestItemList);
                rv.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dbRef.child("Users").child("Employees").child(userId).child("available").setValue(true);

        filterSituation.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                applyFilters();
            }
            else {
                resetFilters();
            }
        });

        filterAccepted.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                applyFilters();
            }
            else {
                resetFilters();
            }
        });

        return v;
    }

    @Override
    public void onClick(int pos, ChatRequestItem chatRequestItem) {
        ChatRequestItem requestItem = null;
        if (filteredList.size() > 0) {
            requestItem = filteredList.get(pos);
        } else {
            requestItem = chatRequestItemList.get(pos);
        }

        if (requestItem != null){
            Intent intent = new Intent(getActivity(), Customer_Profile.class);
            CallRequest request = chatRequestItemList.get(pos).getCallRequest();
            intent.putExtra("chatRequest", request);
            startActivity(intent);
        }

    }

    private void resetFilters() {
        chatAdapter = new ChatAdapter(getInterface(), chatRequestItemList);
        rv.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        filteredList.clear();
    }

    private void applyFilters() {
        filteredList.clear();

        for (ChatRequestItem item : chatRequestItemList) {

            boolean isUnresolved = (!item.getCallRequest().getClosed() || !item.getCallRequest().getChat().getClosed());
            boolean isResolved = (item.getCallRequest().getClosed() || item.getCallRequest().getChat().getClosed());
            boolean isAccepted = (item.getCallRequest().getAccepted());
            boolean isUnaccepted = (!item.getCallRequest().getAccepted());

            boolean addToList = true;


            if (filterAccepted.getCheckedButtonId() == R.id.filter_accepted && !isAccepted) {
                addToList = false;
            } else if (filterAccepted.getCheckedButtonId() == R.id.filter_unaccepted && !isUnaccepted) {
                addToList = false;
            }

            if (filterSituation.getCheckedButtonId() == R.id.cs_filter_unresolved && !isUnresolved) {
                addToList = false;
            } else if (filterSituation.getCheckedButtonId() == R.id.cs_filter_resolved && !isResolved) {
                addToList = false;
            }

            if (addToList) {
                filteredList.add(item);
            }
        }

        chatAdapter = new ChatAdapter(getInterface(), filteredList);
        RecyclerView rv = getView().findViewById(R.id.csSupport_home);
        rv.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }
}