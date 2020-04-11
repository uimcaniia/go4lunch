package com.uimainon.go4lunch.service.apiElements;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("icon")
    private String icon;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("photos")
    private List<Photo> photos = null;

    @SerializedName("place_id")
    private String placeId;

    @SerializedName("reference")
    private String reference;

    @SerializedName("scope")
    private String scope;

    @SerializedName("types")
    private List<String> types = null;

    @SerializedName("vicinity")
    private String vicinity;

    @SerializedName("rating")
    private float rating;

    @SerializedName("opening_hours")
    private OpeningHours openingHours;

    @SerializedName("price_level")
    private Integer priceLevel;

    private Boolean eatingWorker;

    private Double nbrVote;

    private int nbrworkerEating;

    private Bitmap photoReference;

    private String openingHourDetails;

    private double distance;



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public Geometry getGeometry(){return geometry;}

    public String getPlaceId() {
        return placeId;
    }

    public String getReference() {
        return reference;
    }

    public String getScope() {
        return scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public float getRating() {
        return rating;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public Integer getPriceLevel() {
        return priceLevel;
    }

    public Boolean getEatingWorker() {
        return eatingWorker;
    }



    public void setEatingWorker(Boolean eatingWorker) {
        this.eatingWorker = eatingWorker;
    }

    public Double getNbrVote() { return nbrVote; }

    public void setNbrVote(Double nbrVote) { this.nbrVote = nbrVote;}

    public Bitmap getPhotoReference() {return photoReference; }

    public void setPhotoReference(Bitmap photoReference) { this.photoReference = photoReference; }

    public String getOpeningHourDetails() {
        return openingHourDetails;
    }

    public void setOpeningHourDetails(String openingHourDetails) {this.openingHourDetails = openingHourDetails;}

    public int getNbrworkerEating() {
        return nbrworkerEating;
    }

    public void setNbrworkerEating(int nbrworkerEating) {this.nbrworkerEating = nbrworkerEating;}

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
