package com.example.voiceassistant.Classes;

import java.util.Date;

public class Message {
    public static String SENT_BY_USER = "user";
    public static String SENT_BY_BOT = "bot";
    private Date timestamp;

    String message;
    String sentBy;

    public Message(String message, String sentBy, Date timestamp) {
        this.message = message;
        this.sentBy = sentBy;
        this.timestamp = timestamp;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getSentBy() { return sentBy; }

    public void setSentBy(String sentBy) { this.sentBy = sentBy; }

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}