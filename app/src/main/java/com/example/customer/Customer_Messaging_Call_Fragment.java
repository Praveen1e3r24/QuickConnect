package com.example.customer;

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
import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.ChatAdapter;
import com.example.quickconnect.ChatRequestItem;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.FragmentCustomerMessagingCallBinding;
import com.example.utilities.UserData;
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


public class Customer_Messaging_Call_Fragment extends Fragment implements OnClickInterface {

    private User user;
    private String topic = "";
    private String query = "";
    private DatabaseReference dbRef;

    private List<ChatRequestItem> chatRequestItemList = new ArrayList<>();

    private List<ChatRequestItem> filteredList = new ArrayList<>();
    private Boolean hasEmployee;
    private FragmentCustomerMessagingCallBinding binding;

    private ChatAdapter chatAdapter;

    private OnClickInterface getInterface() {
        return this;
    }

    private MaterialButtonToggleGroup filterChatsRequestsGrp;

    private MaterialButtonToggleGroup filterSituation;

    private RecyclerView chatRV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_customer_messaging_call, container, false);
        binding = FragmentCustomerMessagingCallBinding.inflate(inflater, container, false);
        user = new UserData().getUserDetailsFromSharedPreferences(getContext());
        dbRef = FirebaseDatabase.getInstance().getReference();

        filterChatsRequestsGrp = v.findViewById(R.id.filter_chats_requests_grp);
        filterSituation = v.findViewById(R.id.filter_situation_grp);
        chatRV = v.findViewById(R.id.customer_allchats);

        RecyclerView chatRV = v.findViewById(R.id.customer_allchats);
        chatRV.setLayoutManager(new LinearLayoutManager(getContext()));
        dbRef.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<ChatRequestItem> itemsToRemove = new ArrayList<>();
                for (ChatRequestItem item : chatRequestItemList) {
                    if (item.isChat()) {
                        itemsToRemove.add(item);
                    }
                }
                // Remove all collected items
                chatRequestItemList.removeAll(itemsToRemove);

                for (DataSnapshot s : snapshot.getChildren()) {
                    Chat chat = s.getValue(Chat.class);
                    if (chat.getCallRequestId() == null && chat.getCustomerId().equals(FirebaseAuth.getInstance().getUid())) {
                        ChatRequestItem chatRequestItem = new ChatRequestItem(chat,null);
                        chatRequestItemList.add(chatRequestItem);
                    }
                }

                if (filteredList.size() > 0) {
                    applyFilters();
                }
