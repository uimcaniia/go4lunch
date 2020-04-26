package com.uimainon.go4lunch.controllers.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.controllers.RecyclerView.PlacesAutoCompleteAdapter;
import com.uimainon.go4lunch.controllers.activities.ProfileActivity;
import com.uimainon.go4lunch.service.NearByPlaces;
import com.uimainon.go4lunch.service.apiElements.Result;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;

/*import com.google.android.gms.location.LocationListener;*/

public class MapViewFragment extends Fragment implements LocationListener, PlacesAutoCompleteAdapter.ClickListener{

    private ProgressDialog myProgress;
    private GoogleMap myMap;

    private SearchView sv;
    private static final String MYTAG = "MYTAG";
    private List<Place.Field> fields;
    private Double latitudeUser;
    private Double longitudeUser;
private MenuItem searchItem;
    AutocompleteSupportFragment autocompleteFragment;
    private Menu menu;
    private RecyclerView mRecyclerView;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("I'm Hungry!!");
        /*setHasOptionsMenu(true);*/
       if (!Places.isInitialized()) {
            Places.initialize(Objects.requireNonNull(getContext()), getString(R.string.google_maps_key), Locale.FRENCH);
        }
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
/*        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(this.latitudeUser-0.2, this.longitudeUser+0.2),
                new LatLng(this.latitudeUser+0.2, this.longitudeUser-0.2));*/

        // Create Progress Bar.
        myProgress = new ProgressDialog(getContext());
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        // Display Progress Bar.
        myProgress.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg); //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        FrameLayout mFram =  getActivity().findViewById(R.id.contain_result_searchview);
        mRecyclerView = (RecyclerView)mFram.findViewById(R.id.result_searchWidget);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));

        assert mapFragment != null;
        // Set callback listener, on Google Map ready.
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });
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
      // ImageView searchIconTest=sv.findViewById(androidx.appcompat.R.id.edit_query);
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
                  // System.out.println("pas vide");
                   mAutoCompleteAdapter.getFilter().filter(query);
                   if (mRecyclerView.getVisibility() == View.GONE) {mRecyclerView.setVisibility(View.VISIBLE);}
               } else {
                   if (mRecyclerView.getVisibility() == View.VISIBLE) {mRecyclerView.setVisibility(View.GONE);}
               }
               return true;
           }
       });
       super.onCreateOptionsMenu(menu, inflater);
   }

    @Override
    public void click(Place place, String placeId) {
        LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)             // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        MarkerOptions option = new MarkerOptions();
        option.title(""+place.getName());
        option.position(latLng);
        Marker currentMarker = myMap.addMarker(option);
        currentMarker.showInfoWindow();
        searchItem.collapseActionView();
        ((ProfileActivity) Objects.requireNonNull(getActivity())).hideKeyboardFrom(Objects.requireNonNull(getContext()));
        //ProfileActivity.hideKeyboardFrom(Objects.requireNonNull(getContext()));//, Objects.requireNonNull(Objects.requireNonNull(getActivity()).getCurrentFocus())
/*        hideKeyboardFrom(Objects.requireNonNull(getContext()), Objects.requireNonNull(Objects.requireNonNull(getActivity()).getCurrentFocus()));*/
        final Handler handler = new Handler();//timer
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentMarker.remove();
            }
        }, 5000);
    }

    private void onMyMapReady(GoogleMap googleMap) {
        // Get Google Map from Fragment.
        myMap = googleMap;
        // Set OnMapLoadedCallback Listener.
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Map loaded. Dismiss this dialog, removing it from the screen.
                showMyLocation();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.setMyLocationEnabled(true);
    }

    // Find Location provider is openning.
    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager)getContext().getSystemService (Context.LOCATION_SERVICE);
        // Criteria to find location provider.
        Criteria criteria = new Criteria();
        // Returns the name of the provider that best meets the given criteria.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(getContext(), "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

    //-------------------------------------------------------------------------
    // Call this method only when you have the permissions to view a user's location.
    private void showMyLocation() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        String locationProvider = this.getEnabledLocationProvider();
        if (locationProvider == null) {
            return;
        }
        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation = null;
        try {
            // This code need permissions (Asked above ***)
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
            // Getting Location.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // With Android API >= 23, need to catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(getContext(), "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return;
        }
        if (myLocation != null) {
            this.latitudeUser = myLocation.getLatitude();
            this.longitudeUser = myLocation.getLongitude();
            setHasOptionsMenu(true);
            mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getContext(),latitudeUser,longitudeUser);
            mAutoCompleteAdapter.setClickListener(this);
            mAutoCompleteAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mAutoCompleteAdapter);
            getRestaurantPosition(myLocation.getLatitude(), myLocation.getLongitude(), myMap);

            ((ProfileActivity) Objects.requireNonNull(getActivity())).updateFirestoreUserPosition(myLocation.getLatitude(), myLocation.getLongitude());
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // Add Marker to Map
            MarkerOptions option = new MarkerOptions();
            option.title("Hello !");
           /* option.snippet("....");*/
            option.position(latLng);
            Marker currentMarker = myMap.addMarker(option);
            currentMarker.showInfoWindow();


        } else {
            Toast.makeText(getContext(), "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }
    }

    public void getRestaurantPosition(Double latitude, Double longitude, GoogleMap mMap){

        Object[] transferData = new Object[6];
        transferData[0] = latitude;
        transferData[1] = longitude;
        transferData[2] = getContext();
        transferData[3] = "fragmentMap";
        transferData[4] = mMap;
        transferData[5] = getString(R.string.google_maps_key);

        getTheAsyncTaskRestaurant(transferData);
        Toast.makeText(getContext(), "Searching for nearby restaurants...", Toast.LENGTH_SHORT).show();
    }

    /* Skipping most code and I will only show you the most essential. */
    private void getTheAsyncTaskRestaurant(Object[] transferData) {
        NearByPlaces nearByPlaces = new NearByPlaces(new FragmentCallback() {
            @Override
            public void onTaskDone(List<Result> mGooglePlaceData) {
                taskIsDoneGetDetailsrestaurant(mGooglePlaceData);
                myProgress.dismiss();
            }
        });
        nearByPlaces.start(transferData);
    }
    private void taskIsDoneGetDetailsrestaurant(List<Result> mGooglePlaceData) {
       // System.out.println(mGooglePlaceData);

    }

    public interface FragmentCallback {
        public void onTaskDone(List<Result> mGooglePlaceData);
    }
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
