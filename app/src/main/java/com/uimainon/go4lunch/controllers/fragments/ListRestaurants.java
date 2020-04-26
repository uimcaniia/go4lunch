package com.uimainon.go4lunch.controllers.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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
import com.uimainon.go4lunch.controllers.RecyclerView.PlacesAutoCompleteAdapter;
import com.uimainon.go4lunch.controllers.activities.ProfileActivity;
import com.uimainon.go4lunch.service.DateService;
import com.uimainon.go4lunch.service.NearByPlaces;
import com.uimainon.go4lunch.service.apiElements.Result;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListRestaurants extends Fragment implements PlacesAutoCompleteAdapter.ClickListener{

    private RecyclerView mRecyclerView;
    private GoogleMap myMap;
    private DateService mDate;
    private Double latitudeUser;
    private Double longitudeUser;
    private ProgressDialog myProgress;
    private SearchView sv;
    private Menu menu;
    private List<Result> originalGooglePlaceData;
    private int nbrUser=0;
    private MenuItem searchItem;
    private RecyclerView mRecyclerViewAutoComplete;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private String week;
    private int goodHourInFrance;
    private int minute;
    private TextView mTextViewNothing;
    private FloatingActionButton floatBtn;
    private View rootView;

    public static ListRestaurants newInstance() {
        ListRestaurants fragment = new ListRestaurants();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mDate = new DateService();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getActivity().setTitle("I'm Hungry!");
        myProgress = new ProgressDialog(getContext());
        myProgress.setTitle("Searching all restaurant. Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        // Display Progress Bar.
        myProgress.show();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        Bundle bundle=getArguments();
        assert bundle != null;

        String url = bundle.getString("url");
        this.latitudeUser = bundle.getDouble("latitude");
        this.longitudeUser = bundle.getDouble("longitude");
        Object[] transferData = new Object[6];

        transferData[0] = latitudeUser;
        transferData[1] = longitudeUser;
        transferData[2] = getContext();
        transferData[3] = "listRestaurants";
        transferData[4] = myMap;
        transferData[5] = getString(R.string.google_maps_key);

        int nbrWeek = mDate.givedayOfWeek();
        this.week = mDate.giveTheDayOfTheWeek(nbrWeek);
        this.minute = mDate.giveMinute();
        this.goodHourInFrance = mDate.giveHour();

        FrameLayout mFram =  getActivity().findViewById(R.id.contain_result_searchview);
        mRecyclerViewAutoComplete = (RecyclerView)mFram.findViewById(R.id.result_searchWidget);
        mRecyclerViewAutoComplete.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewAutoComplete.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getContext(),latitudeUser,longitudeUser);
        mAutoCompleteAdapter.setClickListener(this);
        mAutoCompleteAdapter.notifyDataSetChanged();
        mRecyclerViewAutoComplete.setAdapter(mAutoCompleteAdapter);

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.list_restaurant);
      /*  */
        mTextViewNothing = rootView.findViewById(R.id.textNoRestaurant); // s'affichera si aucune réunion existe
        floatBtn = rootView.findViewById(R.id.btn_refresh_restaurant);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));

        getTheAsyncTaskRestaurant(transferData, this.week, this.goodHourInFrance, this.minute);

        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        this.menu = menu;
        this.searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager)  Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        sv = (SearchView) searchItem.getActionView();
        sv.setQueryHint(Html.fromHtml("<font color = #8D8D8D>Search restaurant</font>"));
        sv.setBackgroundColor(getResources().getColor(R.color.colorBgNavBar));
        // ImageView searchIconTest=sv.findViewById(androidx.appcompat.R.id.search_src_text);
        sv.setIconifiedByDefault(false);
        sv.setSubmitButtonEnabled(false);
        assert searchManager != null;
        sv.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        int searchVoiceId = sv.getContext().getResources().getIdentifier("android:id/search_voice_btn", null, null);
        int searchId = sv.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);

        TextView textView = (TextView) sv.findViewById(id);
        ImageView searchIconVoice =sv.findViewById(searchVoiceId);
        ImageView searchIcon =sv.findViewById(searchId);
        textView.setTextColor(getResources().getColor(R.color.colortopBarLog));
        searchIconVoice.setColorFilter(R.color.colortopBarLog);
        searchIcon.setColorFilter(R.color.colortopBarLog);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("onQueryTextSubmit=>" +query);
                sv.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.equals("")) {
                    mRecyclerViewAutoComplete.setVisibility(View.VISIBLE);
                    mAutoCompleteAdapter.getFilter().filter(query);
                   /* if (mRecyclerViewAutoComplete.getVisibility() == View.GONE) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }*/
                } else {
                    mRecyclerViewAutoComplete.setVisibility(View.GONE);

                    /*if (mRecyclerViewAutoComplete.getVisibility() == View.VISIBLE) {
                        mRecyclerView.setVisibility(View.GONE);
                    }*/
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void click(Place place,String placeId) {
        LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        sv.setQueryHint("");
        int sizeList = this.originalGooglePlaceData.size();
        List<Result> newGooglePlaceData = new ArrayList<>();
        for (int i = 0; i <sizeList ; i++) {
            Result googleNearByPlace = originalGooglePlaceData.get(i);
            if(googleNearByPlace.getPlaceId().equals(placeId)){
                newGooglePlaceData.add(googleNearByPlace);
            }
        }
        taskIsDoneGetDetailsrestaurant(newGooglePlaceData, this.nbrUser, this.week, this.goodHourInFrance, this.minute);
        searchItem.collapseActionView();
        ((ProfileActivity) Objects.requireNonNull(getActivity())).hideKeyboardFrom(Objects.requireNonNull(getContext()));
       // ProfileActivity.hideKeyboardFrom(Objects.requireNonNull(getContext()));//, Objects.requireNonNull(Objects.requireNonNull(getActivity()).getCurrentFocus())
    }
/*    public static void hideKeyboardFrom(Context context, View view) { // close the keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/
/* Skipping most code and I will only show you the most essential. */
    private void getTheAsyncTaskRestaurant(Object[] transferData, String semaine, int goodHourInFrance, int minute) {
        NearByPlaces nearByPlaces = new NearByPlaces(new FragmentCallback() {
            @Override
            public void onTaskDone(List<Result> mGooglePlaceData) {
                Query user = UserHelper.getAllUser();
                setHasOptionsMenu(true);
                user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    //private int nbrWorker = 0;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        originalGooglePlaceData = mGooglePlaceData;
                        nbrUser= Objects.requireNonNull(task.getResult()).size();
                        taskIsDoneGetDetailsrestaurant(mGooglePlaceData, nbrUser, semaine, goodHourInFrance, minute);
                    }
                });
            }
        });
        nearByPlaces.start(transferData);
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
      //  System.out.println("taskIsDoneGetDetailsrestaurant"+mGooglePlaceData);
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
       // System.out.println("configureRecyclerView"+mGooglePlaceData);
        if(mGooglePlaceData.size()==0){ // if no restaurant
            mTextViewNothing.setVisibility(View.VISIBLE);
            floatBtn.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            configBtnRestartListRestaurant(rootView);
        }if(mGooglePlaceData.size()==1){
            mTextViewNothing.setVisibility(View.GONE);
            floatBtn.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            configBtnRestartListRestaurant(rootView);
            ListRestaurantAdapter listRestaurantAdapter = new ListRestaurantAdapter(mGooglePlaceData, nbrWorker);
            this.mRecyclerView.setAdapter(listRestaurantAdapter);

        }if(mGooglePlaceData.size()>1){
            mTextViewNothing.setVisibility(View.GONE);
            floatBtn.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            ListRestaurantAdapter listRestaurantAdapter = new ListRestaurantAdapter(mGooglePlaceData, nbrWorker);
            this.mRecyclerView.setAdapter(listRestaurantAdapter);
        }
    }
    private void configBtnRestartListRestaurant(View view) {
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchItem.collapseActionView();
                ((ProfileActivity) Objects.requireNonNull(getActivity())).hideKeyboardFrom(Objects.requireNonNull(getContext()));
               // ProfileActivity.hideKeyboardFrom(Objects.requireNonNull(getContext()));//, Objects.requireNonNull(Objects.requireNonNull(getActivity()).getCurrentFocus())
                taskIsDoneGetDetailsrestaurant(originalGooglePlaceData, nbrUser, week, goodHourInFrance, minute);
            }
        });
    }
}
