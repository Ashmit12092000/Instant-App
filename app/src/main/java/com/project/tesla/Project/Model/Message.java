package com.project.tesla.Project.Model;

import android.graphics.Bitmap;

public class Message {
    private String senderID,recieverID,message,messageID,image_uri,video_uri;
    private long timestamp;
    Bitmap setbitMapImage_uri;
    private int feeling;
    private Boolean msg_seen;
    private  String pdf_uri;
    private String filename;

    public Message(String senderID, String message, long timestamp) {
        this.senderID = senderID;
        this.message = message;
        this.timestamp = timestamp;
    }
    public Message(String senderID, String message, long timestamp,String recieverID) {
        this.senderID = senderID;
        this.message = message;
        this.timestamp = timestamp;
        this.recieverID=recieverID;
    }
    public Message() {
    }

    public String getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(String recieverID) {
        this.recieverID = recieverID;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getVideo_uri() {
        return video_uri;
    }

    public void setVideo_uri(String video_uri) {
        this.video_uri = video_uri;
    }

    public Boolean getIs_seen() {
        return msg_seen;
    }

    public String getPdf_uri() {
        return pdf_uri;
    }

    public void setPdf_uri(String pdf_uri) {
        this.pdf_uri = pdf_uri;
    }


    public void setMsg_seen(Boolean msg_seen) {
        this.msg_seen = msg_seen;
    }

    public Bitmap getSetbitMapImage_uri() {
        return setbitMapImage_uri;
    }

    public void setSetbitMapImage_uri(Bitmap setbitMapImage_uri) {
        this.setbitMapImage_uri = setbitMapImage_uri;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageID() {
        return messageID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }
}
