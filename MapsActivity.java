package com.example.dk.mapsviabt;
/**
 * ------------------------------------------------------------------------------
 * ------------------------------------------------------------------------------
 * The user clicks on markers to select initial and final points
 * First marker touched is the initial one and the second one is final
 * Then the app communicates with the server and draws the best path between them
 * If the user presses back button after selecting two markers everything gets reset
 * If the user presses a third marker, the first two markers get unselected and the third marker
 * becomes the initial point
 * ------------------------------------------------------------------------------
 * IS WORKING-
 * 1)LOCATION
 * 2)MARKERS AND LATLNG ARRAYLIST
 * ------------------------------------------------------------------------------
 * TO BE CHECKED-
 * 1)SERVER COMMUNICATION - CHECK IF POST REQUEST IS DOING THE RIGHT THING
 * 2)GEODESIC TRUE/FALSE
 * ------------------------------------------------------------------------------
 * TO BE ADDED-
 * EXTRA METHODS FOR BETTER PERFORMANCE UNDER CORNER CASES
 * ------------------------------------------------------------------------------
 * ------------------------------------------------------------------------------
 */
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

//Using retrofit 1.9 and not 2.0
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    /**
     * devpos is an ArrayList containing LatLng of all devices
     * devices is an ArrayList of markers at the locations in devpos
     * route is an ArrayList of LatLng containing the points which represent the shortest path
     * between initial and final points
     */
    private ArrayList<LatLng> devpos = new ArrayList();
    private ArrayList<Marker> devices = new ArrayList<Marker>();
    private ArrayList<LatLng> route = new ArrayList();
    private double inlat = 0.0, inlong = 0.0, finlat = 0.0,  finlong = 0.0;
    int state = 0, errorcode=0;
    /*
     *errorcode=2 GET-route failure HUE_YELLOW
     *errorcode=1 GET-markers failure
     */
    List<Double> initialpos = Arrays.asList(inlat,inlong);
    List<Double> finalpos = Arrays.asList(finlat,finlong);
    String ROOT_URL = "http://trafficmonitoring.pythonanywhere.com/";
    PolylineOptions polylineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /**
         * Getting node locations from server
         */
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ROOT_URL).build();
        HttpRequest api = restAdapter.create(HttpRequest.class);
        api.getMarkers(new Callback<ServerData>() {
            @Override
            public void success(ServerData serverData, Response response) {
                int i=0;
                for(List<Double> loc : serverData.getPointList()){
                    devpos.add(i,new LatLng(loc.get(0),loc.get(1)));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                errorcode=1;
            }
        });
    }
    //This method is called in onMarkerClick along with drawroute()
    private void putData(){
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ROOT_URL).build();
        HttpRequest api = restAdapter.create(HttpRequest.class);
        api.getData(initialpos, finalpos, new Callback<ServerData>() {
            @Override
            public void success(ServerData serverData, Response response) {
                int i=0;
                /**
                 * Getting node locations along shortest route
                 */
                for(List<Double> loc: serverData.getPointList()){
                     route.add(i,new LatLng(loc.get(0),loc.get(1)));
                     i++;
                 }


            }

            @Override
            public void failure(RetrofitError error) {

                if(state!=0)
                    for(Marker marker: devices){
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }
                errorcode=2;

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int i = 0;
        /**
         * Putting markers on map
         */
            for (LatLng Location : devpos) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(Location).title("Device" + i));
                devices.add(i, marker);
                i++;
            }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Helps user find out his current location
        googleMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }
    public boolean onMarkerClick(Marker marker){
        LatLng markloc;
        markloc = marker.getPosition();
        if (state < 2) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            if (state == 0) {
                inlat = markloc.latitude;
                inlong = markloc.longitude;
                state = 1;
                marker.setSnippet("Initial");
            } else if (state == 1) {

                finlat = markloc.latitude;
                finlong = markloc.longitude;
                state = 2;
                marker.setSnippet("Destination");
                putData();
                drawRoute();
            }
        else{//make the third marker the initial point
                state=0;
                route.clear();
                for(Marker mark:devices)
                {
                    mark.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mark.setSnippet("");
                }
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                inlat = markloc.latitude;
                inlong = markloc.longitude;
                state = 1;
                marker.setSnippet("Initial");
            }

        }
        return false;
    }
    //Function sets everything to initial state
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(state!=0){
            state=0;
            for (Marker marker: devices){
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                marker.setSnippet("");
        }
            inlat=0.0;
            inlong = 0.0;
            finlat = 0.0;
            finlong = 0.0;
            route.clear();

    }}

    private void drawRoute(){
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5);
//      polylineOptions.geodesic(true);
        polylineOptions.addAll(route);
        mMap.addPolyline(polylineOptions);


    }
}
