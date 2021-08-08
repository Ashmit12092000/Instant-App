package com.project.tesla.Project.Model;

import android.graphics.Bitmap;

public class Post {
    String caption_txt,image_uri;
    String post_id;
    String post_type;
    String post_count_id_key;
    Bitmap bitmap;
    String Filename;
    String uid;
    long timestamp;
    String username;
    String likes;
    String userprofile_photo;
    private String video_url;
    private String vid;

    public Post(String caption_txt, String image_uri,long timestamp) {
        this.caption_txt = caption_txt;
        this.image_uri = image_uri;
        this.timestamp=timestamp;
    }

    public String getPost_count_id_key() {
        return post_count_id_key;
    }

    public void setPost_count_id_key(String post_count_id_key) {
        this.post_count_id_key = post_count_id_key;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUsername() {
        return username;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Post() {
    }

    public String getUserprofile_photo() {
        return userprofile_photo;
    }

    public void setUserprofile_photo(String userprofile_photo) {
        this.userprofile_photo = userprofile_photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCaption_txt() {
        return caption_txt;
    }

    public void setCaption_txt(String caption_txt) {
        this.caption_txt = caption_txt;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }
    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }
}
