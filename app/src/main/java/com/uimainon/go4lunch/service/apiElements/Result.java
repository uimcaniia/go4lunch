package com.uimainon.go4lunch.service.apiElements;

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

}
