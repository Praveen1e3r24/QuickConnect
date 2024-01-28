package com.example.quickconnect;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat implements Parcelable {
    private String chatId;
    private String supportId;
    private String supportName;
    private String supportTeam;
    private String customerId;
    private String customerName;
    private String category;
    private Date timestamp;
    private List<Message> messages;
    private Boolean isClosed;
    private String callRequestId;

    public Chat(){
    }

    public Chat(String chatId, String supportId, String supportName, String supportTeam, String customerId, String customerName, String topic, Date timestamp, List<Message> messages, Boolean isClosed) {
        this.chatId = chatId;
        this.supportId = supportId;
        this.supportName = supportName;
        this.supportTeam = supportTeam;
        this.customerId = customerId;
        this.customerName = customerName;
        this.category = topic;
        this.timestamp = timestamp;
        this.isClosed = isClosed;

        if (messages == null)
            this.messages = new ArrayList<>();
        else
            this.messages = messages;
    }


    public Chat(String chatId, String supportId, String supportName, String supportTeam, String customerId, String customerName, String category, Date timestamp, List<Message> messages, Boolean isClosed, String callRequestId) {
        this.chatId = chatId;
        this.supportId = supportId;
        this.supportName = supportName;
        this.supportTeam = supportTeam;
        this.customerId = customerId;
        this.customerName = customerName;
        this.category = category;
        this.timestamp = timestamp;
        this.messages = messages;
        this.isClosed = isClosed;
        this.callRequestId = callRequestId;
    }

    protected Chat(Parcel in) {
        chatId = in.readString();
        supportId = in.readString();
        supportName = in.readString();
        supportTeam = in.readString();
        customerId = in.readString();
        customerName = in.readString();
        category = in.readString();
        messages = in.createTypedArrayList(Message.CREATOR);
        byte tmpIsClosed = in.readByte();
        isClosed = tmpIsClosed == 0 ? null : tmpIsClosed == 1;
        callRequestId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeString(supportId);
        dest.writeString(supportName);
        dest.writeString(supportTeam);
        dest.writeString(customerId);
        dest.writeString(customerName);
        dest.writeString(category);
        dest.writeTypedList(messages);
        dest.writeByte((byte) (isClosed == null ? 0 : isClosed ? 1 : 2));
        dest.writeString(callRequestId);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
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

    public String getSupportTeam() {
        return supportTeam;
    }

    public void setSupportTeam(String supportTeam) {
        this.supportTeam = supportTeam;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public String getCallRequestId() {
        return callRequestId;
    }

    public void setCallRequestId(String callRequestId) {
        this.callRequestId = callRequestId;
    }

    public void setUserId(String userId) {
    }
}
