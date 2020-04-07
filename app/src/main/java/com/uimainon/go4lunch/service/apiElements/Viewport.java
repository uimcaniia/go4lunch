package com.uimainon.go4lunch.service.apiElements;

import com.google.gson.annotations.SerializedName;

public class Viewport {
    @SerializedName("northeast")

    private Northeast northeast;
    @SerializedName("southwest")

    private Southwest southwest;

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }
}
