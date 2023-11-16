package com.example.customer;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quickconnect.CallRequest;
import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Message;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.databinding.CustomSeriousQuickconnectPopupBinding;
import com.example.quickconnect.databinding.FragmentCustomerProfileBinding;
import com.example.quickconnect.databinding.FragmentCustomerQuickConnectBinding;
import com.example.utilities.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Customer_QuickConnect_Fragment extends Fragment {

    FragmentCustomerQuickConnectBinding binding;

    FragmentCustomerProfileBinding  ProfileBinding;

    CustomSeriousQuickconnectPopupBinding custompopup;
    FirebaseModelInterpreter interpreter;
    FirebaseModelInputOutputOptions inputOutputOptions;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private boolean hasEmployee;

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
                .setAssetFilePath("severity_qc_2.2.tflite")
                .build();

        // For remotely-hosted model
        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("severity_qc_twoone").build();

        // Create FirebaseModelInterpreterOptions
        FirebaseModelInterpreterOptions option = new FirebaseModelInterpreterOptions.Builder(localModel).build();

        // Initialize the interpreter
        Interpreter.Options options = new Interpreter.Options();
        options.setUseNNAPI(false);

        // Initialize input and output options
        try {
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 100})  // Update the input dimension here
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
                    if (isDownloaded) {
                        Log.d(TAG, "Using Remote Model: " + remoteModel.getModelName());
                    } else {
                        Log.d(TAG, "Using Local Model: " + localModel.getAssetFilePath());
                        // Download the remote model
                        downloadModel(remoteModel);
                    }

                    FirebaseModelInterpreterOptions interpreterOptions = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();

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
                        float confidence = outputValues[0][0]; // Assuming the confidence is in the second position
                        Log.d(TAG, "Confidence: " + confidence);
                        float predictedClass = outputValues[0][0];

                        // The confidence of the model is typically associated with the probability
                        // values in the output tensor. You can print these values for each class.

                        Log.d(TAG, "performSeverityCheck:output values "+result);; // Assuming the confidence is in the second position

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


    private void handleModelOutput(float predictedProbability) {
        // Modify this based on your Python model's output


        // Use a threshold of 0.5 to determine the severity label
        String severityText = (predictedProbability >= 0.5) ? "Serious" : "Not Serious";


        Log.d(TAG, "Severity Label: " + severityText);

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

        custompopup = CustomSeriousQuickconnectPopupBinding.inflate(getLayoutInflater());
        builder.setView(custompopup.getRoot());

        // Handle button clicks or any other interactions
       custompopup.requestCall.setOnClickListener(v -> {
            dbRef.child("Users").child("Employees").addValueEventListener(addRequestToDB());
        });

        custompopup.requestMessage247.setOnClickListener(v -> {
            dbRef.child("Users").child("Employees").addValueEventListener(checkAndAddChatToDB());
        });

        custompopup.requestBookAnAppointment.setOnClickListener(v -> {
            // Handle the "Book an Appointment" option
            // Implement your logic here
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private ValueEventListener checkAndAddChatToDB() {

        hasEmployee = false;
        User user = new UserData().getUserDetailsFromSharedPreferences(getContext());

        // Add topic here :)
        String topic = "topic here";
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

    private ValueEventListener addRequestToDB(){
        hasEmployee = false;
        ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Employee employee = s.getValue(Employee.class);
                    if (employee != null && employee.getAvailable() && employee.getEmployeeRole().equals("CS")) {
                        hasEmployee = true;
                        User user = new UserData().getUserDetailsFromSharedPreferences(getContext());
                        String topic = "topic here";
                        String query = binding.editTextComplaint.getText().toString();

                        dbRef.child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int queueNo = 0;

                                if (snapshot.exists()) {
                                    queueNo = (int) snapshot.getChildrenCount();
                                }
                                List<Message> messages = new ArrayList<>();
                                Chat chat = new Chat(UUID.randomUUID().toString(), employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
                                CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(), user.getUserId(), user.getFullName(), employee.getUserId(), employee.getFullName(), chat, query, topic, queueNo, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), false, false);
                                callRequest.setRequestId(dbRef.child("Requests").push().getKey());
                                callRequest.getChat().setCallRequestId(callRequest.getRequestId());
                                dbRef.child("Requests").child(callRequest.getRequestId()).setValue(callRequest);
                                dbRef.child("Users").child("Employees").child(employee.getUserId()).child("available").setValue(false);
                                dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
                                Toast.makeText(getContext(), "Call Request Sent", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                                intent.putExtra("callRequest", callRequest);
//                                startActivity(intent);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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

            }
        };


        return eventListener;
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
