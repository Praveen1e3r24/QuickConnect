package com.example.customer;

import static android.content.ContentValues.TAG;

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

import org.tensorflow.lite.Interpreter;

import java.util.Arrays;

public class Customer_QuickConnect_Fragment extends Fragment {

    FragmentCustomerQuickConnectBinding binding;
    FirebaseModelInterpreter interpreter;
    FirebaseModelInputOutputOptions inputOutputOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // For locally-bundled model
        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("Quick_Connect_SD.tflite")
                .build();

        // For remotely-hosted model
        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("Quick_Connect_SD").build();

        // Create FirebaseModelInterpreterOptions
        FirebaseModelInterpreterOptions option = new FirebaseModelInterpreterOptions.Builder(localModel).build();

        // Initialize the interpreter
        Interpreter.Options options = new Interpreter.Options();
        options.setUseNNAPI(false);

        // Initialize input and output options
        try {
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 824})  // Update the input dimension here
                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1})
                    .build();
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }


        // Check if model is downloaded and initialize interpreter
        checkModelDownloaded(remoteModel, localModel);

        // Set onClickListener for the Send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input (complaint text, attached image, etc.)
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
                        interpreterOptions = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
                    } else {
                        interpreterOptions = new FirebaseModelInterpreterOptions.Builder(localModel).build();
                        // Download the remote model
                        downloadModel(remoteModel);
                    }

                    try {
                        interpreter = FirebaseModelInterpreter.getInstance(interpreterOptions);
                    } catch (FirebaseMLException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void downloadModel(FirebaseCustomRemoteModel remoteModel) {
        // Specify the conditions under which you want to allow downloading
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()  // Example: Download only if connected to Wi-Fi
                .build();

        // Start the model download task
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Model downloaded successfully
                        Toast.makeText(requireContext(), "Model download success", Toast.LENGTH_SHORT).show();
                    } else {
                        // Model download failed
                        // Handle the failure, e.g., show an error message
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

        Log.d(TAG, "Severity Label: " + severityText);

        // Handle the result, e.g., display it or take further actions
        Toast.makeText(requireContext(), "Predicted Severity: " + severityText, Toast.LENGTH_SHORT).show();
    }

    private float[] preprocessInput(String userInput) {
        // Modify this method based on your Python preprocessing logic
        String[] words = userInput.split("\\s+");
        float[] inputArray = new float[824];

        for (int i = 0; i < inputArray.length; i++) {
            if (i < words.length) {
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

}
