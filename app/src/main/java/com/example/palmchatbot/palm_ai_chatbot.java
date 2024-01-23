package com.example.palmchatbot;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.R;
import com.example.quickconnect.databinding.ActivityPalmAiChatbotBinding;
import com.example.speechtotext.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class palm_ai_chatbot extends AppCompatActivity {
    ActivityPalmAiChatbotBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText input ;
    Button send ;
    TextView response1 ;

    private ImageView iv_mic;
    private TextView tv_Speech_to_text;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Firestore", "Listen failed.1");
        setContentView(R.layout.activity_palm_ai_chatbot);
        binding = ActivityPalmAiChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        input= binding.InputAi;
        send = binding.sendAiBtn;
        response1 = binding.aiResponse;



        send.setOnClickListener(v -> {
            if (input.getText().toString().isEmpty()){
                Log.e("Firestore", "Listen failed.5");
                input.setError("Please enter a message");

            }
            else {
                send.setEnabled(false);
                Log.e("Firestore", "Listen failed.6");
                sendrequestai(input.getText().toString());

            }
        });


        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Perform actions for long click
                // Return true to indicate that the long click event is consumed
                doSomethingDifferent();
                return true;
            }
        });






    }

    public void doSomethingDifferent(){

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
            Toast.makeText(palm_ai_chatbot.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void sendrequestai(String message){
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("prompt", message);

        db.collection("QCchatbot")
                .add(chatData)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    String documentId = documentReference.getId();
                    // Do something with the document ID if needed
                    DocumentReference docRef = db.collection("QCchatbot").document(documentId);

                    docRef.addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            Log.e("Firestore", "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String response = documentSnapshot.getString("response");
                            if (response != null) {
                                response1.setText(response);
                                send.setEnabled(true);
                                Log.d("Firestore", "Response: " + response);
                                // Handle the response as needed in your app
                            }
                        }
                    });



                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occurred during the operation
                    Log.e("Firestore", "Error adding document", e);
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                input.setText(
                        Objects.requireNonNull(result).get(0));

                sendrequestai(Objects.requireNonNull(result).get(0));
            }
        }
    }
}