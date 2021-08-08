package com.project.tesla.Project.Model;

public class Comments {
    String commentsID,comment_msg,comments_username,senderID,PostID;
    long timestamp;

    public Comments(String senderID,String PostID, String comment_msg,  long timestamp) {
        this.senderID=senderID;
        this.PostID=PostID;
        this.comment_msg = comment_msg;
        this.timestamp = timestamp;
    }

    public Comments() {
    }

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        PostID = postID;
    }

    public String getCommentsID() {
        return commentsID;
    }

    public void setCommentsID(String commentsID) {
        this.commentsID = commentsID;
    }

    public String getComment_msg() {
        return comment_msg;
    }

    public void setComment_msg(String comment_msg) {
        this.comment_msg = comment_msg;
    }

    public String getComments_username() {
        return comments_username;
    }

    public void setComments_username(String comments_username) {
        this.comments_username = comments_username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
