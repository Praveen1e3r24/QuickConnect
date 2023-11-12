package com.example.quickconnect;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Chat implements Parcelable {
    private int chatId;
    private String supportId;
    private String supportName;
    private static String supportTeam;

    private String customerId;
    private String customerName;
    private String category;
    private List<Message> messages;

    public Chat(int chatId, String supportId, String supportName, String supportTeam, String customerId, String customerName, String topic, List<Message> messages) {
        this.chatId = chatId;
        this.supportId = supportId;
        this.supportName = supportName;
        this.supportTeam = supportTeam;
        this.customerId = customerId;
        this.customerName = customerName;
        this.category = topic;
        this.messages = messages;
    }

    protected Chat(Parcel in) {
        chatId = in.readInt();
        supportId = in.readString();
        supportName = in.readString();
        customerId = in.readString();
        customerName = in.readString();
        category = in.readString();
        supportTeam = in.readString();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getSupportId() {
        return supportId;
    }

    public void setSupportId(String supportId) {
        this.supportId = supportId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupportName() {
        return supportName;
    }

    public void setSupportName(String supportName) {
        this.supportName = supportName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(chatId);
        dest.writeString(supportId);
        dest.writeString(supportName);
        dest.writeString(customerId);
        dest.writeString(customerName);
        dest.writeString(category);
        dest.writeString(supportTeam);
    }
}
