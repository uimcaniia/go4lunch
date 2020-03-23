package com.uimainon.go4lunch.controllers.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.base.BaseActivity;
import com.uimainon.go4lunch.controllers.fragments.ListPeople;
import com.uimainon.go4lunch.controllers.fragments.ListRestaurants;
import com.uimainon.go4lunch.controllers.fragments.MapView;
import com.uimainon.go4lunch.controllers.fragments.SettingFragment;
import com.uimainon.go4lunch.controllers.fragments.YourLunch;

public class ProfileActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    //FOR FRAGMENTS
    // 1 - Declare fragment handled by Navigation Drawer
    private Fragment fragmentYourLunch;
    private Fragment fragmentSetting;
    private Fragment fragmentMapView;
    private Fragment fragmentListRestaurant;
    private Fragment fragmentListPeople;

    //FOR DATAS
    // 2 - Identify each fragment with a number
    private static final int FRAGMENT_LUNCH = 0;
    private static final int FRAGMENT_SETTING = 1;
    private static final int FRAGMENT_MAP = 2;
    private static final int FRAGMENT_RESTAURANT = 3;
    private static final int FRAGMENT_PEOPLE = 4;

    String username = "";
    String email = "";
    Uri imageProfil;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.configureToolBar();
        this.updateUIWhenCreating();
        this.configureDrawerLayout();
        this.configureNavigationView();
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                this.showFragment(FRAGMENT_LUNCH);
                break;
            case R.id.profile_activity_setting:
                this.showFragment(FRAGMENT_SETTING);
                break;
            case R.id.profile_activity_logout:
                this.signOutUserFromFirebase();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        View hView = navigationView.getHeaderView(0);

        TextView textUserName = (TextView) hView.findViewById(R.id.userName);
        textUserName.setText(username);

        TextView textViewEmail = (TextView) hView.findViewById(R.id.userMail);
        textViewEmail.setText(email);

        ImageView imageViewProfile = (ImageView) hView.findViewById(R.id.imageViewProfile);
        Glide.with(this)
                .load(imageProfil)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewProfile);
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
                }
            //Get email & username from Firebase
            email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();
        }
    }
    // 5 - Show fragment according an Identifier

    private void showFragment(int fragmentIdentifier){
        switch (fragmentIdentifier){
            case FRAGMENT_LUNCH :
                this.showYourLunchFragment();
                break;
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

    // 4 - Create each fragment page and show it

    private void showYourLunchFragment(){
        if (this.fragmentYourLunch == null) this.fragmentYourLunch = YourLunch.newInstance();
        this.startTransactionFragment(this.fragmentYourLunch);
    }

    private void showSettingFragment(){
        if (this.fragmentSetting == null) this.fragmentSetting = SettingFragment.newInstance();
        this.startTransactionFragment(this.fragmentSetting);
    }
    private void showMapViewFragment(){
        if (this.fragmentMapView == null) this.fragmentMapView = MapView.newInstance();
        this.startTransactionFragment(this.fragmentMapView);
    }
    private void showListRestaurantFragment(){
        if (this.fragmentListRestaurant == null) this.fragmentListRestaurant = ListRestaurants.newInstance();
        this.startTransactionFragment(this.fragmentListRestaurant);
    }
    private void showListPeopleFragment(){
        if (this.fragmentListPeople == null) this.fragmentListPeople = ListPeople.newInstance();
        this.startTransactionFragment(this.fragmentListPeople);
    }
    // 3 - Generic method that will replace and show a fragment inside the MainActivity Frame Layout
    private void startTransactionFragment(Fragment fragment){
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

}
