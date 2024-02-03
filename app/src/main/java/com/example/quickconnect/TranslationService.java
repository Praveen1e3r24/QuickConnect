package com.example.quickconnect;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TranslationService {

    private FirebaseFirestore db;

    public TranslationService() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

    public ArrayList<String> getSupportedLanguages() {
        ArrayList<String> supportedLanguages = new ArrayList<>();
        supportedLanguages.add("en");
        supportedLanguages.add("es");
        supportedLanguages.add("fr");
        supportedLanguages.add("de");
        supportedLanguages.add("it");
        supportedLanguages.add("ja");
        supportedLanguages.add("ko");
        supportedLanguages.add("pt");
        supportedLanguages.add("ru");
        supportedLanguages.add("zh");
        return supportedLanguages;
    }

    public void translate(String message, String languageCode, TranslationCallback callback) {
        Map<String, Object> translationdata = new HashMap<>();
        translationdata.put("input", message);

        db.collection("translations")
                .add(translationdata)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    DocumentReference docRef = db.collection("translations").document(documentId);

                    docRef.addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            Log.e("Firestore", "Listen failed.", e);
                            callback.onTranslationFailure(e.getMessage());
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Map<String, Object> response = documentSnapshot.getData();
                            if (response != null && response.containsKey("translated")) {
                                Map<String, Object> translated = (Map<String, Object>) response.get("translated");
                                if (translated.containsKey(languageCode)) {
                                    Object languageData = translated.get(languageCode);
                                    String Translated_Response = languageData.toString();
                                    callback.onTranslationSuccess(Translated_Response);
                                }
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding document", e);
                    callback.onTranslationFailure(e.getMessage());
                });
    }

    public interface TranslationCallback {
        String onTranslationSuccess(String translatedMessage);

        void onTranslationFailure(String errorMessage);
    }
}
