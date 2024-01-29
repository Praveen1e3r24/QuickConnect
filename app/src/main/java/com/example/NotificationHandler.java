package com.example;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.quickconnect.Chat;
import com.example.quickconnect.ChatActivity;
import com.example.quickconnect.Login;
import com.example.quickconnect.Message;
import com.example.quickconnect.R;
import com.example.quickconnect.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationHandler {

    private static volatile NotificationHandler instance;

    private String userId = FirebaseAuth.getInstance().getUid();
    private Map<String, Chat> chatMap = new HashMap<>();
    private Boolean notificationStatus;
    private Activity activity;
    private Chat currentChat;

    private NotificationHandler() {
        // private constructor
    }

    public static NotificationHandler getInstance() {
        if (instance == null) {
            synchronized (NotificationHandler.class) {
                if (instance == null) {
                    instance = new NotificationHandler();
                }
            }
        }
        return instance;
    }

    public void initialize(Activity activity, Chat chat) {
        this.activity = activity;
        this.currentChat = chat;
        setupNotificationStatus(activity);
    }

    public void initialize(Activity activity) {
        this.activity = activity;
        this.currentChat = null;
        setupNotificationStatus(activity);
    }

    private void setupNotificationStatus(Activity activity) {
        AppPreferences appPreferences = new AppPreferences(activity.getApplicationContext());
        notificationStatus = appPreferences.getNotificationStatus();

        if (notificationStatus == null) {
            notificationStatus = true;
            appPreferences.saveNotificationStatus(true);
        }
    }


    public ValueEventListener checkNewMessage()
    {
          ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot s : snapshot.getChildren())
                {
                    Chat chat = s.getValue(Chat.class);

                    if (chatMap.containsKey(s.getKey()))
                    {
                        Chat oldChat = chatMap.get(s.getKey());
                        List<Message> oldMessages = oldChat.getMessages();
                        List<Message> newMessages = chat.getMessages();

                        if (oldMessages != null && oldMessages.size()>0)
                        {
                            if (oldMessages.size() < newMessages.size() && (chat.getCustomerId().equals(userId) || chat.getSupportId().equals(userId)) && notificationStatus)
                            {
                                if (currentChat == null || !currentChat.getChatId().equals(chat.getChatId()))
                                {
                                    createMessageNotification(chat);
                                }
                            }
                        }
                    }
                    chatMap.put(s.getKey(), chat);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, "Error sending notification: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
          };
          return eventListener;
    }

    private void createMessageNotification(Chat chat)
    {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        List<Message> messageList = chat.getMessages();
        Message msg = messageList.get(messageList.size() - 1);
        String username = null;
        if (userId.equals(msg.getRecipientId()))
        {
            if (userId.equals(chat.getSupportId()))
            {
                username = chat.getSupportName();
            }
            else
            {
                username = chat.getCustomerName();
            }
        }

        if (username != null)
        {
            CharSequence name = "QuickConnect";
            String description = "Channel for Quickconnect notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = new NotificationChannel("notify", name, importance);
                channel.setDescription(description);
            }

            NotificationManager notificationManager = getSystemService(activity.getApplicationContext(), NotificationManager.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(activity.getApplicationContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "notify")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher_foreground))
                    .setContentTitle(username)
                    .setContentText(msg.getText())
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            notificationManager.notify(200, builder.build());
        }
    }
}

//    public NotificationHandler(Activity activity, Chat chat)
//    {
//        this.activity = activity;
//        AppPreferences appPreferences = new AppPreferences(activity.getApplicationContext());
//        notificationStatus = appPreferences.getNotificationStatus();
//
//        if (notificationStatus == null)
//        {
//            notificationStatus = true;
//            appPreferences.saveNotificationStatus(true);
//        }
//        currentChat = chat;
//    }
//
//    public NotificationHandler(Activity activity)
//    {
//        this.activity = activity;
//        AppPreferences appPreferences = new AppPreferences(activity.getApplicationContext());
//        notificationStatus = appPreferences.getNotificationStatus();
//
//        if (notificationStatus == null)
//        {
//            notificationStatus = true;
//            appPreferences.saveNotificationStatus(true);
//        }
//    }
