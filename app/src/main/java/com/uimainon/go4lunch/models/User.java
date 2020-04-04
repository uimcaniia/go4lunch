package com.uimainon.go4lunch.models;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    private String email;
    private Double latitude;
    private Double longitude;
    private String idRestaurant;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, String username, String urlPicture, String email, String idRestaurant) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.idRestaurant = "null";
        this.urlPicture = urlPicture;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getEmail() { return email;}
    public Double getLongitude() {return longitude;}
    public Double getLatitude() {return latitude;}
    public String getIdRestaurant() {return idRestaurant;  }
    public String getUrlPicture() { return urlPicture; }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) {this.email = email;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
    public void setLatitude(Double latitude) { this.latitude = latitude;}
    public void setIdRestaurant(String idRestaurant) {this.idRestaurant = idRestaurant; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }





}
