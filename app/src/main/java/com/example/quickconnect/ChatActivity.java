package com.example.quickconnect;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickconnect.databinding.ActivityChatBinding;
import com.example.utilities.UserData;
import com.google.firebase.auth.FirebaseAuth;
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

public class ChatActivity extends AppCompatActivity {
    private EditText messageText;

    private Chat chat;

    private DatabaseReference dbRef;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private Boolean firstenter;
    private ActivityChatBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbRef = FirebaseDatabase.getInstance().getReference();

        firstenter = true;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        chat = intent.getParcelableExtra("chat");

        RecyclerView rv = binding.recyclerGchat;
        rv.setLayoutManager(new LinearLayoutManager(this));


        dbRef.child("Chats").child(chat.getChatId()).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot s : snapshot.getChildren()){
                    Message msg = s.getValue(Message.class);
                    messageList.add(msg);

                }
                if (!messageList.isEmpty())
                {
                    adapter = new MessageAdapter(messageList);
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if (firstenter == true)
                    {
                        binding.recyclerGchat.smoothScrollToPosition(adapter.getItemCount() - 1);
                        firstenter = false;
                    }
                    else
                    {
                        binding.recyclerGchat.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (chat.getSupportId() == FirebaseAuth.getInstance().getCurrentUser().getUid())
        {
            actionBar.setTitle(chat.getCustomerId());
            actionBar.setSubtitle("Subject: " + chat.getCategory());
        }
        else
        {
            actionBar.setTitle(chat.getSupportName());
            actionBar.setSubtitle(chat.getSupportTeam());
        }

        messageText = binding.chatMessage;
        messageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });

        binding.chatSend.setOnClickListener(v -> {
            sendMessage();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chatmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage(){
        String message = messageText.getText().toString();
        if (message.isEmpty()) {
            messageText.setError("Please enter a message");
            messageText.requestFocus();
        }
        else {
            String messageId = UUID.randomUUID().toString();
            Message msg = new Message(messageId,FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid().equals(chat.getCustomerId())?chat.getSupportId():chat.getCustomerId(),message, new Date());
            messageList.add(msg);
            
            dbRef.child("Chats").child(chat.getChatId()).child("messages").setValue(messageList);
            messageText.setText("");
        }
    }
}
