package com.example.apptrail4;

public class ModelChat {

    String message,reciver,sender,timestamp,message_type,isSeen;


    public ModelChat() {
    }

    public ModelChat(String message, String reciver, String sender, String timestamp, String message_type, String isSeen) {
        this.message = message;
        this.reciver = reciver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.message_type = message_type;
        this.isSeen = isSeen;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }
}
