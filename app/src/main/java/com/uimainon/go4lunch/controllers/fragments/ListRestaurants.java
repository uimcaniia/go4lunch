package com.uimainon.go4lunch.controllers.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.controllers.RecyclerView.ListRestaurantAdapter;
import com.uimainon.go4lunch.service.DateService;
import com.uimainon.go4lunch.service.NearByPlaces;
import com.uimainon.go4lunch.service.apiElements.Result;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ListRestaurants extends Fragment{

    private RecyclerView mRecyclerView;
    private GoogleMap myMap;
    private DateService mDate;
    private Double latitudeUser;
    private Double longitudeUser;
    private ProgressDialog myProgress;

    public static ListRestaurants newInstance() {
        ListRestaurants fragment = new ListRestaurants();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = new DateService();
        getActivity().setTitle("I'm Hungry!");
        myProgress = new ProgressDialog(getContext());
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        // Display Progress Bar.
        myProgress.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        Bundle bundle=getArguments();
        assert bundle != null;

        String url = bundle.getString("url");
        this.latitudeUser = bundle.getDouble("latitude");
        this.longitudeUser = bundle.getDouble("longitude");
        Object[] transferData = new Object[4];
        transferData[0] = myMap;
        transferData[1] = url;
        transferData[2] = getContext();
        transferData[3] = "listRestaurants";

        DateService mDate = new DateService();
        int nbrWeek = mDate.givedayOfWeek();
        int goodHourInFrance = 0;
        Date today = mDate.formatDateToCompare(mDate.giveYear(), mDate.giveMonth(), mDate.giveDay());
        try {
            goodHourInFrance = mDate.giveTheGoodHourInFrance(mDate.giveYear(), mDate.giveHour(), today);
        } catch (ParseException e) {
            e.printStackTrace();
        }

         String week = mDate.giveTheDayOfTheWeek(nbrWeek);

        getTheAsyncTaskRestaurant(transferData, week, goodHourInFrance, mDate.giveMinute());
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.list_restaurant);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));

        return rootView;
    }

