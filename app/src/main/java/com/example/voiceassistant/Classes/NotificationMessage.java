package com.example.voiceassistant.Classes;

public class NotificationMessage {
    private String text;
    private String timestamp;

    public NotificationMessage() {
        // Обязательный пустой конструктор для Firebase
    }
    public NotificationMessage(String text, String timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }

    public void setNotification(String notification) { this.text = notification; }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}