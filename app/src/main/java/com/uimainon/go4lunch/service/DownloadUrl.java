package com.uimainon.go4lunch.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {

    public String readPlaceUrl(String placeUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(placeUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            bufferedReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }

/*    public String getUrlForMarker(Double latitude, Double longitude, String placeType) {

        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location=" + latitude + "," + longitude);
        googleUrl.append("&radius=" + 3000);
        googleUrl.append("&type=" + placeType);
        googleUrl.append("&key=").append(R.string.google_maps_key);
        return googleUrl.toString();
    }




    public String getUrlPicture(String photoReference) {
        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        googleUrl.append("maxheight=400");
        googleUrl.append("&photoreference=").append(photoReference);
        googleUrl.append("&key=").append(""+R.string.google_maps_key);
        return googleUrl.toString();
    }*/
}
