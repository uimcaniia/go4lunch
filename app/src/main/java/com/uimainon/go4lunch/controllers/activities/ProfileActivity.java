package com.uimainon.go4lunch.controllers.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.PreferenceHelper;
import com.uimainon.go4lunch.api.RestaurantHelper;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.api.VoteHelper;
import com.uimainon.go4lunch.base.BaseActivity;
import com.uimainon.go4lunch.controllers.fragments.ListPeople;
import com.uimainon.go4lunch.controllers.fragments.ListRestaurants;
import com.uimainon.go4lunch.controllers.fragments.MapViewFragment;
import com.uimainon.go4lunch.controllers.fragments.SettingFragment;
import com.uimainon.go4lunch.models.Preference;
import com.uimainon.go4lunch.models.User;
import com.uimainon.go4lunch.models.Vote;
import com.uimainon.go4lunch.service.DateService;
import com.uimainon.go4lunch.service.Notification;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class ProfileActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    //FOR FRAGMENTS
    // 1 - Declare fragment handled by Navigation Drawer
    private Fragment fragmentSetting;
    private Fragment fragmentMapView;
    private Fragment fragmentListRestaurant;
    private Fragment fragmentListPeople;
    //FOR DATAS
    // 2 - Identify each fragment with a number
    private static final int FRAGMENT_SETTING = 1;
    private static final int FRAGMENT_MAP = 2;
    private static final int FRAGMENT_RESTAURANT = 3;
    private static final int FRAGMENT_PEOPLE = 4;

    String username = "";
    String email = "";
    String idRestaurant = "null";
    Uri imageProfil;
    Double latitudeUser;
    Double longitudeUser;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @BindView(R.id.activity_main_bottom_navigation)
    BottomNavigationView bottomNavigationView;

    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    private String idUser = "";
    private int hourPref = 12;
    private int minutePref = 0;
    private int mondayPref = 1;
    private int tuesdayPref = 1;
    private int wednesdayPref = 1;
    private int thursdayPref = 1;
    private int fridayPref = 1;
    private int saturdayPref = 0;
    private int sundayPref = 0;
    private Intent serviceIntent;
/*    private MyService myService;*/

    private  DateService mDate = null;
    private String week;
/*    private String weekPref;*/
    private int nbrWeek;
    private int goodHourInFrance;
    private int minute;
    private int prefOfDay = 0; // oui ou non si alarm aujourd'hui
