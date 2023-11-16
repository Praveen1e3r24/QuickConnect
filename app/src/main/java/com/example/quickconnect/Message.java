package com.example.quickconnect;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Message implements Parcelable {
    private String messageId;
    private String senderId;
    private String recipientId;
    private String text;
    private String image;
    private Date timestamp;

    public Message() {
    }

    public Message(String messageId, String senderId, String recipientId, String text, Date timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Message(String messageId, String senderId, String recipientId, String text, String image, Date timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.text = text;
        this.image = image;
        this.timestamp = timestamp;
    }

    protected Message(Parcel in) {
        messageId = in.readString();
        senderId = in.readString();
        recipientId = in.readString();
        text = in.readString();
        image = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(messageId);
        dest.writeString(senderId);
        dest.writeString(recipientId);
        dest.writeString(text);
        dest.writeString(image);
    }
}
