package com.example.palmchatbot;

import android.os.Handler;
import android.os.Looper;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Palm_Ai_Service {
    FirebaseFirestore db ;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 2000; // 2 seconds


    public Palm_Ai_Service() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }
    public interface AIResponseCallback {
        void onReceived(String response);
        void onError(Exception e);
    }

    public void sendrequestai(String message, String Language, AIResponseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("prompt", message);
        db.collection("QC_Chatbot_Messages")
                .add(chatData)
                .addOnSuccessListener(documentReference -> {
                    awaitResponse(documentReference.getId(), 0, callback);
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    private static void awaitResponse(String documentId, int currentRetry, AIResponseCallback callback) {
        if (currentRetry >= MAX_RETRIES) {
            callback.onError(new Exception("Max retries reached"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("QC_Chatbot_Messages").document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String response = document.getString("response");
                    if (response != null && !response.isEmpty()) {
                        callback.onReceived(response);
                    } else {
                        // Retry after a delay
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                awaitResponse(documentId, currentRetry + 1, callback), RETRY_DELAY_MS);
                    }
                } else {
                    callback.onError(new Exception("Document does not exist"));
                }
            } else {
                callback.onError(task.getException());
            }
        });
    }


}
