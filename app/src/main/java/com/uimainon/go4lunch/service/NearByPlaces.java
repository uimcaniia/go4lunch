package com.uimainon.go4lunch.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.Query;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.controllers.activities.DetailsRestaurantActivity;
import com.uimainon.go4lunch.models.Vote;
import com.uimainon.go4lunch.service.apiElements.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class NearByPlaces extends AsyncTask<Object, String, Object[]> implements GoogleMap.OnMarkerClickListener {

    private List<Result> mGooglePlaceData;
    private String mUrl;
    private GoogleMap mMap;
    private Context mContext;
    private Vote mRestaurant;

    private List<HashMap<String, String>> nearByPlacesListRestaurant;
    private Marker mMarker;

    @Override
    protected Object[] doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        mUrl = (String) objects[1];
        mContext = (Context) objects[2];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            mGooglePlaceData = downloadUrl.readPlaceUrl(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
       // System.out.println("ici => "+mGooglePlaceData);
        return objects;
    }

    @Override
    protected void onPostExecute(Object[] s) {
        super.onPostExecute(s);
        UserHelper userHelper = new UserHelper();
        Query listUser = userHelper.getAllUser();
        displayNearByPlaces();
    }

/*    private List<HashMap<String, String>> searchChoiceRestaurantWorker(Query listUser, List<HashMap<String, String>> nearByPlacesList){
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //System.out.println(document.getId() + " => " + document.getData());
                            }
                        } else {
                           // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return nearByPlacesList;
    }*/
    public void displayNearByPlaces() {

        for (int i = 0; i <mGooglePlaceData.size() ; i++) {
            Result googleNearByPlace = mGooglePlaceData.get(i);
            /*System.out.println(mGooglePlaceData.get(i));*/
            String name = googleNearByPlace.getName();
            Double latitude = googleNearByPlace.getGeometry().getLocation().getLat();//Double.parseDouble(googleNearByPlace.get("lat"));
            Double longitude = googleNearByPlace.getGeometry().getLocation().getLng();//Double.parseDouble(googleNearByPlace.get("lng"));
            // Add marker to the map
            LatLng latLng = new LatLng(latitude, longitude);
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .icon(bitmapDescriptorFromVector(mContext, R.drawable.ic_restaurant_red_24px)));
            mMarker.setTag(googleNearByPlace.getPlaceId());
            mMap.setOnMarkerClickListener(this);
        }
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_marker_green);
        if(vectorResId == R.drawable.ic_restaurant_red_24px){
            background = ContextCompat.getDrawable(context, R.drawable.ic_marker_orange);
        }
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(2, 15, vectorDrawable.getIntrinsicWidth()+2, vectorDrawable.getIntrinsicHeight() + 15);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        HashMap<String, String> restaurant;
        String idRestaurant = (String) marker.getTag();
        Intent intent = new Intent(mContext, DetailsRestaurantActivity.class);

        assert mContext != null;
        intent.putExtra("idRestaurant", idRestaurant);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity (intent);

        return false;
    }

}
