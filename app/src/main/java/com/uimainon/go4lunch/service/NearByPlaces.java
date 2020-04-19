package com.uimainon.go4lunch.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.api.VoteHelper;
import com.uimainon.go4lunch.controllers.activities.DetailsRestaurantActivity;
import com.uimainon.go4lunch.controllers.fragments.ListRestaurants;
import com.uimainon.go4lunch.controllers.fragments.MapViewFragment;
import com.uimainon.go4lunch.models.User;
import com.uimainon.go4lunch.models.Vote;
import com.uimainon.go4lunch.service.apiElements.NearbySearch;
import com.uimainon.go4lunch.service.apiElements.Result;
import com.uimainon.go4lunch.service.network.GetRestaurantDataService;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.core.content.ContextCompat.getDrawable;

public class NearByPlaces implements GoogleMap.OnMarkerClickListener{

   private List<Result> mGooglePlaceData;
    private Context mContext;
    private String mFragment;
    private GoogleMap mMap;
    private MapViewFragment.FragmentCallback mFragmentCallbackMap;
    private ListRestaurants.FragmentCallback mFragmentCallback;
    private List<HashMap<String, String>> nearByPlacesListRestaurant;
    private Marker mMarker;
    private int PROXIMITY_RADIUS = 1000;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/";

    public NearByPlaces(MapViewFragment.FragmentCallback fragmentCallback) {
        mFragmentCallbackMap = fragmentCallback;
    }
    public NearByPlaces(ListRestaurants.FragmentCallback fragmentCallback) {
        mFragmentCallback = fragmentCallback;
    }

    public void start(Object[] transferData) {
        mFragment = (String) transferData[3];
        mContext = (Context) transferData[2];
        String latitute = String.valueOf((Double) transferData[0]);
        String longitude = String.valueOf((Double) transferData[1]);
        mMap = (GoogleMap) transferData[4];
        String apiKey = (String) transferData[5];

        Gson gson = new GsonBuilder()
                .setLenient()
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GetRestaurantDataService service = retrofit.create(GetRestaurantDataService.class);
        service.getNearbyPlaces("restaurant", latitute + "," + longitude, PROXIMITY_RADIUS, apiKey)
                .enqueue(new Callback<NearbySearch> () {
                    @Override
                    public void onResponse(Call<NearbySearch> call, Response<NearbySearch>  response) {
                        NearbySearch places = response.body();
                        mGooglePlaceData = places.getResults();
                       // System.out.println("resultat requete"+mGooglePlaceData);
                        if(mFragment.equals("listRestaurants")){
                            searchChoiceRestaurantWorker("listRestaurants");
                        }
                        if (mFragment.equals("fragmentMap")) {
                            searchChoiceRestaurantWorker("fragmentMap");
                            mFragmentCallbackMap.onTaskDone(mGooglePlaceData);
                        }
                    }
                    @Override
                    public void onFailure(Call<NearbySearch> call, Throwable t) {
                        System.out.println("nope !");
                        t.printStackTrace();
                    }
                });
        }


    public void displayNearByPlaces() {
        for (int i = 0; i < mGooglePlaceData.size() ; i++) {
            Result googleNearByPlace = mGooglePlaceData.get(i);
           // System.out.println(googleNearByPlace.getName());
            String name = googleNearByPlace.getName();
            Double latitude = googleNearByPlace.getGeometry().getLocation().getLat();//Double.parseDouble(googleNearByPlace.get("lat"));
            Double longitude = googleNearByPlace.getGeometry().getLocation().getLng();//Double.parseDouble(googleNearByPlace.get("lng"));
            // Add marker to the map
            LatLng latLng = new LatLng(latitude, longitude);
            if(!googleNearByPlace.getEatingWorker())
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .icon(bitmapDescriptorFromVector(mContext, R.drawable.ic_restaurant_red_24px)));
            else{
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .icon(bitmapDescriptorFromVector(mContext, R.drawable.ic_restaurant_white_24px)));
            }
            mMarker.setTag(googleNearByPlace.getPlaceId());
            mMap.setOnMarkerClickListener(this);
        }
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable background = getDrawable(context, R.drawable.ic_marker_green);
        if(vectorResId == R.drawable.ic_restaurant_red_24px){
            background = getDrawable(context, R.drawable.ic_marker_orange);
        }
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = getDrawable(context, vectorResId);
        vectorDrawable.setBounds(14, 15, vectorDrawable.getIntrinsicWidth()+5, vectorDrawable.getIntrinsicHeight() + 15);
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

    private void searchChoiceRestaurantWorker(String mFragment){
        UserHelper userHelper = new UserHelper();
        Query listUser = userHelper.getAllUser();
        listUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i <mGooglePlaceData.size() ; i++) {
                       // System.out.println( mGooglePlaceData.get(i).getName());
                        Result googleNearByPlace = mGooglePlaceData.get(i);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User userModel = document.toObject(User.class);
                            if(googleNearByPlace.getPlaceId().equals(userModel.getIdRestaurant())){
                                googleNearByPlace.setEatingWorker(true);
                                break;
                            }else{
                                googleNearByPlace.setEatingWorker(false);
                            }
                        }
                    }
                    if(mFragment.equals("fragmentMap")){
                        displayNearByPlaces();
                    }if(mFragment.equals("listRestaurants")){
                        searchVoteRestaurantWorker();
                    }
                } else {
                    // Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void searchVoteRestaurantWorker(){
        for (int i = 0; i <mGooglePlaceData.size() ; i++) {
            Result googleNearByPlace = mGooglePlaceData.get(i);
            Query listVote = VoteHelper.getAllVotesForRestaurants(googleNearByPlace.getPlaceId());

            listVote.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                private Double nbrVote = 0.00;
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Vote voteModel = document.toObject(Vote.class);
                            this.nbrVote += 1.00;
                        }
                        googleNearByPlace.setNbrVote(this.nbrVote);
                    }
                }
            });
            Query user = UserHelper.getAllUser();
            user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                private int nbrUser = 0;
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User userModel = document.toObject(User.class);
                            if(userModel.getIdRestaurant().equals(googleNearByPlace.getPlaceId()))
                                this.nbrUser += 1;
                        }
                        googleNearByPlace.setNbrworkerEating(this.nbrUser);
                    }
                }
            });
        }
        mFragmentCallback.onTaskDone(mGooglePlaceData);
    }


}
