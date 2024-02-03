//package com.example.customer;
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.google.ai.generativelanguage.v1beta3.DiscussServiceClient;
//import com.google.ai.generativelanguage.v1beta3.DiscussServiceSettings;
//import com.google.api.gax.core.FixedCredentialsProvider;
//import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
//import com.google.api.gax.rpc.FixedHeaderProvider;
//
//import androidx.fragment.app.Fragment;
//
//import com.example.quickconnect.Message;
//import com.example.quickconnect.R;
//
//import java.util.HashMap;
//
//public class QC_QuickQueries_Bot extends Fragment {
//
//    private DiscussServiceClient client;
//
//    private EditText userInputEditText;
//    private Button sendButton;
//    private TextView chatTextView;
//
//    public QC_QuickQueries_Bot() {
//        // Required empty public constructor
//    }
//
//    public static QC_QuickQueries_Bot newInstance() {
//        return new QC_QuickQueries_Bot();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_q_c__quick_queries__bot, container, false);
//
//        userInputEditText = view.findViewById(R.id.userInputEditText);
//        sendButton = view.findViewById(R.id.sendButton);
//        chatTextView = view.findViewById(R.id.chatTextView);
//
//        initializeDiscussServiceClient();
//
//        sendButton.setOnClickListener(v -> {
//            String userMessage = userInputEditText.getText().toString();
//            if (!userMessage.isEmpty()) {
//                userInputEditText.setText(""); // Clear input field
//                sendMessage(userMessage);
//            }
//        });
//
//        return view;
//    }
//
//    private void initializeDiscussServiceClient() {
//        AsyncTask.execute(() -> {
//            try {
//                // Replace "YOUR_API_KEY" with your actual PaLM API key
//                String palmApiKey = "AIzaSyACKlw8Etu0KZtl-NLhOfUlmo6JkeM0eyU";
//
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("x-goog-api-key", palmApiKey);
//
//                InstantiatingGrpcChannelProvider provider = InstantiatingGrpcChannelProvider.newBuilder()
//                        .setHeaderProvider(FixedHeaderProvider.create(headers))
//                        .build();
//
//                DiscussServiceSettings settings = DiscussServiceSettings.newBuilder()
//                        .setTransportChannelProvider(provider)
//                        .setCredentialsProvider(FixedCredentialsProvider.create(null))
//                        .build();
//
//                client = DiscussServiceClient.create(settings);
//            } catch (Exception e) {
//                showToast("Error initializing DiscussServiceClient: " + e.getMessage());
//            }
//        });
//    }
//
//    private void sendMessage(String userMessage) {
//        AsyncTask.execute(() -> {
//            try {
//                Message userInputMessage = Message.newBuilder()
//                        .setAuthor("user")
//                        .setContent(userMessage)
//                        .build();
//
//                MessagePrompt messagePrompt = MessagePrompt.newBuilder()
//                        .addMessages(userInputMessage)
//                        .build();
//
//                GenerateMessageRequest request = GenerateMessageRequest.newBuilder()
//                        .setModel("models/chat-bison-001")
//                        .setPrompt(messagePrompt)
//                        .setTemperature(0.5f)
//                        .setCandidateCount(1)
//                        .build();
//
//                GenerateMessageResponse response = client.generateMessage(request);
//                Message generatedMessage = response.getCandidatesList().get(0);
//
//                requireActivity().runOnUiThread(() -> updateChat(userMessage, generatedMessage.getContent()));
//            } catch (Exception e) {
//                showToast("Error sending message: " + e.getMessage());
//            }
//        });
//    }
//
//    private void updateChat(String userMessage, String botMessage) {
//        String currentChat = chatTextView.getText().toString();
//        currentChat += "\nUser: " + userMessage + "\nBot: " + botMessage + "\n";
//        chatTextView.setText(currentChat);
//    }
//
//    private void showToast(String message) {
//        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
//    }
//}
