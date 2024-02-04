package com.example.customer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Message;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.FragmentCustomerMessagingBinding;
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


public class Customer_Messaging_Fragment extends Fragment {

    private EditText issue;
    private Button submit;

    private boolean hasEmployee;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private AutoCompleteTextView departmentSpinner;

    private FragmentCustomerMessagingBinding binding;

    public Customer_Messaging_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer__messaging, container, false);
        binding = FragmentCustomerMessagingBinding.inflate(inflater, container, false);
        if (new DarkModePrefManager(getContext()).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onViewCreated(view, savedInstanceState);

        issue = binding.mIssue;
        submit = binding.mSubmitButton;
        departmentSpinner = binding.mDepartmentSpinner;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.department_array));

        departmentSpinner.setAdapter(adapter);

        submit.setOnClickListener(v -> {
            String issueText = issue.getText().toString();
            String department = departmentSpinner.getText().toString();
            if (issueText.isEmpty() || department.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Your issue has been submitted", Toast.LENGTH_SHORT).show();
            }

            ValueEventListener valueEventListener = checkAndAddChatToDB(issueText, department);

            dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(valueEventListener);
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private ValueEventListener checkAndAddChatToDB(String topic, String department) {
        Log.d(TAG, "showDialogWithResolutionMethod:3 ");
        hasEmployee = false;
        User user = new UserData().getUserDetailsFromSharedPreferences(getContext());

        Log.d(TAG, "showDialogWithResolutionMethod:4 ");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "showDialogWithResolutionMethod:5 ");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Employee employee = snapshot.getValue(Employee.class);
                    Log.d(TAG, "showDialogWithResolutionMethod:6");
                    if (employee != null && employee.getAvailable() && employee.getEmployeeRole().equals("M") && employee.getNumChats() < 5 && employee.getDepartment().equals(department)){
                        Log.d(TAG, "showDialogWithResolutionMethod:7");
                        List<Message> messages = new ArrayList<>();
                        messages.add(new Message(UUID.randomUUID().toString(),employee.getUserId(),user.getUserId(), "Hello! I am " + employee.getFullName() + " from the " + department + " department. How may I help you today?", Date.from(Instant.ofEpochSecond(System.currentTimeMillis()))));
                        Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), department, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
                        chat.setChatId(dbRef.child("Chats").push().getKey());
                        Log.d(TAG, "showDialogWithResolutionMethod:8");
                        dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
                        dbRef.child("Users").child("Employees").child(employee.getUserId()).child("numChats").setValue(employee.getNumChats() + 1);
                        Log.d(TAG, "showDialogWithResolutionMethod:9");
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
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        Log.d("FinalHasEmployee", "Has Employee: " + hasEmployee);
        return valueEventListener;
    }



}