package com.example.instagramclone.Model;

public class User {
    private String username;
    private String name;
    private String email;
    private String bio;
    private String userid;
    private String imageurl;

    public User() {
    }

    public User(String username, String name, String email, String bio, String userid, String imageurl) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.userid = userid;
        this.imageurl = imageurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
