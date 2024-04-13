package com.example.android1finalproject.models;

import com.google.firebase.firestore.FieldValue;

import java.text.SimpleDateFormat;

public class Message {

    private String messageText;
    private String senderId;
    private String receiverId;
    private FieldValue timestamp;
    private String senderName;
    private String reciverName;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReciverName() {
        return reciverName;
    }

    public void setReciverName(String reciverName) {
        this.reciverName = reciverName;
    }

    public Message() {
    }

    public Message(String messageText, String senderId, String receiverId, FieldValue timestamp, String senderName, String reciverName) {
        this.messageText = messageText;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.reciverName = reciverName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public FieldValue getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(FieldValue timestamp) {
        this.timestamp = timestamp;
    }



}
