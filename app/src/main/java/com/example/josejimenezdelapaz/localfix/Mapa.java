package com.example.josejimenezdelapaz.localfix;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Mapa extends FragmentActivity implements GoogleMap.OnMapLongClickListener,GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;


    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 101;

    private int modalidad=-1; // 0-Selección Ubicación,1-Ver MAPA Desperfectos,
    private ArrayList<Desperfecto> listaDesperfectos=new ArrayList<Desperfecto>();
    private String direccion="";
    private Marker marcaNuevoDesperfecto;
    private String lat="";
    private String lon="";
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        modalidad=getIntent().getIntExtra("EXTRA_MODALIDAD",-1);
        Bundle bundleObject=getIntent().getExtras();
        listaDesperfectos=(ArrayList<Desperfecto>) bundleObject.getSerializable("desperfectos");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
         // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    private void MarcarDesperfectos(ArrayList<Desperfecto> l){
        for(int i=0;i<l.size();i++){
            if(l.get(i).getLatitud()!="" && l.get(i).getLongitud()!=""){
                LatLng pos=new LatLng(Double.parseDouble(l.get(i).getLatitud()),
                        Double.parseDouble(l.get(i).getLongitud()));

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(l.get(i).getTitulo()));
                mHashMap.put(marker,i);
            }

        }
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
        getLocationPermission();
        updateLocationUI();

        getDeviceLocation();// si modalidad ==0-> esta en crear desp->cargar desp cercanos en el mapa
        if(modalidad==1){
            //Esta en ver mapa->Cargar todos los desperfectos en el mapa
            MarcarDesperfectos(listaDesperfectos);
        }


        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            //mLastKnownLocation->guarda la posicion actual del dispositivo
                            if(modalidad==0){//en crear desperfecto
                                LatLng actual=new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                SeleccionDespCercanos(actual);
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d("GPS", "Current location is null. Using defaults.");
                            Log.e("GPS", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void SeleccionDespCercanos(LatLng posActual){
        double tasa=0.004;
        double  maxLat=posActual.latitude+tasa;
        double minLat=posActual.latitude-tasa;
        double  maxLon=posActual.longitude+tasa;
        double minLon=posActual.longitude-tasa;
        ArrayList<Desperfecto> l=new ArrayList<>();
        for(int i=0;i<listaDesperfectos.size();i++){
            if(l.get(i).getLatitud()!=null && l.get(i).getLongitud()!=null){
                if(Double.valueOf(listaDesperfectos.get(i).getLatitud())<=maxLat &&
                        Double.valueOf(listaDesperfectos.get(i).getLatitud())>=minLat&&
                        Double.valueOf( listaDesperfectos.get(i).getLongitud())<=maxLon &&
                        Double.valueOf( listaDesperfectos.get(i).getLongitud())>=minLon){
                    l.add(listaDesperfectos.get(i));
                }
            }

        }
        MarcarDesperfectos(l);
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {


        return false;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
       /* if(modalidad==0){ //Si esta en nuevodesperfecto y hace click en la marca donde quiere ubicarlo
            //DevolverDireccion(direccion);
        }else if(modalidad==1){ //Esta viendo los desperfectos, acceder a la info completa:
            //Enviar el desperfecto seleccionado a la vista.
            int posicion=mHashMap.get(marker);
            Intent visualizarDesperfecto = new Intent (Mapa.this, VisualizarDesperfecto.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("desperfecto",listaDesperfectos.get(posicion));
            visualizarDesperfecto.putExtras(bundle);
            // visualizarDesperfecto.putExtra("EXTRA_IMAGENES", listaDesperfectos.get(position).getImagenes());
            startActivity(visualizarDesperfecto);
            //finish();
        }*/
        //Enviar el desperfecto seleccionado a la vista.
        int posicion=mHashMap.get(marker);
        Intent visualizarDesperfecto = new Intent (Mapa.this, VisualizarDesperfecto.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("desperfecto",listaDesperfectos.get(posicion));
        visualizarDesperfecto.putExtras(bundle);
        // visualizarDesperfecto.putExtra("EXTRA_IMAGENES", listaDesperfectos.get(position).getImagenes());
        startActivity(visualizarDesperfecto);
        //finish();

    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        //SELECCIÓN DE UBICACIÓN DEL DESPERFECTO MODALIDAD=0
        if(modalidad==0){
            mMap.clear();
            direccion=CargarDireccionDeCoordenadas(latLng);
            lat=String.valueOf(latLng.latitude);
            lon=String.valueOf(latLng.longitude);
            marcaNuevoDesperfecto=mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(direccion));
            //VISUALIZACION DEL MAPA PARA VER DESPERFECTOS
        }else  if(modalidad==1){
        }
    }
    private void DevolverDireccion(String direccion){
        //Starting the previous Intent
        Intent previousScreen = new Intent(getApplicationContext(), NuevoDesperfecto.class);
        //Sending the data to Activity_A

        previousScreen.putExtra("direccion",direccion);
        previousScreen.putExtra("lat",lat);
        previousScreen.putExtra("lon",lon);
        setResult(1001, previousScreen);
        super.onBackPressed();
       // finish();
    }


    private String CargarDireccionDeCoordenadas(LatLng latLng){
        String direccion="";
        Geocoder gc = new Geocoder(this,Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gc.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addresses != null) {
                direccion= addresses.get(0).getAddressLine(0);
                Toast.makeText(Mapa.this,direccion, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Mapa.this, "No se encontró la calle", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Mapa.this, "Error-No se encontró la calle", Toast.LENGTH_SHORT).show();
        }
        return direccion;
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    @Override
    public void onBackPressed() {
       DevolverDireccion(direccion);
    }

}

