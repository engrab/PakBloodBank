package com.pakbloodbank.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.pakbloodbank.R;
import com.pakbloodbank.adapters.PlaceArrayAdapter;
import com.pakbloodbank.utils.Constant;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.PlaceBuffer;
//import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;


//import com.otacodes.goestateapp.Constants.C1426Constants;
//import com.otacodes.goestateapp.Item.PlaceAutoCompleteItem;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class PicklocationActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        OnMapReadyCallback
//        ,
//        ConnectionCallbacks,
//        OnConnectionFailedListener
{
    public static final String FORM_VIEW_INDICATOR = "FormToFill";
    public static final String LOCATION_LATLNG = "LocationLatLng";
    public static final String LOCATION_NAME = "LocationName";
    public static final int LOCATION_PICKER_ID = 78;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    AutoCompleteTextView autoCompleteTextView;
    //    ImageView backbutton;
    TextView currentAddress;
    private int formToFill;
    public GoogleMap gMap;
    //    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    public PlaceArrayAdapter mAdapter;
    //    public PlaceAutoCompleteAdapter mAdapter;
    Button selectLocation;
    PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//    }
//
//    public void onConnectionSuspended(int i) {
//    }
//
//    public void onStart() {
//        super.onStart();
//        this.googleApiClient.connect();
//    }
//
//    public void onStop() {
//        super.onStop();
//        this.googleApiClient.disconnect();
//    }

    private void setUpSupportToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.pick_location);

    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_picklocation);

        setUpSupportToolbar();

        this.autoCompleteTextView = findViewById(R.id.locationPicker_autoCompleteText);
        this.currentAddress = findViewById(R.id.locationPicker_currentAddress);
        this.selectLocation = findViewById(R.id.locationPicker_destinationButton);
//        this.backbutton = findViewById(R.id.back_btn);


        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        placesClient = Places.createClient(this);


//        setupGoogleApiClient();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.locationPicker_maps)).getMapAsync(this);
        setupAutocompleteTextView();
        this.formToFill = getIntent().getIntExtra(FORM_VIEW_INDICATOR, -1);
        this.selectLocation.setOnClickListener(view -> selectLocation());
//        this.backbutton.setOnClickListener(view -> finish());
    }

    public void selectLocation() {
        LatLng latLng = this.gMap.getCameraPosition().target;
        String charSequence = this.currentAddress.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(FORM_VIEW_INDICATOR, this.formToFill);
        intent.putExtra(LOCATION_NAME, charSequence);
        intent.putExtra(LOCATION_LATLNG, latLng);
        setResult(-1, intent);
        finish();
    }

//    private void setupGoogleApiClient() {
//        if (this.googleApiClient == null) {
//            this.googleApiClient = new Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).addApi(Places.GEO_DATA_API).build();
//        }
//    }

    private void setupAutocompleteTextView() {
        this.mAdapter = new PlaceArrayAdapter(this, Constant.RECT_BOUNDS);
        this.autoCompleteTextView.setAdapter(this.mAdapter);
        this.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        this.autoCompleteTextView.setOnItemClickListener((adapterView, view, i, j) -> {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 2);

            this.autoCompleteTextView.setText(mAdapter.getItem(i).getPrimaryText(null).toString());


            getLocationFromPlaceId(
                    mAdapter.getItem(i).getPlaceId(),
                    placeBuffer -> {
                        if (placeBuffer.getPlace() != null) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(placeBuffer.getPlace().getLatLng()));


                        }
                    });


        });
    }

    public void getLocationFromPlaceId(String str, OnSuccessListener<FetchPlaceResponse> resultCallBack) {
//        Places.getPlaceById(this.googleApiClient, str).setResultCallback(resultCallback);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        FetchPlaceRequest request = FetchPlaceRequest.builder(str, placeFields)
                .build();

        placesClient.fetchPlace(request)
                .addOnSuccessListener(resultCallBack)
                .addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e("asdfg", "Place not found: " + exception.getMessage());
                    }
                });

    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_PERMISSION_LOCATION);
            return;
        }
//        this.lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            lastKnownLocation = location;
                            moveToMyLocation();
                        }
                    }
                });


        this.gMap.setMyLocationEnabled(true);
//        moveToMyLocation();
    }

    public void moveToMyLocation() {
        if (this.lastKnownLocation != null) {
            this.gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude()), 15.0f));
            this.gMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        this.gMap.getUiSettings().setMyLocationButtonEnabled(true);
        updateLastLocation();
        setupMapOnCameraChange();
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == REQUEST_PERMISSION_LOCATION && iArr.length > 0 && iArr[0] == 0) {
            updateLastLocation();
        }
    }

    private void setupMapOnCameraChange() {
        this.gMap.setOnCameraIdleListener(() -> fillAddress(currentAddress, gMap.getCameraPosition().target));
    }

    public void fillAddress(final TextView textView, final LatLng latLng) {
        new Thread(() -> {
            try {
                final List fromLocation = new Geocoder(PicklocationActivity.this, Locale.getDefault()).getFromLocation(latLng.latitude, latLng.longitude, 1);
                runOnUiThread(() -> {
                    if (fromLocation.isEmpty()) {
                        textView.setText("not Available");
                    } else if (fromLocation.size() > 0) {
                        textView.setText(((Address) fromLocation.get(0)).getAddressLine(0));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

//    public void onConnected(@Nullable Bundle bundle) {
//        updateLastLocation();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == android.R.id.home) {
            onBackPressed();
        }

    }
}