//                chatRV.setAdapter(chatAdapter);
//                chatAdapter.notifyDataSetChanged();
                if (chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();
                }
                else {
                    chatAdapter = new ChatAdapter(getInterface(), chatRequestItemList);
                    chatRV.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dbRef.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<ChatRequestItem> itemsToRemove = new ArrayList<>();
                for (ChatRequestItem item : chatRequestItemList) {
                    if (item.isCallRequest()) {
                        itemsToRemove.add(item);
                    }
                }

                // Remove all collected items
                chatRequestItemList.removeAll(itemsToRemove);

                for (DataSnapshot s : snapshot.getChildren()) {
                    CallRequest request = s.getValue(CallRequest.class);
                    if (Objects.requireNonNull(request).getCustomerId().equals(FirebaseAuth.getInstance().getUid())) {
                        ChatRequestItem chatRequestItem = new ChatRequestItem(null, request);
                        chatRequestItemList.add(chatRequestItem);
                    }

                }

                if (filteredList.size() > 0) {
                    applyFilters();
                }

                if (chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();
                }
                else {
                    chatAdapter = new ChatAdapter(getInterface(), chatRequestItemList);
                    chatRV.setAdapter(chatAdapter);
                }

//                ChatAdapter adapter = new ChatAdapter(getInterface(), null, callRequestList);
//                chatRV.setAdapter(adapter)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        filterChatsRequestsGrp.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            applyFilters();
        });

        MaterialButtonToggleGroup filterSituation = v.findViewById(R.id.filter_situation_grp);


        filterSituation.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            applyFilters();
        });

        return v;
    }

    @Override
    public void onClick(int pos, ChatRequestItem o) {

        if (o.isCallRequest()) {
            CallRequest request;
            if (filteredList.size() > 0) {
                request = filteredList.get(pos).getCallRequest();
            } else {
                request = chatRequestItemList.get(pos).getCallRequest();
            }

            if (request == null) {
                return;}
            Intent intent = new Intent(this.getActivity(), call_waiting_dashboard.class);
            intent.putExtra("chat", request.getChat());
            intent.putExtra("callRequest", request);
            startActivity(intent);
        } else if (o.isChat()) {
            Chat chat;
            if (filteredList.size() > 0) {
                chat = filteredList.get(pos).getChat();
            } else {
                chat = chatRequestItemList.get(pos).getChat();
            }
            if (chat == null)
            { return; }
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("chat", chat);
            intent.putExtra("isChat", true);
            startActivity(intent);

        }
    }

    private void resetFilters() {
        chatAdapter = new ChatAdapter(getInterface(), chatRequestItemList);
        chatRV.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        filteredList.clear();
    }

    private void applyFilters() {
        filteredList.clear();

        for (ChatRequestItem item : chatRequestItemList) {
            boolean isChat = item.isChat();
            boolean isRequest = item.isCallRequest();
            boolean isUnresolved = (isChat && !item.getChat().getClosed()) || (isRequest && !item.getCallRequest().getClosed());
            boolean isResolved = (isChat && item.getChat().getClosed()) || (isRequest && item.getCallRequest().getClosed());

            boolean addToList = true;

            if (filterChatsRequestsGrp.getCheckedButtonId() == R.id.filter_chats && !isChat) {
                addToList = false;
            } else if (filterChatsRequestsGrp.getCheckedButtonId() == R.id.filter_requests && !isRequest) {
                addToList = false;
            }

            if (filterSituation.getCheckedButtonId() == R.id.filter_unresolved && !isUnresolved) {
                addToList = false;
            } else if (filterSituation.getCheckedButtonId() == R.id.filter_resolved && !isResolved) {
                addToList = false;
            }

            if (addToList) {
                filteredList.add(item);
            }
        }

        chatAdapter = new ChatAdapter(getInterface(), filteredList);
        chatRV.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }
}

//filterChatsRequestsGrp.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
//@Override
//public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
//        if (isChecked) {
//        Toast.makeText(getContext(), "text", Toast.LENGTH_SHORT).show();
//        if (checkedId == R.id.filter_chats) {
//        binding.filterChats.callOnClick();
//        Toast.makeText(getContext(), "Chats", Toast.LENGTH_SHORT).show();
//        filteredList.clear();
//        group.setSingleSelection(true);
//        for (ChatRequestItem item : chatRequestItemList) {
//        if (item.isChat()) {
//        filteredList.add(item);
//        }
//        }
//        chatAdapter = new ChatAdapter(getInterface(), filteredList);
//        chatRV.setAdapter(chatAdapter);
//        chatAdapter.notifyDataSetChanged();
//        filteredList.clear();
//        group.setSingleSelection(true);
//        for (ChatRequestItem item : chatRequestItemList) {
//        if (item.isCallRequest()) {
//        filteredList.add(item);
//        }
//        }
//        chatAdapter = new ChatAdapter(getInterface(), filteredList);
//        chatRV.setAdapter(chatAdapter);
//        chatAdapter.notifyDataSetChanged();
//        }
//        }
//        }
//        });