/*    private int hourNow;*/
    private PlacesClient placesClient;
    private Preference preferenceUser;

    Context ctx;
    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.latitudeUser = 0.0;
        this.longitudeUser = 0.0;
        ctx = this;

        try {
            this.mDate = new DateService();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.week= this.mDate.giveTheDayOfTheWeek(this.mDate.givedayOfWeek());
        this.nbrWeek = mDate.givedayOfWeek();
        this.week = mDate.giveTheDayOfTheWeek(this.nbrWeek);
        this.minute = mDate.giveMinute();
        this.goodHourInFrance = mDate.giveHour();

        if (!Places.isInitialized()) {
            String gApiKey = getString(R.string.google_maps_key);
            Places.initialize(getApplicationContext(), gApiKey);
        }
        this.placesClient = Places.createClient(getApplicationContext());
        this.configureToolBar();
        this.configureBottomView();
        this.configureDrawerLayout();
        this.showFirstFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWhenCreating();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_profile;
    }

    @Override// Handle navigation view item clicks here.
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.profile_activity_your_lunch :
                if(!idRestaurant.equals("null")){
                    Intent intentLunch = new Intent(this, DetailsRestaurantActivity.class);
                    intentLunch.putExtra("idRestaurant", idRestaurant);
                    startActivity (intentLunch);
                }else{
                    Toast toast = Toast.makeText(this, "You don't have choice a restaurant !", Toast.LENGTH_LONG);
                    View view = toast.getView();
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    TextView text = (TextView) view.findViewById(android.R.id.message);
                    text.setTextColor(getResources().getColor(R.color.colorBgNavBar));
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
            case R.id.profile_activity_setting:
                this.showFragment(FRAGMENT_SETTING);
                break;
            case R.id.profile_activity_logout:
                this.signOutUserFromFirebase();
                break;
            case R.id.profile_activity_chat:
                Intent intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    // 2 - Configure BottomNavigationView
    private Boolean bottomNavigationItemmSelected(Integer integer){
        switch (integer) {
            case R.id.action_map:
                this.showFragment(FRAGMENT_MAP);
                break;
            case R.id.action_list:
                this.showFragment(FRAGMENT_RESTAURANT);
                break;
            case R.id.action_people:
                this.showFragment(FRAGMENT_PEOPLE);
                break;
        }
        return true;
    }
    // 2 - Configure BottomNavigationView Listener
    private void configureBottomView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> bottomNavigationItemmSelected(item.getItemId()));
    }
    // 1 - Configure Toolbar
   private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
   }
    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBgNavBar)));
        navigationView.setItemIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBgNavBar)));

        if(!idRestaurant.equals("null")){
            navigationView.getMenu()
                    .findItem(R.id.profile_activity_your_lunch).setCheckable(true).setChecked(false);
        }else{
            navigationView.getMenu()
                    .findItem(R.id.profile_activity_your_lunch).setCheckable(true).setChecked(true);
        }
        View hView = navigationView.getHeaderView(0);
        TextView textUserName = (TextView) hView.findViewById(R.id.userName);
        textUserName.setText(username);
        TextView textViewEmail = (TextView) hView.findViewById(R.id.userMail);
        textViewEmail.setText(email);
        textViewEmail.setTextColor(ContextCompat.getColor(this, R.color.colorBgNavBar));

        ImageView imageViewProfile = (ImageView) hView.findViewById(R.id.imageViewProfile);
        Glide.with(this)
                .load(imageProfil)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewProfile);
    }
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void configureAlarmNotification(long timeInmiliForAlarm, int alarm, long hourNow) { //String idRestaurant
        StringBuffer passToNotification = new StringBuffer();
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(idRestaurant, placeFields);
        this.placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            passToNotification.append(place.getName() + " - " + place.getAddress());
            Query query = UserHelper.getAllUser();
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<User> listUser = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            User userModel = document.toObject(User.class);
                            assert userModel != null;
                            if (userModel.getIdRestaurant().equals(idRestaurant)) {
                                listUser.add(userModel);
                            }
                        }
                        if (listUser.size() == 0) {
                            passToNotification.append(". No worker eat with you");
                        } else {
                            passToNotification.append(". Eat with you : ");
                            for (int i = 0; i < listUser.size(); i++) {
                                if ((i == (listUser.size() - 1)) && (!listUser.get(i).getUid().equals(idUser))) {
                                    passToNotification.append(listUser.get(i).getUsername());
                                }
                                if ((i == (listUser.size() - 2)) && (!listUser.get(i).getUid().equals(idUser))) {
                                    passToNotification.append(listUser.get(i).getUsername() + " and ");
                                }
                                if ((i != (listUser.size() - 1)) && (i != (listUser.size() - 2)) && (!listUser.get(i).getUid().equals(idUser))) {
                                    passToNotification.append(listUser.get(i).getUsername() + ", ");
                                }
                            }
                        }
                        /*System.out.println("send : "+passToNotification);*/
                        Intent myIntent = new Intent(ctx, Notification.class);
                        myIntent.putExtra("restaurant", (Serializable) passToNotification);
                        int ALARM1_ID = 10000;
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                ctx, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager)(ctx.getSystemService( Context.ALARM_SERVICE ));
                        if(alarm > hourNow) {
                            System.out.println("send : "+passToNotification);
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInmiliForAlarm, AlarmManager.INTERVAL_DAY, pendingIntent);
                        }else{
                            System.out.println("stopp alarm");
                            alarmManager.cancel(pendingIntent);
                        }
                    }
                }
            });
        });
    }
    //configure fragment setting avec les préférences des notifications
    public void configurePreferenceUser(){
        Task<DocumentSnapshot> queryPref = PreferenceHelper.getPreferenceForUser(idUser);
        queryPref.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        DateService mDateService = null;
                        try {
                            mDateService = new DateService();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        preferenceUser = document.toObject(Preference.class);
                        int hourNow = mDateService.giveTimeNow();
                        long timeInmiliForAlarm = mDateService.giveTheAlarmTimeMilli(preferenceUser.getHour(),preferenceUser.getMinute());
                        prefOfDay = mDateService.giveThepreferenceOfDay(preferenceUser, week); //1 si alarm sinon 0

                        int hourAlarm = ((preferenceUser.getHour()*60)*60) + (preferenceUser.getMinute()*60);
         /*               System.out.println( "hourAlarm => "+ hourAlarm+ " / hourNow => "+hourNow);*/
                      /*  AlarmManager alarmManager = (AlarmManager)(ctx.getSystemService( Context.ALARM_SERVICE ));*/
                        if((!idRestaurant.equals("null")) && (prefOfDay == 1)){ // && (hourAlarm > hourNow)
                            configureAlarmNotification(timeInmiliForAlarm, hourAlarm, hourNow);//idRestaurant
                        }
                    } else {
                        System.out.println( "No such document Pref");
                        PreferenceHelper.createPreference(mondayPref, tuesdayPref, wednesdayPref, thursdayPref, fridayPref,saturdayPref, sundayPref, hourPref, minutePref ,idUser);
                        preferenceUser = new Preference(mondayPref, tuesdayPref, wednesdayPref, thursdayPref, fridayPref,saturdayPref, sundayPref, hourPref, minutePref ,idUser);
                    }
                } else {
                    System.out.println("get failed pref");
                }
            }
        });
    }

    private void configureProfilUser(String idUser) {
        Task<DocumentSnapshot> query = UserHelper.getUser(idUser);
        query.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User userConnect = document.toObject(User.class);
                        idRestaurant = userConnect.getIdRestaurant();
                        configureNavigationView();
                        configurePreferenceUser(); //, idRestaurant
                    } else {
                        System.out.println( "No such document user");
                    }
                } else {
                    System.out.println("get failed user");
                }
            }
        });
    }
    // 1 - Update UI when activity is creating
    //Nous avons créé une méthode nommée updateUIWhenCreating() (1) et appelée dans le  onCreate() (2) de l'activité.
    // Cette dernière récupère l'utilisateur actuellement connecté (via la méthode  getCurrentUser()
    // que nous avons précédemment créée) et qui nous retourne un utilisateur FirebaseUser.
    public void updateUIWhenCreating(){
        if (this.getCurrentUser() != null){
            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                imageProfil = this.getCurrentUser().getPhotoUrl();
            }//Get email & username from Firebase
            email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();
            idUser = this.getCurrentUser().getUid();
            configureProfilUser(idUser);
        }
    }

    private void showFragment(int fragmentIdentifier){ // 5 - Show fragment according an Identifier
        switch (fragmentIdentifier){
            case FRAGMENT_SETTING:
                this.showSettingFragment();
                break;
            case FRAGMENT_MAP:
                this.showMapViewFragment();
                break;
            case FRAGMENT_RESTAURANT:
                this.showListRestaurantFragment();
                break;
            case FRAGMENT_PEOPLE:
                this.showListPeopleFragment();
                break;
            default:
                break;
        }
    }
    // 1 - Show first fragment when activity is created
    public void showFirstFragment(){
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (visibleFragment == null){
            // 1.1 - Show News Fragment
            this.showFragment(FRAGMENT_MAP);
            // 1.2 - Mark as selected the menu item corresponding to NewsFragment
            this.navigationView.getMenu().getItem(2).setChecked(true);
        }
    }

    // 4 - Create each fragment page and show it
    private void showSettingFragment(){
        if (this.fragmentSetting == null){
            this.fragmentSetting = SettingFragment.newInstance();
        }
        Bundle bundle=new Bundle();
        bundle.putString("idUser", this.idUser);
        bundle.putInt("hour", this.preferenceUser.getHour());
        bundle.putInt("minute", this.preferenceUser.getMinute());
        bundle.putInt("monday", this.preferenceUser.getMonday());
        bundle.putInt("tuesday", this.preferenceUser.getTuesday());
        bundle.putInt("wednesday", this.preferenceUser.getWednesday());
        bundle.putInt("thursday", this.preferenceUser.getThursday());
        bundle.putInt("friday", this.preferenceUser.getFriday());
        bundle.putInt("saturday", this.preferenceUser.getSaturday());
        bundle.putInt("sunday", this.preferenceUser.getSunday());
        this.fragmentSetting.setArguments(bundle);
        this.startTransactionFragment(this.fragmentSetting);
    }

    private void showMapViewFragment(){
        if (this.fragmentMapView == null) this.fragmentMapView = MapViewFragment.newInstance();
        this.startTransactionFragment(this.fragmentMapView);
    }
    private void showListRestaurantFragment(){
        if (this.fragmentListRestaurant == null){
            this.fragmentListRestaurant = ListRestaurants.newInstance();
        }
            Bundle bundle=new Bundle();
            bundle.putDouble("latitude", this.latitudeUser);
            bundle.putDouble("longitude", this.longitudeUser);
            this.fragmentListRestaurant.setArguments(bundle);
            this.startTransactionFragment(this.fragmentListRestaurant);
    }
    private void showListPeopleFragment() {
        if (this.fragmentListPeople == null) {
            this.fragmentListPeople = ListPeople.newInstance();
        }
        Bundle valueIdUser = new Bundle();
        valueIdUser.putString("idUser", idUser);
        this.fragmentListPeople.setArguments(valueIdUser);
        this.startTransactionFragment(this.fragmentListPeople);
    }

    // 3 - Generic method that will replace and show a fragment inside the MainActivity Frame Layout
    public void startTransactionFragment(Fragment fragment){
        hideKeyboardFrom(this);
        if (!fragment.isVisible()){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment).commit();
        }
    }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 1 - Create http requests (SignOut & Delete)
    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }
    public void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            PreferenceHelper.deleteUser(this.idUser);
            CollectionReference query = RestaurantHelper.getRestaurantsCollection();
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Query listVote = VoteHelper.getAllVotesForRestaurants(document.getId());
                            listVote.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            Vote voteModel = document.toObject(Vote.class);
                                            if(voteModel.getIdUserVote().equals(idUser)){
                                                VoteHelper.deleteVoteForRestaurant(document.getId(), idUser);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
            UserHelper.deleteUser(idUser).addOnFailureListener(this.onFailureListener());
        }
    }

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void updateFirestoreUserPosition(Double latitude, Double longitude) {
        UserHelper.updateLattitude(""+latitude, idUser);
        UserHelper.updateLongitude(""+longitude, idUser);
        this.latitudeUser = latitude;
        this.longitudeUser = longitude;

    }
    public void hideKeyboardFrom(Context context) { // close the keyboard , View view
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
