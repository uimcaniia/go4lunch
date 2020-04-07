package com.uimainon.go4lunch.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uimainon.go4lunch.service.apiElements.NearbySearch;
import com.uimainon.go4lunch.service.apiElements.Result;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DownloadUrl {


    public List<Result> readPlaceUrl(String placeUrl) throws IOException {

        try (InputStream is = new URL(placeUrl).openStream();
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            GsonBuilder builder = new GsonBuilder();
            builder.serializeNulls();
            Gson gson = builder.create();
            NearbySearch mapper = gson.fromJson(reader, NearbySearch.class);
            List<Result> results = mapper.getResults();

            return results;
        }
    }

}
