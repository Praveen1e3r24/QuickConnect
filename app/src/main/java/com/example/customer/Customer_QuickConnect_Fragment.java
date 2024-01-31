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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.palmchatbot.Chatbot_Activity;
import com.example.palmchatbot.Palm_Ai_Service;
import com.example.quickconnect.CallRequest;
import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.Employee;
import com.example.quickconnect.Message;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.example.quickconnect.WebViewer;
import com.example.quickconnect.databinding.CustomSeriousQuickconnectPopupBinding;
import com.example.quickconnect.databinding.FragmentCustomerQuickConnectBinding;
import com.example.utilities.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Customer_QuickConnect_Fragment extends Fragment {

    FragmentCustomerQuickConnectBinding binding;



    CustomSeriousQuickconnectPopupBinding custompopup;
    FirebaseModelInterpreter interpreter;
    FirebaseModelInputOutputOptions inputOutputOptions;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private boolean hasEmployee;

    String topic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomerQuickConnectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (new DarkModePrefManager(getContext()).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        EditText complaintEditText = binding.editTextComplaint;
        ImageView attachImageButton = binding.imageViewAttachedFile;
        Button sendButton = binding.buttonSend;

        binding.tofaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewer.class);
                intent.putExtra("url", "https://www.ocbc.com/personal-banking/help-and-support");
                startActivity(intent);
            }
        });

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
        // ... existing code ...

        new Palm_Ai_Service().sendrequestai(userInput, "EN", new Palm_Ai_Service.AIResponseCallback() {
            @Override
            public void onReceived(String response) {
                if (response != null && !response.isEmpty()) {
                    Log.d("AI Response", response);

                    String[] aiResults = response.split(",");
                    if (aiResults.length >= 4) {
                        // Extract data from AI response
                        String severity = aiResults[0];
                        String resolutionMethod = aiResults[1];
                        String department = aiResults[2];
                        String title = aiResults[3];
                        Log.d("DEPARTMENT", resolutionMethod);
                        // Show the dialog with the suggested resolution method
                        showDialogWithResolutionMethod(resolutionMethod,topic,department);
                    } else {
                        Log.d("AI Response1", "Invalid response format");
                    }
                } else {
                    Log.d("AI Response2", "Received null or empty response");
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("AI Response Error", e.getMessage());
                // Handle the error (e.g., show error message to the user)
            }
        });


    }

    private void showDialogWithResolutionMethod(String resolutionMethod,String topic,String department) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Customer_QuickConnect_Fragment.this.getContext());
        builder.setTitle("Suggested Resolution Method");
        builder.setMessage("We suggest resolving this issue via: " + resolutionMethod + "\nDo you want to proceed?");

        builder.setPositiveButton("Proceed", (dialog, which) -> {
            // User chose to proceed, handle the resolution method
            Log.d(TAG, "showDialogWithResolutionMethod:1 ");
            handleResolutionMethod(resolutionMethod,topic,department);
        });

        builder.setNegativeButton("Not Proceed", (dialog, which) -> {
            // User chose not to proceed, dismiss the dialog
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleResolutionMethod(String method,String topic,String department ) {
//        if (method.trim().toLowerCase()==" ResolutionMethod: Messaging".trim().toLowerCase()) {
//            Log.d(TAG, "showDialogWithResolutionMethod:2 ");
//            checkAndAddChatToDB(topic, department);
        if (method.contains("Messaging")) {
            Log.d(TAG, "showDialogWithResolutionMethod:2 ");
            dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(checkAndAddChatToDB(topic, department));
        } else if (method.contains("Voice Call")) {
            dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(addRequestToDB("Voice Call", department));
        } else if (method.contains("Video Call")) {
            dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(addRequestToDB("Video Call", department));
        } else if (method.contains("Chatbot")) {
            startActivity(new Intent(getActivity(), Chatbot_Activity.class));
        }
    }


    // Mock method to represent fetching output from AI
//    private String getAIOutput(String userInput) {
//        // TODO: Implement the actual API call or AI processing logic
//        return "Low,Messaging,General Inquiries,Account Inquiry";
//    }
//
//    private void handleModelOutput(float predictedProbability) {
//        // Modify this based on your Python model's output
//
//        if (lowercaseInput.contains("referral")) {
//            topic = "Referral";
//        }
//
////        // Use a threshold of 0.5 to determine the severity label
////        String severityText = (predictedProbability >= 0.5) ? "Serious" : "Not Serious";
////
////
////        Log.d(TAG, "Severity Label: " + severityText);
//
//        String severityText ="Serious";
//
//        if(severityText.equals("Serious")){
//            showSeriousOptionsDialog();
//        }
//        else{
//            Toast.makeText(requireContext(), "Predicted Severity: " + severityText, Toast.LENGTH_SHORT).show();
//
////            replaceFragment(new Customer_Profile_Fragment());
//
//        }
//
//        // Handle the result, e.g., display it or take further actions
//        Toast.makeText(requireContext(), "Predicted Severity: " + severityText, Toast.LENGTH_SHORT).show();
//    }



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

//    private void showSeriousOptionsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//
//        custompopup = CustomSeriousQuickconnectPopupBinding.inflate(getLayoutInflater());
//        builder.setView(custompopup.getRoot());
//
//        // Handle button clicks or any other interactions
//       custompopup.requestCall.setOnClickListener(v -> {
//           custompopup.requestCall.setEnabled(false);
//            dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(addRequestToDB());
//       });
//
//        custompopup.requestMessage247.setOnClickListener(v -> {
//            dbRef.child("Users").child("Employees").addListenerForSingleValueEvent(checkAndAddChatToDB());
//            custompopup.requestMessage247.setEnabled(false);
//        });
//
//        custompopup.requestBookAnAppointment.setOnClickListener(v -> {
//            // Handle the "Book an Appointment" option
//            // Implement your logic here
//        });
//
//        // Show the dialog
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    
    @RequiresApi(api = Build.VERSION_CODES.M)
    private ValueEventListener checkAndAddChatToDB(String topic,String department) {
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
                        Chat chat = new Chat("", employee.getUserId(), employee.getFullName(), employee.getDepartment(), user.getUserId(), user.getFullName(), topic, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), messages, false);
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

//    private boolean checkDepartment(String department) {
////        {"Scam & Fraud", "Technical", "Account", "General"}
//        switch (department)
//        {
//            case "Scam & Fraud":
//                return topic.equals("Scam");
//            case "Technical":
//                return topic.equals("Technical");
//            case "Account":
//                return topic.equals("loan") || topic.equals("Transaction") || topic.equals("Referral") || topic.equals("Waiver Fees");
//            case "General":
//                return topic.equals("General Inquiries");
//            default:
//                return false;
//        }
//    }

    private ValueEventListener addRequestToDB(String CallType, String department){
        hasEmployee = false;
        if (topic == null) {
            topic = "General Inquiries";
        }
        ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Employee employee = s.getValue(Employee.class);
                    if (employee != null && employee.getAvailable() && employee.getEmployeeRole().equals("CS") && employee.getDepartment().equals(department)) {
                        hasEmployee = true;
                        User user = new UserData().getUserDetailsFromSharedPreferences(getContext());
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
                                CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(), user.getUserId(), user.getFullName(), employee.getUserId(), employee.getFullName(), chat, query, topic, queueNo, Date.from(Instant.ofEpochSecond(System.currentTimeMillis())), false, false, CallType);
                                callRequest.setRequestId(dbRef.child("Requests").push().getKey());
                                callRequest.getChat().setCallRequestId(callRequest.getRequestId());
                                dbRef.child("Requests").child(callRequest.getRequestId()).setValue(callRequest);
                                dbRef.child("Users").child("Employees").child(employee.getUserId()).child("available").setValue(false);
                                dbRef.child("Chats").child(chat.getChatId()).setValue(chat);
                                Toast.makeText(getContext(), "Call Request Sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), call_waiting_dashboard.class);
                                intent.putExtra("callRequest", callRequest);
                                startActivity(intent);
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


}