/* Skipping most code and I will only show you the most essential. */
    private void getTheAsyncTaskRestaurant(Object[] transferData, String semaine, int goodHourInFrance, int minute) {
        NearByPlaces nearByPlaces = new NearByPlaces(new FragmentCallback() {
            @Override
            public void onTaskDone(List<Result> mGooglePlaceData) {
                Query user = UserHelper.getAllUser();
                user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private int nbrWorker = 0;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        nbrWorker= Objects.requireNonNull(task.getResult()).size();
                        taskIsDoneGetDetailsrestaurant(mGooglePlaceData, nbrWorker, semaine, goodHourInFrance, minute);
                    }
                });

            }
        });
        nearByPlaces.execute(transferData);
    }
    private double calculateDistance(Double latitudeRestaurant, Double longitudeRestaurant) {
        double distance=0.0;
        Location currentLocation = new Location("currentLocation");
        currentLocation.setLatitude(latitudeUser);
        currentLocation.setLongitude(longitudeUser);

        Location restaurantLocation = new Location("restaurantLocation");
        restaurantLocation.setLatitude(latitudeRestaurant);
        restaurantLocation.setLongitude(longitudeRestaurant);


        distance = currentLocation.distanceTo(restaurantLocation);
        return distance;
    }
    private void taskIsDoneGetDetailsrestaurant(List<Result> mGooglePlaceData, int nbrWorker, String semaine, int goodHourInFrance, int minute) {

        if (!Places.isInitialized()) {
            String gApiKey = this.getString(R.string.google_maps_key);
            Places.initialize(Objects.requireNonNull(getContext()), gApiKey);
        }
        PlacesClient placesClient = Places.createClient(Objects.requireNonNull(getContext()));
        List<Place.Field> placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS, Place.Field.OPENING_HOURS, Place.Field.ADDRESS, Place.Field.NAME);

        for (int i = 0; i <mGooglePlaceData.size() ; i++) {
            Result googleNearByPlace = mGooglePlaceData.get(i);
            double distance = calculateDistance(googleNearByPlace.getGeometry().getLocation().getLat(), googleNearByPlace.getGeometry().getLocation().getLng());
            googleNearByPlace.setDistance(distance);
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(googleNearByPlace.getPlaceId(), placeFields);
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            String resultOpenOrNot = "null";

            if (place.getOpeningHours() != null) {
                resultOpenOrNot = searchRestTimeOpen(place.getOpeningHours().getPeriods(), semaine, goodHourInFrance, minute);
               // System.out.println("name => "+place.getName() +" horaire =>"+resultOpenOrNot+" periode =>"+place.getOpeningHours().getPeriods());
                googleNearByPlace.setOpeningHourDetails(resultOpenOrNot);
            }else{
                googleNearByPlace.setOpeningHourDetails("schedules are not filled in");
            }

            if(place.getPhotoMetadatas() != null){  // Get the photo metadata.
                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(170) // Optional.
                        .setMaxHeight(170) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    googleNearByPlace.setPhotoReference(bitmap);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                    }
                });
            }
                configureRecyclerView(mGooglePlaceData, nbrWorker);
                myProgress.dismiss();
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    System.out.println("Place not found: ");
                }
            });
        }

    }

    private String searchRestTimeOpen(List<Period> periods, String semaine, int goodHourInFrance, int minute) {
        int openHour = 0;
        int openMinute = 0;
        int closeHour = 0;
        int closeMinute = 0;
        int openHourNight = 0;
        int openMinuteNight = 0;
        int closeHourNight = 0;
        int closeMinuteNight = 0;
        String restHour = "";
        int timeToday = goodHourInFrance*60 + minute;

        List<Period> period = new ArrayList<>();
        for (int x = 0; x < periods.size(); x++) {
            String day = periods.get(x).getOpen().getDay().toString();
            String dayClose = periods.get(x).getClose().getDay().toString();
            if (semaine.equals(day)) { // on récupère les période de la journée d'aujourd'hui
                period.add(periods.get(x));
            }
        }
        if(period.size() == 0){ // si pas de période, c'est que c'est fermé
            restHour = "Close today";
        }
        if(period.size() == 1){ // si une ouverture par jour
            openHour = period.get(0).getOpen().getTime().getHours();
            openMinute = period.get(0).getOpen().getTime().getMinutes();
            closeHour = period.get(0).getClose().getTime().getHours();
            closeMinute = period.get(0).getClose().getTime().getMinutes();

            if(period.get(0).getOpen().getDay()!= period.get(0).getClose().getDay()){
                restHour = openingDayNotTheSameDayForClose(false, openHour, openMinute, closeHour, closeMinute, timeToday);
            }else{
                restHour = openingDayNotTheSameDayForClose(true, openHour, openMinute, closeHour, closeMinute, timeToday);
            }
        }

        if(period.size() == 2){ // si 2 ouvertures par jour
            openHour = period.get(0).getOpen().getTime().getHours();
            openMinute = period.get(0).getOpen().getTime().getMinutes();
            closeHour = period.get(0).getClose().getTime().getHours();
            closeMinute = period.get(0).getClose().getTime().getMinutes();

            openHourNight = period.get(1).getOpen().getTime().getHours();
            openMinuteNight = period.get(1).getOpen().getTime().getMinutes();
            closeHourNight = period.get(1).getClose().getTime().getHours();
            closeMinuteNight = period.get(1).getClose().getTime().getMinutes();

            int timeRestau = openHour*60 + openMinute;
            if(timeToday > timeRestau){ // si la première heure d'ouverture est passée
                int timeRestauClose = closeHour*60 +closeMinute; // on vérifie la fermeture
                if(timeToday >= timeRestauClose){ // si première fermeture dépassé, on vérifie la deuxième ouverture

                    if(period.get(0).getOpen().getDay()!= period.get(0).getClose().getDay()){
                        restHour = openingDayNotTheSameDayForClose(false, openHourNight, openMinuteNight, closeHourNight, closeMinuteNight, timeToday);
                    }else{
                        restHour = openingDayNotTheSameDayForClose(true, openHourNight, openMinuteNight, closeHourNight, closeMinuteNight, timeToday);
                    }
                }else{// si première fermeture pas encore passée
                    restHour = giveMeTheGoodFormat(closeHour, closeMinute, false);
                }
            }else{ // si ce n'est pas encore ouvert de la journée
                restHour = giveMeTheGoodFormat(openHour, openMinute, true);
            }
        }
        return restHour;
    }

    private String openingDayNotTheSameDayForClose(boolean b, int openHour, int openMinute, int closeHour, int closeMinute, int timeToday) {
        String restHour = "";
        if(!b){
            int timeRestau = openHour*60 + openMinute;
            if(timeToday > timeRestau){ // si l'heure d'ouverture est passée
                    restHour = "Closing after midnight !";
            }else{
                restHour = giveMeTheGoodFormat(closeHour, closeMinute, false);
            }
        }else{
            int timeRestau = openHour*60 + openMinute;
            if(timeToday > timeRestau){ // si l'heure d'ouverture est passée
                int timeRestauClose = closeHour*60 +closeMinute; // on vérifie la fermeture
                if(timeToday > timeRestauClose){ // si fermeture dépassé
                    restHour = "Close";
                }else{
                    restHour = giveMeTheGoodFormat(closeHour, closeMinute, false);
                }
            }else{ // si ce n'est pas encore ouvert
                restHour = giveMeTheGoodFormat(openHour, openMinute, true);
            }
        }
        return restHour;
    }

    private String giveMeTheGoodFormat(int hour, int minute, boolean open) {
        String restHour = "";
        String openOrOpenning = "";
        if(open) {
            openOrOpenning = "Openning at";
        }else {
            openOrOpenning = "Open until";
        }
            if(hour >= 12){
                int hourInenglish = convertFrHourToEn(hour);
                if(minute == 0){
                    restHour = openOrOpenning+" "+hourInenglish+"pm";
                }else{
                    restHour = openOrOpenning+" "+hourInenglish+"."+minute+"pm";
                }
            }else{
                if(minute == 0){
                    restHour = openOrOpenning+" "+hour+"am";
                }else{
                    restHour = openOrOpenning+" "+hour+"."+minute+"am";
                }
            }

        return restHour;
    }

    private int convertFrHourToEn(int hour) {
        int goodHour = hour;
        switch (hour) {
            case 12:  goodHour = 12;
                break;
            case 13:  goodHour = 1;
                break;
            case 14:  goodHour = 2;
                break;
            case 15:  goodHour = 3;
                break;
            case 16:  goodHour = 4;
                break;
            case 17:  goodHour = 5;
                break;
            case 18:  goodHour = 6;
                break;
            case 19:  goodHour = 7;
                break;
            case 20:  goodHour = 8;
                break;
            case 21:  goodHour = 9;
                break;
            case 22:  goodHour = 10;
                break;
            case 23:  goodHour = 11;
                break;
            case 24:  goodHour = 12;
                break;
            case 0:  goodHour = 12;
                break;
        }
        return goodHour;
    }

    public interface FragmentCallback {
        public void onTaskDone(List<Result> mGooglePlaceData);
    }

    private void configureRecyclerView(List<Result> mGooglePlaceData, int nbrWorker){
        //Configure Adapter & RecyclerView
        ListRestaurantAdapter listRestaurantAdapter = new ListRestaurantAdapter(mGooglePlaceData, nbrWorker);
        this.mRecyclerView.setAdapter(listRestaurantAdapter);
    }
}
