package com.example.customer;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quickconnect.R;
import com.example.quickconnect.databinding.CustomSeriousQuickconnectPopupBinding;
import com.example.quickconnect.databinding.FragmentCustomerProfileBinding;
import com.example.quickconnect.databinding.FragmentCustomerQuickConnectBinding;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;

import java.util.Arrays;

public class Customer_QuickConnect_Fragment extends Fragment {

    FragmentCustomerQuickConnectBinding binding;

    CustomSeriousQuickconnectPopupBinding customDialogBinding;

    FragmentCustomerProfileBinding profileBinding;
    FirebaseModelInterpreter interpreter;
    FirebaseModelInputOutputOptions inputOutputOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: 1");
        binding = FragmentCustomerQuickConnectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        EditText complaintEditText = binding.editTextComplaint;
        ImageView attachImageButton = binding.imageViewAttachedFile;
        Button sendButton = binding.buttonSend;
        Log.d(TAG, "onCreateView: 2");

        // For locally-bundled model
        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("QC_SD_2.tflite")
                .build();
        Log.d(TAG, "onCreateView: 3");
        // For remotely-hosted model
        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("QC_SD_2").build();
        Log.d(TAG, "onCreateView: 4");
        // Create FirebaseModelInterpreterOptions
        FirebaseModelInterpreterOptions options = new FirebaseModelInterpreterOptions.Builder(localModel).build();

        // Initialize the interpreter
        try {
            Log.d(TAG, "onCreateView: 5");
            interpreter = FirebaseModelInterpreter.getInstance(options);
        } catch (FirebaseMLException e) {
            throw new RuntimeException(e);
        }

