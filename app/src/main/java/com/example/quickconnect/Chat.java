package com.example.quickconnect;

import java.util.List;

public class Chat {
    private int chatId;
    private String supportId;
    private String customerId;
    private List<Message> messages;

    public Chat(int chatId, String supportId, String customerId, List<Message> messages) {
        this.chatId = chatId;
        this.supportId = supportId;
        this.customerId = customerId;
        this.messages = messages;
    }

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
}
