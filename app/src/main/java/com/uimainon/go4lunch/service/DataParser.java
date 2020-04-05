package com.uimainon.go4lunch.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getNearByPlace(JSONObject googlePlace) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String nameOfPlace = "";
        String vicinity = "";
        String latitude = "";
        String longitude = "";
        String id = "";
        String photo_reference ="";
        String opening_hours = "";

        try {
            if (!googlePlace.isNull("name")) {
                nameOfPlace = googlePlace.getString("name");
            }
            if (!googlePlace.isNull("vicinity")) {
                vicinity = googlePlace.getString("vicinity");
            }
            latitude = googlePlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlace.getJSONObject("geometry").getJSONObject("location").getString("lng");

            if (!googlePlace.isNull("photos")) {
                photo_reference = googlePlace.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
              /*  System.out.println(photo);*/
            }
            if (!googlePlace.isNull("opening_hours")) {
                opening_hours = googlePlace.getString("opening_hours");
            }
            id = googlePlace.getString("place_id");

            googlePlaceMap.put("name", nameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("id", id);
            googlePlaceMap.put("photo_reference", photo_reference);
            googlePlaceMap.put("opening_hours", opening_hours);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearByPlaces(JSONArray googlePlaces) {
        int count = googlePlaces.length();
        List<HashMap<String, String>> nearByPlacesList = new ArrayList<>();
        HashMap<String, String> nearByPlaceMap = null;
        for (int i=0; i<count; i++) {
            try {
           /*     System.out.println((JSONObject) googlePlaces.get(i));*/
                nearByPlaceMap = getNearByPlace((JSONObject) googlePlaces.get(i));
                nearByPlacesList.add(nearByPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearByPlacesList;
    }

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllNearByPlaces(jsonArray);
    }
}
