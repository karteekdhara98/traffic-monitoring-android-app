package com.example.kunal.mapsapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng loc1, loc2, loc3, loc4;
    private int state = 0;
    private int beta_val;
    private Marker mark1, mark2, mark3, mark4, mark;
    public static final String ROOT_URL = "http://jsonplaceholder.typicode.com/";

    //Strings to bind with intent will be used to send data to other activity
//   public static final String KEY_BOOK_ID = "key_book_id";
//    public static final String KEY_BOOK_NAME = "key_book_name";
//    public static final String KEY_BOOK_PRICE = "key_book_price";
//    public static final String KEY_BOOK_STOCK = "key_book_stock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Creating a rest adapter
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL)
                .build();
        //Creating an object of our api interface
        HttpRequest api = adapter.create(HttpRequest.class);
        //Defining the method
        api.getBooks(new Callback<ResponseData>() {
            @Override
            public void success(ResponseData data, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        state = 0;
        //mark1 to 4 are the options available for selection
        mark.setAlpha(0f);
        mark1.setAlpha(0f);
        mark2.setAlpha(0f);
        mark4.setAlpha(0f);
        mark3.setAlpha(0f);

        mark.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)); //centre mark
        mark1.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mark2.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mark3.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mark4.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LatLng centerpos = new LatLng(12.9887026, 80.2293583);
        mMap.addMarker(new MarkerOptions().position(centerpos));
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location currloc = locationManager.getLastKnownLocation(locationProvider);
        if (currloc != null) {
            marklocation(currloc);
        }
    }

    double currlat = 0, currlong = 0;

    @Override
    public void onLocationChanged(Location location) {
        marklocation(location);
    }

    public void marklocation(Location location) {
        mMap.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        currlat = location.getLatitude();
        currlong = location.getLongitude();
        mark = mMap.addMarker(new MarkerOptions().position(currentPosition).snippet("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude()).flat(true).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
        loc1 = new LatLng(currlat + 0.001, currlong );
        mark1 = mMap.addMarker(new MarkerOptions().position(loc1));
        loc2 = new LatLng(currlat - 0.001, currlong );
        mark2 = mMap.addMarker(new MarkerOptions().position(loc2));
        loc3 = new LatLng(currlat , currlong + 0.001);
        mark3 = mMap.addMarker(new MarkerOptions().position(loc3));
        loc4 = new LatLng(currlat , currlong - 0.001);
        mark4 = mMap.addMarker(new MarkerOptions().position(loc4));
    }



    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
//    @Override
    public boolean onMarkerClick(Marker marker){

        if(state == 0) { //when no marker selected
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                beta_val=1;
                marker.setAlpha((float)beta_val);
                state = 1;
            }
        else if(state == 1) //one marker selected
        {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                beta_val=2;
                marker.setAlpha((float)beta_val);
                state = 2;
        }
        else { //reset
            state = 0;
            mark1.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mark2.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mark3.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mark4.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));        }
        return true;
    }

}

