package com.uimainon.go4lunch.service.apiElements;

import com.google.gson.annotations.SerializedName;

public class Southwest {
    @SerializedName("lat")

    private Double lat;
    @SerializedName("lng")

    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
