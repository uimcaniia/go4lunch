package com.uimainon.go4lunch.models;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    private String email;
    private int latLng;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, String username, String email, int latLng, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.latLng = latLng;
        this.urlPicture = urlPicture;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getEmail() { return email;}
    public int getLatLng() { return latLng; }
    public String getUrlPicture() { return urlPicture; }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) {this.email = email;}
    public void setLatLng(int latLng) { this.latLng = latLng;}
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }




}
