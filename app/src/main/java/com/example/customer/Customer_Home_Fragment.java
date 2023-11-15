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
import android.util.Log;
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
import com.google.firebase.database.snapshot.BooleanNode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Customer_Home_Fragment extends Fragment implements OnClickInterface {

    private User user;
    private String topic = "";
    private DatabaseReference dbRef;

    private List<Chat> chatList = new ArrayList<>();


    private Boolean hasEmployee;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Enter topic:");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                topic = input.getText().toString();
                dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(checkAndAddChatToDB());
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

    public interface CheckAgentCallback {
        void onCheckComplete(boolean isValid);
    }


//
@RequiresApi(api = Build.VERSION_CODES.M)
    private ValueEventListener checkAndAddChatToDB() {

        hasEmployee = false;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Employee employee = snapshot.getValue(Employee.class);
                    if (employee != null && employee.getAvailable() && employee.getEmployeeRole().equals("M") && employee.getNumChats() < 5) {
                            List<Message> messages = new ArrayList<>();
                            Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
                            chat.setChatId(dbRef.child("Chats").push().getKey());
                            dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
                            dbRef.child("Users").child("Employees").child(employee.getUserId()).child("numChats").setValue(employee.getNumChats() + 1);
                            hasEmployee = true;
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("chat", chat);
                            startActivity(intent);
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
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        Log.d("FinalHasEmployee", "Has Employee: " + hasEmployee);
        return valueEventListener;
    }

    @Override
    public void onClick(int pos) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chat",chatList.get(pos) );
        startActivity(intent);
    }




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
}