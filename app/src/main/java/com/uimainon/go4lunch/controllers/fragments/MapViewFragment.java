package com.uimainon.go4lunch.controllers.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.controllers.activities.ProfileActivity;
import com.uimainon.go4lunch.service.NearByPlaces;
import com.uimainon.go4lunch.service.apiElements.Result;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/*import com.google.android.gms.location.LocationListener;*/

public class MapViewFragment extends Fragment implements LocationListener  {

    private ProgressDialog myProgress;
    private GoogleMap myMap;
    private static final String MYTAG = "MYTAG";



    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("I'm Hungry!!");
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
    private void onMyMapReady(GoogleMap googleMap) {
        // Get Google Map from Fragment.
        myMap = googleMap;
        // Set OnMapLoadedCallback Listener.
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                // Map loaded. Dismiss this dialog, removing it from the screen.
                myProgress.dismiss();
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
/*            this.latitude = myLocation.getLatitude();
            this.longitude = myLocation.getLongitude();*/
            //((ProfileActivity)getActivity()).getRestaurantPosition(myLocation.getLatitude(), myLocation.getLongitude(), myMap);
            getRestaurantPosition(myLocation.getLatitude(), myLocation.getLongitude(), myMap);
            ((ProfileActivity)getActivity()).updateFirestoreUserPosition(myLocation.getLatitude(), myLocation.getLongitude());
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
            option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            Marker currentMarker = myMap.addMarker(option);
            currentMarker.showInfoWindow();


        } else {
            Toast.makeText(getContext(), "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }
    }
    private String getUrl(Double latitude, Double longitude, String placeType) {
        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location=" + latitude + "," + longitude);
        googleUrl.append("&radius=" + 1000);
        googleUrl.append("&type=" + placeType);
        googleUrl.append("&key=" + getString(R.string.google_maps_key));
        return googleUrl.toString();
    }
    public void getRestaurantPosition(Double latitude, Double longitude, GoogleMap mMap){
        String url = getUrl(latitude, longitude, "restaurant");
        Object[] transferData = new Object[4];
        transferData[0] = mMap;
        transferData[1] = url;
        transferData[2] = getContext();
        transferData[3] = "fragmentMap";
        getTheAsyncTaskRestaurant(transferData);
        Toast.makeText(getContext(), "Searching for nearby restaurants...", Toast.LENGTH_SHORT).show();
    }

    /* Skipping most code and I will only show you the most essential. */
    private void getTheAsyncTaskRestaurant(Object[] transferData) {
        NearByPlaces nearByPlaces = new NearByPlaces(new FragmentCallback() {
            @Override
            public void onTaskDone(List<Result> mGooglePlaceData) {
                taskIsDoneGetDetailsrestaurant(mGooglePlaceData);
            }
        });
        nearByPlaces.execute(transferData);
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
