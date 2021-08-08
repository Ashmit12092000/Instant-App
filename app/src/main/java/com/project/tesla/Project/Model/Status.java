package com.project.tesla.Project.Model;

public class Status {
    private String image_url,uid,status_id;
    private  long timestamp;


    public Status(String image_url, long timestamp) {
        this.image_url = image_url;
        this.timestamp = timestamp;
    }
    public Status(){

    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSUid() {
        return uid;
    }

    public void setSUid(String uid) {
        this.uid = uid;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

}
