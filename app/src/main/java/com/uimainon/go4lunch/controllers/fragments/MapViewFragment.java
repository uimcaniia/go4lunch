package com.uimainon.go4lunch.controllers.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uimainon.go4lunch.R;

import java.util.Objects;

/*import com.google.android.gms.location.LocationListener;*/

public class MapViewFragment extends Fragment implements OnMapReadyCallback  {

    private MapView mMapView;
    private GoogleMap mMap;

    private double longitude = 0D;
    private double latitude = 0D;
    private static final String TAG = "CurrentLocation";

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Location location = null;
        LocationManager locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            assert locationManager != null;
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location){
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                System.out.println("onLocationChanged" + latitude );
                System.out.println("onLocationChanged" +longitude);
/*                LatLng currentLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));*/
            }

            @Override
            public void onStatusChanged(final String provider, final int status, final Bundle extras) {
            }

            @Override
            public void onProviderEnabled(final String provider) {
            }

            @Override
            public void onProviderDisabled(final String provider) {
            }
        };
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg); //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        return rootView;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        System.out.println("map ready" + latitude );
        System.out.println("map ready" +longitude);
        LatLng ici = new LatLng(latitude, longitude);
        LatLng ici2 = new LatLng(36.421998333333335, -122.08400000000002);
        mMap.addMarker(new MarkerOptions().position(ici).title("Vous êtes ici"));
        mMap.addMarker(new MarkerOptions().position(ici2).title("Vous êtes ici 2 "));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ici));
    }

}
