package com.example.quickconnect;

public class ChatRequestItem {
    private Chat chat;
    private CallRequest callRequest;

    public ChatRequestItem(Chat chat, CallRequest callRequest) {
        this.chat = chat;
        this.callRequest = callRequest;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public CallRequest getCallRequest() {
        return callRequest;
    }

    public void setCallRequest(CallRequest callRequest) {
        this.callRequest = callRequest;
    }

    public boolean isChat() {
        return chat != null;
    }

    public boolean isCallRequest() {
        return callRequest != null;
    }
}
