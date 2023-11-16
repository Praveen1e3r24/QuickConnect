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
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;

import java.util.Arrays;
import java.util.List;

public class Customer_QuickConnect_Fragment extends Fragment {

    FragmentCustomerQuickConnectBinding binding;

    FragmentCustomerProfileBinding  ProfileBinding;

    CustomSeriousQuickconnectPopupBinding custompopup;
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

//        // For locally-bundled model
//        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
//                .setAssetFilePath("severity_qc_2.2.tflite")
//                .build();
//
//        // For remotely-hosted model
//        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("severity_qc_twoone").build();
//
//        // Create FirebaseModelInterpreterOptions
//        FirebaseModelInterpreterOptions option = new FirebaseModelInterpreterOptions.Builder(localModel).build();
//
//        // Initialize the interpreter
//        Interpreter.Options options = new Interpreter.Options();
//        options.setUseNNAPI(false);
//
//        // Initialize input and output options
//        try {
//            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
//                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 100})  // Update the input dimension here
//                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1})
//                    .build();
//        } catch (FirebaseMLException e) {
//            e.printStackTrace();
//        }


        // Check if model is downloaded and initialize interpreter
//        checkModelDownloaded(remoteModel, localModel);

        // Set onClickListener for the Send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input (complaint text, attached image, etc.)
                String complaintText = complaintEditText.getText().toString();
//                String cleansedText = cleanseText(complaintText);
                // Perform machine learning model check
                performSeverityCheck(complaintText);


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

//    private void checkModelDownloaded(FirebaseCustomRemoteModel remoteModel, FirebaseCustomLocalModel localModel) {
//        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
//                .addOnSuccessListener(isDownloaded -> {
//                    if (isDownloaded) {
//                        Log.d(TAG, "Using Remote Model: " + remoteModel.getModelName());
//                    } else {
//                        Log.d(TAG, "Using Local Model: " + localModel.getAssetFilePath());
//                        // Download the remote model
//                        downloadModel(remoteModel);
//                    }
//
//                    FirebaseModelInterpreterOptions interpreterOptions = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
//
//                    try {
//                        interpreter = FirebaseModelInterpreter.getInstance(interpreterOptions);
//                    } catch (FirebaseMLException e) {
//                        e.printStackTrace();
//                    }
//                });
//    }



//    private void downloadModel(FirebaseCustomRemoteModel remoteModel) {
//        // Specify the conditions under which you want to allow downloading
//        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
//                .requireWifi()  // Example: Download only if connected to Wi-Fi
//                .build();
//
//        // Start the model download task
//        FirebaseModelManager.getInstance().download(remoteModel, conditions)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // Model downloaded successfully
//                        Toast.makeText(requireContext(), "Model download success", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Model download failed
//                        // Handle the failure, e.g., show an error message
//                        Toast.makeText(requireContext(), "Model download failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

//    private void performSeverityCheck(String userInput) {
//        Log.d(TAG, "performSeverityCheck");
//
//        Log.d(TAG, "Cleansed Input: " + userInput);
//        float[] inputArray = preprocessInput(userInput);
//
//        Log.d(TAG, "Input Array: " + Arrays.toString(inputArray));
//
//        try {
//            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
//                    .add(new float[][]{inputArray})
//                    .build();
//
//            interpreter.run(inputs, inputOutputOptions)
//                    .addOnSuccessListener(result -> {
//                        float[][] outputValues = result.getOutput(0);
//                        float confidence = outputValues[0][0]; // Assuming the confidence is in the second position
//                        Log.d(TAG, "Confidence: " + confidence);
//                        float predictedClass = outputValues[0][0];
//
//                        // The confidence of the model is typically associated with the probability
//                        // values in the output tensor. You can print these values for each class.
//
//                        Log.d(TAG, "performSeverityCheck:output values "+result);; // Assuming the confidence is in the second position
//
//                        Log.d(TAG, "Predicted Class: " + predictedClass);
//
//
//                        // Handle the result
//                        handleModelOutput(predictedClass);
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e(TAG, "Inference failed", e);
//                        Toast.makeText(requireContext(), "Inference failed", Toast.LENGTH_SHORT).show();
//                    });
//        } catch (FirebaseMLException e) {
//            e.printStackTrace();
//        }
//    }
    private void performSeverityCheck(String userInput) {
        Log.d(TAG, "performSeverityCheck");

        Log.d(TAG, "Cleansed Input: " + userInput);

        // List of hardcoded words to check for severity
        List<String> seriousWords = Arrays.asList(
                "urgent", "scam", "fraud", "stolen", "phishing", "deceived",
                "assistance", "help", "emergency", "lost", "scammers", "identity",
                "drain", "desperate", "swindled"
        );

        // Convert the user input to lowercase for case-insensitive matching
        String lowercaseInput = userInput.toLowerCase();

        // Check if any serious words are present in the input
        boolean isSerious = false;
        for (String word : seriousWords) {
            if (lowercaseInput.contains(word)) {
                isSerious = true;
                break; // Break out of the loop if a serious word is found
            }
        }

        if(isSerious){
            showSeriousOptionsDialog();
        }
        else{
            replaceFragment(new Customer_Profile_Fragment());

        }

        // The rest of your code...
    }


    private void handleModelOutput(float predictedProbability) {
        // Modify this based on your Python model's output


//        // Use a threshold of 0.5 to determine the severity label
//        String severityText = (predictedProbability >= 0.5) ? "Serious" : "Not Serious";
//
//
//        Log.d(TAG, "Severity Label: " + severityText);

        String severityText ="Serious";

        if(severityText.equals("Serious")){
            showSeriousOptionsDialog();
        }
        else{
            Toast.makeText(requireContext(), "Predicted Severity: " + severityText, Toast.LENGTH_SHORT).show();

            replaceFragment(new Customer_Profile_Fragment());

        }

        // Handle the result, e.g., display it or take further actions
        Toast.makeText(requireContext(), "Predicted Severity: " + severityText, Toast.LENGTH_SHORT).show();
    }



    private void replaceFragment(Fragment fragment) {
        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getParentFragmentManager();

        if (fragmentManager != null && isAdded()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Optional: Adds the transaction to the back stack
                    .commit();
        }
    }

    private void showSeriousOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the layout using view binding for the custom dialog
        custompopup = CustomSeriousQuickconnectPopupBinding.inflate(getLayoutInflater());
        builder.setView(custompopup.getRoot());

        // Handle button clicks or any other interactions
       custompopup.requestCall.setOnClickListener(v -> {
            // Handle the "Request Call" option
            // Implement your logic here
        });

        custompopup.requestMessage247.setOnClickListener(v -> {
            // Handle the "Message 24/7 Messaging Personal" option
            // Implement your logic here
        });

        custompopup.requestBookAnAppointment.setOnClickListener(v -> {
            // Handle the "Book an Appointment" option
            // Implement your logic here
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private float[] preprocessInput(String userInput) {
        // Modify this method based on your Python preprocessing logic
        String[] words = userInput.split("\\s+");
        float[] inputArray = new float[100];

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
