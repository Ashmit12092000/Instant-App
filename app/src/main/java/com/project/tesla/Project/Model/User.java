package com.project.tesla.Project.Model;

import android.net.Uri;

public class User {

    private String email;
    private String uid;
    private String name;
    private String password;
    private String profile_image;
    private String vid;
    private String video_url;
    private String caption;

    public User(String email, String name, String uid) {
        this.email = email;
        this.name = name;
        this.uid = uid;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage_uri() {
        return profile_image;
    }

    public void setImage_uri(String image_uri) {
        this.profile_image = image_uri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }



}