        // Initialize input and output options
        try {
            Log.d(TAG, "onCreateView: 6");
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 100})  // Update the input dimension here
                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1})
                    .build();
        } catch (FirebaseMLException e) {
            Log.d(TAG, "onCreateView: 7");
            e.printStackTrace();
        }


        // Check if model is downloaded and initialize interpreter
        checkModelDownloaded(remoteModel, localModel);
        Log.d(TAG, "onCreateView: 8");

        // Set onClickListener for the Send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreateView: 9");
                // Get user input (complaint text, atta
                // ched image, etc.)
                String complaintText = complaintEditText.getText().toString();
                String cleansedText = cleanseText(complaintText);
                // Perform machine learning model check
                performSeverityCheck(cleansedText);


            }
        });

        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement image attachment logic
                Toast.makeText(requireContext(), "Attach Image Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkModelDownloaded(FirebaseCustomRemoteModel remoteModel, FirebaseCustomLocalModel localModel) {
        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener(isDownloaded -> {
                    FirebaseModelInterpreterOptions interpreterOptions;
                    if (isDownloaded) {
                        Log.d(TAG, "onCreateView: 10");
                        interpreterOptions = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
                    } else {
                        interpreterOptions = new FirebaseModelInterpreterOptions.Builder(localModel).build();
                        // Download the remote model
                        Log.d(TAG, "onCreateView: 11");
                        downloadModel(remoteModel);
                    }

                    try {
                        Log.d(TAG, "onCreateView: 12");
                        interpreter = FirebaseModelInterpreter.getInstance(interpreterOptions);
                    } catch (FirebaseMLException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void downloadModel(FirebaseCustomRemoteModel remoteModel) {
        Log.d(TAG, "onCreateView: 13");
        // Specify the conditions under which you want to allow downloading
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()  // Example: Download only if connected to Wi-Fi
                .build();

        // Start the model download task
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onCreateView: 14");
                        // Model downloaded successfully
                        Toast.makeText(requireContext(), "Model download success", Toast.LENGTH_SHORT).show();
                    } else {
                        // Model download failed
                        // Handle the failure, e.g., show an error message
                        Log.d(TAG, "onCreateView: 15");
                        Toast.makeText(requireContext(), "Model download failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performSeverityCheck(String userInput) {
        Log.d(TAG, "performSeverityCheck");

        Log.d(TAG, "Cleansed Input: " + userInput);
        float[] inputArray = preprocessInput(userInput);

        Log.d(TAG, "Input Array: " + Arrays.toString(inputArray));

        try {
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                    .add(new float[][]{inputArray})
                    .build();

            interpreter.run(inputs, inputOutputOptions)
                    .addOnSuccessListener(result -> {
                        Log.d(TAG, "onCreateView: 16");
                        float[][] outputValues = result.getOutput(0);
                        float predictedClass = outputValues[0][0];

                        Log.d(TAG, "Predicted Class: " + predictedClass);

                        // Handle the result
                        handleModelOutput(predictedClass);
                    })
                    .addOnFailureListener(e -> {

                        Log.e(TAG, "Inference failed", e);
                        Toast.makeText(requireContext(), "Inference failed", Toast.LENGTH_SHORT).show();
                    });
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
    }

    private void handleModelOutput(float predictedClass) {
        // Modify this based on your Python model's output
        int severityLabel = Math.round(predictedClass);
        String severityText = (severityLabel == 1) ? "Serious" : "Not Serious";

        severityText = "Serious";

        if (severityText.equals("Serious")) {
            Log.d(TAG, "onCreateView: 18");
            // Show the serious options dialog
            showSeriousOptionsDialog();
        } else {
            // Navigate to another fragment
            Log.d(TAG, "onCreateView: 19");
            replaceFragment(new Customer_Profile_Fragment());
        }

        // Handle the result, e.g., display it or take further actions
        Toast.makeText(requireContext(), "Predicted Severity: " + severityText, Toast.LENGTH_SHORT).show();
    }

    private void replaceFragment(Fragment fragment) {
        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Optional: Adds the transaction to the back stack
                    .commit();
        }
    }


    private float[] preprocessInput(String userInput) {
        // Modify this method based on your Python preprocessing logic
        String[] words = userInput.split("\\s+");
        Log.d(TAG, "onCreateView: 20");
        float[] inputArray = new float[100];

        for (int i = 0; i < inputArray.length; i++) {
            if (i < words.length) {
                Log.d(TAG, "onCreateView: 21");
                // Modify this part to map words to indices as done in your Python code
                int index = Math.abs(words[i].hashCode() % inputArray.length);
                inputArray[i] = index;
            } else {
                inputArray[i] = 0.0f;
            }
        }

        return inputArray;
    }



    private String cleanseText(String text) {
        // Placeholder implementation (replace with actual logic)
        // Your model should internally handle the conversion of raw text to numerical values
        // If your model expects preprocessed input, you might need to adjust this method accordingly

        // This is just an example; adjust the cleaning logic based on your requirements
        text = text.toLowerCase();
        text = text.replaceAll("</?.*?>", " <> ");
        text = text.replaceAll("(\\d|\\W|_)+", " ");

        return text;
    }




    private float getOneHotEncoding(String word) {
        // Placeholder implementation for one-hot encoding
        // Replace this with actual logic based on your requirements
        // For simplicity, assigning 1.0 for the presence of any word
        return 1.0f;
    }


    private void showSeriousOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the layout using view binding for the custom dialog
        customDialogBinding = CustomSeriousQuickconnectPopupBinding.inflate(getLayoutInflater());
        builder.setView(customDialogBinding.getRoot());

        // Handle button clicks or any other interactions
        customDialogBinding.requestCall.setOnClickListener(v -> {
            // Handle the "Request Call" option
            // Implement your logic here
        });

        customDialogBinding.requestMessage247.setOnClickListener(v -> {
            // Handle the "Message 24/7 Messaging Personal" option
            // Implement your logic here
        });

        customDialogBinding.requestBookAnAppointment.setOnClickListener(v -> {
            // Handle the "Book an Appointment" option
            // Implement your logic here
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
