package com.uimainon.go4lunch.controllers.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.UserHelper;
import com.uimainon.go4lunch.base.BaseActivity;
import com.uimainon.go4lunch.controllers.fragments.ListPeople;
import com.uimainon.go4lunch.controllers.fragments.ListRestaurants;
import com.uimainon.go4lunch.controllers.fragments.MapViewFragment;
import com.uimainon.go4lunch.controllers.fragments.SettingFragment;
import com.uimainon.go4lunch.models.User;

import butterknife.BindView;

public class ProfileActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{//}, MapViewFragment.OnHeadlineSelectedListener {


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
    private GoogleMap myMap;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @BindView(R.id.activity_main_bottom_navigation)
    BottomNavigationView bottomNavigationView;

    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int UPDATE_USERNAME = 30;

    private String idUser = "";
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*System.out.println("création");*/
        this.latitudeUser = 0.0;
        this.longitudeUser = 0.0;

        /*textViewEmail.setTextColor(ContextCompat.getColor(this, R.color.colorBgNavBar));*/
        this.updateUIWhenCreating();
        this.configureToolBar();
        this.configureBottomView();

        this.configureDrawerLayout();
        this.configureNavigationView();
        this.showFirstFragment();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Tells your app's SearchView to use this activity's searchable configuration
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
      //  searchView.setAccessibilityPaneTitle("Search restaurants");
        return true;
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
                    Toast.makeText(this, "You don't have choice a restaurant !", Toast.LENGTH_SHORT).show();
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

    // 1 - Update UI when activity is creating
    //Nous avons créé une méthode nommée updateUIWhenCreating() (1) et appelée dans le  onCreate() (2) de l'activité.
    // Cette dernière récupère l'utilisateur actuellement connecté (via la méthode  getCurrentUser()
    // que nous avons précédemment créée) et qui nous retourne un utilisateur FirebaseUser.
    private void updateUIWhenCreating(){
        if (this.getCurrentUser() != null){
            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                imageProfil = this.getCurrentUser().getPhotoUrl();
            }//Get email & username from Firebase
            email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();
            idUser = this.getCurrentUser().getUid();
            Task<DocumentSnapshot> query = UserHelper.getUser(idUser);
            query.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User userConnect = document.toObject(User.class);
                            idRestaurant = userConnect.getIdRestaurant();
                        } else {
                            System.out.println( "No such document");
                        }
                    } else {
                        System.out.println("get failed");
                    }
                }

            });

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
    private void showFirstFragment(){
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
        if (this.fragmentSetting == null) this.fragmentSetting = SettingFragment.newInstance();
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
            String url = getUrl(this.latitudeUser, this.longitudeUser, "restaurant");
            bundle.putDouble("latitude", this.latitudeUser);
            bundle.putDouble("longitude", this.longitudeUser);

            bundle.putString("url", url);
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

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (origin == SIGN_OUT_TASK) {
                    finish();
                }
            }
        };
    }

    private String getUrl(Double latitude, Double longitude, String placeType) {
        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location=" + latitude + "," + longitude);
        googleUrl.append("&radius=" + 1000);
        googleUrl.append("&type=" + placeType);
        googleUrl.append("&key=" + getString(R.string.google_maps_key));
        return googleUrl.toString();
    }



    public void updateFirestoreUserPosition(Double latitude, Double longitude) {
        UserHelper.updateLattitude(""+latitude, idUser);
        UserHelper.updateLongitude(""+longitude, idUser);
        this.latitudeUser = latitude;
        this.longitudeUser = longitude;

    }

}
