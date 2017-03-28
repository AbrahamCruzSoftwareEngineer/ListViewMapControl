package com.example.excentusmaplistview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private ListView listView;
    private List<String> GeoPlaces;

    private LatLng movingposition;
    private Location mylocation;
    private GoogleMap mMap;
    private Marker marcador;
    private double lat = 0.0;
    private double lng = 0.0;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Primero hacemos el casting del listview
        listView = (ListView) findViewById(R.id.sampleListView);
        mylocation = new Location("");

        GeoPlaces = new ArrayList<String>();
        GeoPlaces.add("San Francisco, California");
        GeoPlaces.add("Dallas, Texas");
        GeoPlaces.add("Atlanta, Georgia");
        GeoPlaces.add("New York, New York");
        GeoPlaces.add("My Location");
        GeoPlaces.add("Dallas, Texas");
        GeoPlaces.add("Atlanta, Georgia");
        GeoPlaces.add("New York, New York");
        GeoPlaces.add("San Francisco, California");
        GeoPlaces.add("My Location");
        GeoPlaces.add("Atlanta, Georgia");
        GeoPlaces.add("New York, New York");

        //vamos a setear el onclick item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                switch(GeoPlaces.get(position)) {
                    case "San Francisco, California":
                        lat =37.774778;
                        lng = -122.419406;
                        break;
                    case "Dallas, Texas":
                        lat = 32.776487;
                        lng = -96.800046;
                        break;
                    case "Atlanta, Georgia":
                        lat = 33.751079;
                        lng = -84.393148;
                        break;
                    case "New York, New York":
                        lat = 40.712598;
                        lng = -74.004133;
                        break;
                    default:
                        miUbicacion();
                }

                movingposition = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(movingposition).title(GeoPlaces.get(position)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(movingposition));
                agregaMarcador(lat, lng);
                Toast.makeText(MainActivity.this, GeoPlaces.get(position), Toast.LENGTH_LONG).show();

            }
        });

        //ESTO ES DONDE SE ENLAZA Y USA EL ADAPTER
        MyAdapterViewHolder myAdapterViewHolder = new MyAdapterViewHolder(this,R.layout.list_item,GeoPlaces);
        listView.setAdapter(myAdapterViewHolder);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        //UiSettings of the map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);


        miUbicacion();

    }

    private void agregaMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 10);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("ME").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        mMap.animateCamera(miUbicacion);
    }

    private void actualizaUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregaMarcador(lat, lng);

            //Actualiza en donde poner el circulo
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(lat, lng))
                    .radius(200)
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor(Color.argb(100, 150,150,150))
            );

        }

    }


    private void miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            checkLocationPermission();
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizaUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locListener);
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizaUbicacion(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };




    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }
                        ,MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    Toast.makeText(this, "Oh Yeah!", Toast.LENGTH_LONG).show();
//                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    actualizaUbicacion(location);
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, (android.location.LocationListener) locListener);


                    mMap.setMyLocationEnabled(true);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}

