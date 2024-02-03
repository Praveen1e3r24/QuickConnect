package com.example.palmchatbot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.AppPreferences;
import com.example.customer.Customer_Main;
import com.example.quickconnect.Chat;
import com.example.quickconnect.Message;
import com.example.quickconnect.MessageAdapter;
import com.example.quickconnect.R;
import com.example.quickconnect.TranslationService;
import com.example.quickconnect.databinding.ActivityChatbotBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Chatbot_Activity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private ActivityChatbotBinding binding;

    private List<Message> messages = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Chat chat;

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private MessageAdapter messageAdapter;

    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        binding = ActivityChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("QuickConnect ChatBot");

        binding.recyclerGchat.setLayoutManager(new LinearLayoutManager(this));
        EditText messageText = binding.chatbotMessage;

        db.collection("QC_Chatbot_Messages").document(userId).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "An error occured: "+ e, Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                chat = documentSnapshot.toObject(Chat.class);
                messages = chat.getMessages();
                messageAdapter = new MessageAdapter(messages, this);
                binding.recyclerGchat.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
                if (messages.size() > 0)
                {
                    if (firstTime)
                    {
                        firstTime = false;
                        binding.recyclerGchat.smoothScrollToPosition(messages.size() - 1);
                    }
                    else
                    {
                        binding.recyclerGchat.scrollToPosition(messages.size() - 1);
                    }
                }
            }
        });
        messageText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        messageText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        binding.chatbotSend.setOnClickListener(v -> {
            if (messageText.getText().toString().isEmpty()) {
                messageText.setError("Please enter a message");
            }
            else
            {
                sendQuery(messageText.getText().toString());
                messageText.setText("");
            }
        });

        binding.chatbotMic.setOnClickListener(v -> {
            speechToText();
        });

        messageText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (messageText.getText().toString().isEmpty()) {
                    messageText.setError("Please enter a message");
                }
                else
                {
                    sendQuery(messageText.getText().toString());
                    messageText.setText("");
                }
                return true;
            }

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (messageText.getText().toString().isEmpty()) {
                    messageText.setError("Please enter a message");
                }
                else
                {
                    sendQuery(messageText.getText().toString());
                    messageText.setText("");
                }
                return true;
            }

            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (messageText.getText().toString().isEmpty()) {
                    messageText.setError("Please enter a message");
                }
                else
                {
                    sendQuery(messageText.getText().toString());
                    messageText.setText("");
                }
                return true;
            }

            return false;
        });
    }

    private void updateChat() {

        if (messageAdapter == null)
        {
            messageAdapter = new MessageAdapter(messages,this);
            binding.recyclerGchat.setAdapter(messageAdapter);
        }
        else
        {
            messageAdapter.notifyDataSetChanged();
        }

        binding.recyclerGchat.scrollToPosition(messages.size() - 1);
    }

    private void sendQuery(String message) {

        binding.progressBar2.setVisibility(android.view.View.VISIBLE);

        String id = UUID.randomUUID().toString();
        Message msg = new Message(id,userId,"QCChatbot",message, new  Date());
        if (chat == null)
        {
            chat = new Chat(userId,"QCChatbot","QCChatbot","QCChatbot",userId,"QCChatbot","QCChatbot",new Date(),messages,false);
        }

        messages.add(msg);
        chat.setMessages(messages);
        db.collection("QC_Chatbot_Messages").document(userId).set(chat);
        updateChat();

        Map<String, Object> queryData = new HashMap<>();
        queryData.put("prompt", message);

        Message typing = new Message("1","QCChatbot",userId,"", new Date());
        messages.add(typing);
        chat.setMessages(messages);
        db.collection("QC_Chatbot_Messages").document(userId).set(chat);
        updateChat();

        db.collection("QCchatbot").add(queryData).addOnSuccessListener(documentReference -> {

            String docId = documentReference.getId();

            DocumentReference docRef = db.collection("QCchatbot").document(docId);

            docRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    Toast.makeText(this, "An error occured: "+ e, Toast.LENGTH_SHORT).show();
                    binding.progressBar2.setVisibility(View.GONE);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String response = documentSnapshot.getString("response");
                    if (response != null) {
                        messages.remove(messages.size() - 1);
                        Message message1 = new Message(UUID.randomUUID().toString(),"QCChatbot",userId,response, new Date());
                        messages.add(message1);
                        chat.setMessages(messages);
                        updateChat();
                        Toast.makeText(this, "Response Sent!", Toast.LENGTH_SHORT).show();
                        db.collection("QC_Chatbot_Messages").document(userId).set(chat);
                        Log.d("Firestore", "Response: " + response);

                        binding.progressBar2.setVisibility(View.GONE);
                    }
                }
                else
                {
                    Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show();
                    binding.progressBar2.setVisibility(View.GONE);
                }
            });

            updateChat();

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "An error occured: "+ e, Toast.LENGTH_SHORT).show();
            binding.progressBar2.setVisibility(View.GONE);
        });
    }

    public void speechToText()
    {
        Intent intent
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e) {
            Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                binding.chatbotMessage.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatmenu, menu);
        menu.findItem(R.id.customer_info).setVisible(false);
        menu.findItem(R.id.end_chat).setTitle("Clear Chat");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            Intent intent = new Intent(Chatbot_Activity.this, Customer_Main.class);
            startActivity(intent);
            finish();
        }
        else if (itemId == R.id.end_chat) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clear Chat");
            builder.setMessage("Are you sure you want to Clear the chat?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    messages.clear();
                    chat.setMessages(messages);
                    db.collection("QC_Chatbot_Messages").document(userId).set(chat);
                    updateChat();
                    Toast.makeText(Chatbot_Activity.this, "Chat Cleared!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();
        }
        else if (itemId == R.id.change_language) {
            Spinner spinner = new Spinner(this, Spinner.MODE_DIALOG);
            TranslationService translationService = new TranslationService();
            AppPreferences appPreferences = new AppPreferences(this);
            ArrayList<String> languages = translationService.getSupportedLanguages();
            languages.add(0, "None");
            SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
            spinner.setAdapter(spinnerAdapter);
            spinner.setSelection(languages.indexOf(appPreferences.getChatLanguage()));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Language")
                    .setView(spinner)
                    .setPositiveButton("Set Language", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String language = spinner.getSelectedItem().toString();
                            appPreferences.saveChatLanguage(language);
                            messageAdapter.notifyDataSetChanged();
                            Toast.makeText(Chatbot_Activity.this, "Language set to " + language, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog a =  builder.create();
            a.show();
        }

        return super.onOptionsItemSelected(menuItem);
    }
}