//    private void createChat() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
//        builder.setTitle("Enter topic:");
//        final EditText input = new EditText(this.getContext());
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//        builder.setView(input);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                topic = input.getText().toString();
//                dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(checkAndAddChatToDB());
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }
//
//
//    //
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private ValueEventListener checkAndAddChatToDB() {
//
//        hasEmployee = false;
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Employee employee = snapshot.getValue(Employee.class);
//                    if (employee != null && employee.getAvailable() && employee.getEmployeeRole().equals("M") && employee.getNumChats() < 5) {
//                        List<Message> messages = new ArrayList<>();
//                        Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
//                        chat.setChatId(dbRef.child("Chats").push().getKey());
//                        dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
//                        dbRef.child("Users").child("Employees").child(employee.getUserId()).child("numChats").setValue(employee.getNumChats() + 1);
//                        hasEmployee = true;
//                        Intent intent = new Intent(getActivity(), ChatActivity.class);
//                        intent.putExtra("chat", chat);
//                        startActivity(intent);
//                        break;
//                    }
//                }
//                if (!hasEmployee) {
//                    Toast.makeText(getContext(), "No Available Employees", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        Log.d("FinalHasEmployee", "Has Employee: " + hasEmployee);
//        return valueEventListener;
//    }

//    private void createCallRequest(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
//
//        // Create a LinearLayout to hold the EditTexts
//        LinearLayout layout = new LinearLayout(this.getContext());
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        // Create the first EditText
//        EditText editText1 = new EditText(this.getContext());
//        editText1.setHint("Query");
//        layout.addView(editText1);
//
//        // Create the second EditText
//        EditText editText2 = new EditText(this.getContext());
//        editText2.setHint("Category");
//        layout.addView(editText2);
//
//        builder.setView(layout)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        query = editText1.getText().toString();
//                        topic = editText2.getText().toString();
//                        dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(addRequestToDB());
//                    }
//                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Handle the button click, if needed
//                        dialog.dismiss();
//                }});
//        builder.create().show();
//    }
//
//    private ValueEventListener addRequestToDB(){
//        hasEmployee = false;
//        ValueEventListener eventListener = new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot s : snapshot.getChildren()) {
//                    Employee employee = s.getValue(Employee.class);
//                    Log.d("EMPLOYEE ID LMAO", employee.getEmployeeRole());
//                    if (employee != null && employee.getAvailable() && employee.getEmployeeRole().equals("CS")) {
//                        hasEmployee = true;
//
//                        dbRef.child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                int queueNo = 0;
//
//                                if (snapshot.exists()) {
//                                    queueNo = (int) snapshot.getChildrenCount();
//                                }
//                                List<Message> messages = new ArrayList<>();
//                                Chat chat = new Chat(UUID.randomUUID().toString(), employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
//                                CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(), user.getUserId(), user.getFullName(), employee.getUserId(), employee.getFullName(), chat, query, topic, queueNo, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), false, false);
//                                callRequest.setRequestId(dbRef.child("Requests").push().getKey());
//                                callRequest.getChat().setCallRequestId(callRequest.getRequestId());
//                                dbRef.child("Requests").child(callRequest.getRequestId()).setValue(callRequest);
//                                dbRef.child("Users").child("Employees").child(employee.getUserId()).child("available").setValue(false);
//                                dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
//                                Toast.makeText(getContext(), "Call Request Sent", Toast.LENGTH_SHORT).show();
////                                Intent intent = new Intent(getActivity(), ChatActivity.class);
////                                intent.putExtra("callRequest", callRequest);
////                                startActivity(intent);
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        break;
//                    }
//                }
//
//                if (!hasEmployee)
//                {
//                    Toast.makeText(getContext(), "No Available Employees", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//
//
//        return eventListener;
//    }
//}
//    public interface CheckAgentCallback {
//        void onCheckComplete(boolean isValid);
//    }


// dbRef.child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot snapshot) {
//        int queueNo = 0;
//        if (snapshot.exists()) {
//        queueNo = (int) snapshot.getChildrenCount();
//        }
//        CallRequest callRequest = new CallRequest("", user.getUserId(), user.getFullName(), "", "", null, query, category, queueNo, false, false);
//        callRequest.setRequestId(dbRef.child("Requests").push().getKey());
//        dbRef.child("Requests").child(callRequest.getRequestId()).setValue(callRequest);
//        Toast.makeText
//        }
//        })
//        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialog, int which) {
//        dialog.dismiss();
//        }
//        });





//    private ValueEventListener checkAndAddChatToDB(){
//        hasEmployee = false;
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Employee employee = snapshot.getValue(Employee.class);
//                    if (employee == null) {
//                        continue;
//                    }
//
//                    Log.d("EmployeeData", "Employee Role: " + employee.getEmployeeRole());
//                    Log.d("EmployeeData", "Num Chats: " + employee.getNumChats());
//
//                    if (employee.getAvailable() && employee.getEmployeeRole().equals("M") && employee.getNumChats() < 5){
//                        CheckAgentCallback callback = new CheckAgentCallback() {
//                            @RequiresApi(api = Build.VERSION_CODES.O)
//                            public Boolean checkAgent(Employee employee) {
//                                final Boolean[] isValid = {false};
//                                dbRef.child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if (snapshot.exists())
//                                        {
//                                            for (DataSnapshot s : snapshot.getChildren()) {
//                                                Chat schat = s.getValue(Chat.class);
//                                                Log.d("EMPLOYEE ID", employee.getUserId());
//                                                Log.d("CHAT SUPPORT ID", schat.getSupportId());
//                                                if ((!schat.getCustomerId().equals(user.getUserId()) && !employee.getUserId().equals(schat.getSupportId())) || (schat == null) ){
//                                                    isValid[0] = true;
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                        else{
//                                            isValid[0] = false;
//                                        }
//
//                                        if (isValid[0] == true)
//                                        {
//                                            List<Message> messages = new ArrayList<>();
//                                            Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
//                                            chat.setChatId(dbRef.child("Chats").push().getKey());
//                                            dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
//                                            dbRef.child("Users").child("Employees").child(employee.getUserId()).child("numChats").setValue(employee.getNumChats() + 1);
//                                            Intent intent = new Intent(getActivity(), ChatActivity.class);
//                                            intent.putExtra("chat", chat);
//                                            startActivity(intent);
//                                        }
//
//                                    }
//
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//
//                                return isValid[0];
//                            }
//                        };
//
//                        Boolean isValid =  callback.checkAgent(employee);
//                        hasEmployee = isValid;
//
//                        if (isValid){
//                            break;
//                        }
//
//                    }
//                }
//                if (!hasEmployee){
//                    Toast.makeText(getContext(), "No available employees", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        Log.d("FinalHasEmployee", "Has Employee: " + hasEmployee);
//        return valueEventListener;
//    }

//    private void checkAgent(Employee employee, CheckAgentCallback callback) {
//
//        dbRef.child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean isValid = false;
//                if (snapshot.exists()) {
//                    for (DataSnapshot s : snapshot.getChildren()) {
//                        Chat schat = s.getValue(Chat.class);
//                        if (schat != null && !schat.getCustomerId().equals(user.getUserId()) && !employee.getUserId().equals(schat.getSupportId())) {
//                            isValid = true;
//                            break;
//                        }
//                    }
//                }
//                else
//                {
//                    isValid = true;
//                }
//
//                if (isValid) {
//                    List<Message> messages = new ArrayList<>();
//                    Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
//                    chat.setChatId(dbRef.child("Chats").push().getKey());
//                    dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
//                    dbRef.child("Users").child("Employees").child(employee.getUserId()).child("numChats").setValue(employee.getNumChats() + 1);
//                    Intent intent = new Intent(getActivity(), ChatActivity.class);
//                    intent.putExtra("chat", chat);
//                    startActivity(intent);
//                }
//                callback.onCheckComplete(isValid);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle onCancelled
//            }
//        });
//    }





//    List<Message> messages = new ArrayList<>();
//    Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
//                                chat.setChatId(dbRef.child("Chats").push().getKey());
//                                dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
//                                dbRef.child("Users").child("Employees").child(employee.getUserId()).child("numChats").setValue(employee.getNumChats() + 1);
//    Intent intent = new Intent(getActivity(), ChatActivity.class);
//                                intent.putExtra("chat", chat);
//    startActivity(intent);


//    dbRef.child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot s : snapshot.getChildren()) {
//
//                                    Chat schat = s.getValue(Chat.class);
//                                    if ((!schat.getCustomerId().equals(user.getUserId()) && !employee.getUserId().equals(schat.getSupportId())) || (schat == null) ) {
//                                            Log.d("CHAT SUPPORT ID", schat.getSupportId());
//                                            Log.d("EMPLOYEE ID", employee.getUserId());
//                                            List<Message> messages = new ArrayList<>();
//                                            Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
//                                            chat.setChatId(dbRef.child("Chats").push().getKey());
//                                            dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
//                                            dbRef.child("Users").child("Employees").child(employee.getUserId()).child("numChats").setValue(employee.getNumChats() + 1);
//                                            Intent intent = new Intent(getActivity(), ChatActivity.class);
//                                            intent.putExtra("chat", chat);
//                                            startActivity(intent);
//                                            hasEmployee = true;
//                                            break;
//                                    }
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });


//    private interface CheckAgentCallback {
//
//        Boolean checkAgent(Employee employee);
//    }
