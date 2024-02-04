package com.example.quickconnect;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AppPreferences;
import com.example.NotificationHandler;
import com.example.customer.Customer_Main;
import com.example.quickconnect.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity implements MessageAdapter.OnFileClickListener {
    private static final int PICKFILE_RESULT_CODE = 0;
    private EditText messageText;

    private Chat chat;
    private CallRequest callRequest;

    private DatabaseReference dbRef;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private Boolean firstenter;
    private ActivityChatBinding binding;
    boolean isRequest;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    private StorageReference storageReference;

    private TranslationService translationService;

    private AppPreferences appPreferences;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbRef = FirebaseDatabase.getInstance().getReference();
        translationService = new TranslationService();
        appPreferences = new AppPreferences(this);
        firstenter = true;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        chat = intent.getParcelableExtra("chat");
        callRequest = intent.getParcelableExtra("callRequest");

        if (callRequest == null) {
            callRequest = intent.getParcelableExtra("callRequest1");
        }
        isRequest = intent.getBooleanExtra("isRequest", false);

        RecyclerView rv = binding.recyclerGchat;
        rv.setLayoutManager(new LinearLayoutManager(this));

        storageReference = FirebaseStorage.getInstance().getReference();

        dbRef.child("Chats").child(chat.getChatId()).child("closed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class)) == true) {
                    Toast.makeText(ChatActivity.this, "Chat has been closed", Toast.LENGTH_SHORT).show();
                    binding.chatMessage.setVisibility(View.GONE);
                    binding.chatSend.setVisibility(View.GONE);
                    binding.chatCloseText.setVisibility(View.VISIBLE);
                    binding.chatGallery.setVisibility(View.GONE);
                    binding.chatUploadFile.setVisibility(View.GONE);
                } else {
                    binding.chatCloseText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dbRef.child("Chats").child(chat.getChatId()).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messageList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Message msg = s.getValue(Message.class);
                    messageList.add(msg);
                }

                if (!messageList.isEmpty()) {
                    adapter = new MessageAdapter(messageList, ChatActivity.this, getApplicationContext());
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if (firstenter == true) {
                        binding.recyclerGchat.smoothScrollToPosition(adapter.getItemCount() - 1);
                        firstenter = false;
                    } else {
                        binding.recyclerGchat.scrollToPosition(adapter.getItemCount() - 1);

//                    if (!messageList.get(messageList.size() - 1).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
//                    {
//                        createNotification();
//                    }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error sending message: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        NotificationHandler notificationHandler = NotificationHandler.getInstance();
        notificationHandler.initialize(this, chat);

        dbRef.child("Chats").addValueEventListener(notificationHandler.checkNewMessage());

        if (chat.getSupportId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            actionBar.setTitle(chat.getCustomerName());
            actionBar.setSubtitle("Subject: " + chat.getCategory());
        } else {
            actionBar.setTitle(chat.getSupportName());
            actionBar.setSubtitle(chat.getSupportTeam());
        }

        messageText = binding.chatMessage;
        messageText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        messageText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        messageText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        messageText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage();
                return true;
            }

            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                sendMessage();
                return true;
            }

            return false;
        });

        binding.chatSend.setOnClickListener(v -> {
            sendMessage();

        });

        binding.chatGallery.setOnClickListener(v -> {
            showAlert(1);
        });

        binding.chatUploadFile.setOnClickListener(v -> {
            showAlert(2);
        });

        binding.chatCancelImg.setOnClickListener(v -> {
            filePath = null;
            binding.chatMessage.clearFocus();
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            binding.cardView.startAnimation(animation);
            binding.layoutGchatChatbox.setBackgroundColor(getColor(R.color.white));
            binding.cardView.setVisibility(View.GONE);
            binding.chatSend.setVisibility(View.GONE);
            binding.chatGallery.setVisibility(View.VISIBLE);
        });

    }

    public void showAlert(int choice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confidentiality Warning");
        builder.setMessage("Please do not send any confidential information through this chat.\n\nThis chat is not secure and is not HIPAA compliant. Please keep caution when sending any files.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (choice == 1)
                {
                    selectImage();
                }
                else if (choice == 2)
                {
                    selectFile();
                }
            }
        });

        // Add a Cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.customer_info) {

        } else if (itemId == R.id.end_chat) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set the dialog title and message
            builder.setTitle("End Chat")
                    .setMessage("Are you sure you want to end this chat?")
                    .setCancelable(true)
                    .setPositiveButton("End Chat", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Handle the button click, if needed
                            endChat();
                            finish();
                        }
                    }).
                    setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Handle the button click, if needed
                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else if (itemId == R.id.change_language) {
            Spinner spinner = new Spinner(this, Spinner.MODE_DIALOG);
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
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ChatActivity.this, "Language set to " + language, Toast.LENGTH_SHORT).show();
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

    private void endChat(){

        dbRef.child("Users").child("Employees").child(chat.getSupportId()).child("numChats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numChats = snapshot.getValue(Integer.class);
                if (numChats > 0)
                {
                    dbRef.child("Users").child("Employees").child(chat.getSupportId()).child("numChats").setValue(numChats - 1);
                }
                dbRef.child("Users").child("Employees").child(chat.getSupportId()).child("available").setValue(true);
                if (callRequest != null)
                {
                    dbRef.child("Requests").child(callRequest.getRequestId()).removeValue();
                    dbRef.child("Chats").child(chat.getChatId()).removeValue();
                    finish();
                }

                if (chat.getCallRequestId() != null)
                {
                    dbRef.child("Requests").child(chat.getCallRequestId()).removeValue();
                    dbRef.child("Chats").child(chat.getChatId()).removeValue();
                    finish();
                }

                else
                {
                    dbRef.child("Chats").child(chat.getChatId()).child("closed").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error ending chat: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendMessage(){
        String message = messageText.getText().toString();
        if ((message.isEmpty() || message == "") && filePath == null) {
            messageText.setError("Please enter a message");
            messageText.requestFocus();
        }
        else {
            String messageId = UUID.randomUUID().toString();

            if (filePath != null)
            {
                uploadFile();
            }
            else
            {
                Message msg = new Message(messageId,FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid().equals(chat.getCustomerId())?chat.getSupportId():chat.getCustomerId(),message, new Date());
                messageList.add(msg);
                dbRef.child("Chats").child(chat.getChatId()).child("messages").setValue(messageList);
                messageText.setText("");
            }
        }
    }

    private void selectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    private void selectFile()
    {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("HELLO", "onActivityResult: ");
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                binding.chatImgSend.setImageBitmap(bitmap);
                binding.cardView.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                binding.cardView.startAnimation(animation);
                binding.chatSend.setVisibility(View.VISIBLE);
                binding.layoutGchatChatbox.setBackgroundColor(Color.parseColor("#EFEFF2"));
                binding.chatGallery.setVisibility(View.GONE);
                binding.chatUploadFile.setVisibility(View.GONE);
                binding.chatFileSend.setVisibility(View.GONE);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }

//        Log.d("RESULTCODE", resultCode + "");
//        Log.d("FILEPATHNODATA", data.getData().toString());
        else if ((requestCode == PICKFILE_RESULT_CODE || requestCode == -1)
                && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            try {
                Log.d("FILEPATHDATA", data.getData().toString());
                Log.d("URII", "onSuccess: ");
                filePath = data.getData();
                binding.cardView.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                binding.cardView.startAnimation(animation);
                binding.chatSend.setVisibility(View.VISIBLE);
                binding.chatFileSend.setText("\uD83D\uDCC2 "+filePath.toString());
                binding.chatFileSend.setVisibility(View.VISIBLE);
                binding.layoutGchatChatbox.setBackgroundColor(Color.parseColor("#EFEFF2"));
                binding.chatGallery.setVisibility(View.GONE);
                binding.chatUploadFile.setVisibility(View.GONE);
                binding.chatImgSend.setVisibility(View.GONE);
            }
            catch (Exception e) {
                // Log the exception
                e.printStackTrace();
            }
        }
        else
        {
            filePath = null;
            binding.chatMessage.clearFocus();
            binding.layoutGchatChatbox.setBackgroundColor(getColor(R.color.white));
            binding.cardView.setVisibility(View.GONE);
            binding.chatSend.setVisibility(View.GONE);
            binding.chatGallery.setVisibility(View.VISIBLE);}
    }

    private void uploadFile()
    {
        binding.chatMessage.clearFocus();
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        binding.layoutGchatChatbox.setBackgroundColor(getColor(R.color.white));
        binding.cardView.startAnimation(animation);
        binding.cardView.setVisibility(View.GONE);
        binding.chatMessage.setText("");
        binding.chatMessage.setFocusableInTouchMode(false);
        binding.chatMessage.setFocusable(false);
        binding.chatSend.setVisibility(View.GONE);

        if (filePath != null) {

            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Sending...");
            progressDialog.show();

            String imageId = UUID.randomUUID().toString();
            StorageReference ref;

            if (filePath.toString().contains("image"))
            {
                ref = storageReference
                        .child(
                                "images/"
                                        + chat.getChatId() + "/"
                                        + imageId);
                Log.d("IMAGEREF", ref.toString());
            }
            else
            {
                ref = storageReference
                        .child(
                                "files/"
                                        + chat.getChatId() + "/"
                                        + imageId);
            }

            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Log.d("URII", "onSuccess: ");
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Image Uploaded", Toast.LENGTH_SHORT).show();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            addFileToDB(uri.toString());
                                        }
                                    });

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ChatActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }


    private void addFileToDB(String url){
        String message = messageText.getText().toString();
        String messageId = UUID.randomUUID().toString();
        Message msg = new Message();
        if (filePath.toString().contains("image"))
        {
           msg = new Message(messageId,FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid().equals(chat.getCustomerId())?chat.getSupportId():chat.getCustomerId(),message,url, new Date());
        }
        else
        {
           msg = new Message(messageId,FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid().equals(chat.getCustomerId())?chat.getSupportId():chat.getCustomerId(),message,null, url, new Date());

        }

        if (msg.getSenderId() != null) {
            messageList.add(msg);
            filePath = null;
            binding.chatMessage.clearFocus();
            binding.layoutGchatChatbox.setBackgroundColor(getColor(R.color.white));
            binding.chatMessage.setFocusableInTouchMode(true);
            binding.chatMessage.setFocusable(true);
            binding.cardView.setVisibility(View.GONE);
            binding.chatSend.setVisibility(View.GONE);
            binding.chatGallery.setVisibility(View.VISIBLE);
            binding.chatUploadFile.setVisibility(View.VISIBLE);
            dbRef.child("Chats").child(chat.getChatId()).child("messages").setValue(messageList);
            messageText.setText("");
        }
        else
        {
            Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileClick(int position) {
        Message message = messageList.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(message.getFile()));
        startActivity(intent);
    }
//    public void createNotification() {
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
//            return;
//        }
//
//        CharSequence name = "QuickConnect";
//        String description = "Channel for Quickconnect notifications";
//        int importance = NotificationManager.IMPORTANCE_DEFAULT;
//        NotificationChannel channel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            channel = new NotificationChannel("notify", name, importance);
//            channel.setDescription(description);
//        }
//
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.createNotificationChannel(channel);
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle(chat.getCustomerName())
//                .setContentText(messageList.get(messageList.size() - 1).getText())
//                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        notificationManager.notify(200, builder.build());
//    }
    //    private void sendImage(String url){
//        String message = messageText.getText().toString();
//        String messageId = UUID.randomUUID().toString();
//        Log.d("MESSAGE1", FirebaseAuth.getInstance().getCurrentUser().getUid().equals(chat.getCustomerId())?chat.getSupportId():chat.getCustomerId());
//        Message msg = new Message(messageId,FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid().equals(chat.getCustomerId())?chat.getSupportId():chat.getCustomerId(),message,url, new Date());
//        Log.d("MESSAGE", msg.getSenderId());
//        Log.d("SENDMESSAGE", "SEND");
//        if (msg.getSenderId() != null)
//        {
//            messageList.add(msg);
//            filePath = null;
//            binding.chatMessage.clearFocus();
//            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//            binding.cardView.startAnimation(animation);
//            binding.layoutGchatChatbox.setBackgroundColor(getColor(R.color.white));
//            binding.cardView.setVisibility(View.GONE);
//            binding.chatSend.setVisibility(View.GONE);
//            binding.chatGallery.setVisibility(View.VISIBLE);
//            dbRef.child("Chats").child(chat.getChatId()).child("messages").setValue(messageList);
//            messageText.setText("");
//        }
//        else
//        {
//            Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void sendFile(String url){
//        String message = messageText.getText().toString();
//        String messageId = UUID.randomUUID().toString();
//        Message msg = new Message(messageId,FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid().equals(chat.getCustomerId())?chat.getSupportId():chat.getCustomerId(),message,null, url, new Date());
//        if (msg.getSenderId() != null)
//        {
//            messageList.add(msg);
//            filePath = null;
//            binding.chatMessage.clearFocus();
//            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//            binding.cardView.startAnimation(animation);
//            binding.layoutGchatChatbox.setBackgroundColor(getColor(R.color.white));
//            binding.cardView.setVisibility(View.GONE);
//            binding.chatSend.setVisibility(View.GONE);
//            binding.chatGallery.setVisibility(View.VISIBLE);
//            dbRef.child("Chats").child(chat.getChatId()).child("messages").setValue(messageList);
//            messageText.setText("");
//        }
//        else
//        {
//            Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
//        }
//    }
//
}



