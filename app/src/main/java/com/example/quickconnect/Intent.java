package com.example.quickconnect;

import java.util.List;

public class Intent {
    private String intentId;
    private User user;
    private String message;
    private boolean isSerious;
    private List<Attachment> attachments;

    public Intent(String intentId, User user, String message, boolean isSerious, List<Attachment> attachments) {
        this.intentId = intentId;
        this.user = user;
        this.message = message;
        this.isSerious = isSerious;
        this.attachments = attachments;
    }

    public static class Attachment {
        private String type; // "image", "file", etc.
        private String data; // Base64 encoded data or file path

        public Attachment(String type, String data) {
            this.type = type;
            this.data = data;
        }
    }
}
