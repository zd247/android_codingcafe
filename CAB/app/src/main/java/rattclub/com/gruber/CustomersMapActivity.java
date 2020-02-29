package rattclub.com.gruber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CustomersMapActivity extends FragmentActivity implements OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        ,com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private int locationRequestCode = 1000;

    private FirebaseAuth mAuth;
    private DatabaseReference customersReqRef, driversAvailableRef, driverLocationRef;

    private Button customersLogoutButton, customersSettingsButton, customersCallButton;
    private String customerID;
    private LatLng customerPickUpLocation;
    private Marker driverMarker;

    private Boolean currentLogoutStatus = false;
    private Boolean onStopFlag = false;
    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);
            return;
        }

        InitializeFields();

        customersLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLogoutStatus = true;

                mAuth.signOut();
                sendUserToWelcomeActivity();
            }
        });

        customersCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoFire geoFire = new GeoFire(customersReqRef);
                geoFire.setLocation(customerID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                }); //this will update the database

                customerPickUpLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(customerPickUpLocation).title("Pick Up customer here"));

                customersCallButton.setText("Searching for driver ...");

                getNearByDrivers();
            }
        });
    }

    private void InitializeFields() {
        mAuth = FirebaseAuth.getInstance();
        customersReqRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");
        customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        customersLogoutButton = findViewById(R.id.customers_map_logout_button);
        customersSettingsButton = findViewById(R.id.customers_map_settings_button);
        customersCallButton = findViewById(R.id.customers_map_call_button);
    }

    @Override
    protected void onStart() {
        super.onStart();

        onStopFlag = false;
        currentLogoutStatus = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!currentLogoutStatus){
        }

        onStopFlag = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();

        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);


        //TODO: deprecated, needs to change to FusedLocationProviderClient
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    @Override
    public void onLocationChanged(Location location) {
        if (!currentLogoutStatus && !onStopFlag){
            lastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    private void getNearByDrivers() {
        driversAvailableRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available"); // look for nearby driver when the button is hit, cannot be moved to Initialized
        if (driversAvailableRef != null){
            GeoFire geoFire = new GeoFire(driversAvailableRef);

            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(customerPickUpLocation.latitude, customerPickUpLocation.longitude), radius);
            geoQuery.removeAllListeners(); // prevent future errors.

            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                //when a driver is available, call this.
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (!driverFound){
                        driverFound = true;
                        driverFoundID = key;

                        driversAvailableRef = FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID);
                        if (driversAvailableRef.child("login_as").toString() == "driver"){
                            HashMap driverMap = new HashMap();
                            driverMap.put ("CallingCustomerID", customerID);
                            driversAvailableRef.updateChildren(driverMap);

                            getDriverLocation();
                            customersCallButton.setText("Looking for driver location...");
                        }
                    }
                }

                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {
                    if (!driverFound){
                        radius += 1;
                        getNearByDrivers();
                    }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });

        }else {
            driverFound = false;
            Toast.makeText(this, "There is no nearby driver detected", Toast.LENGTH_SHORT).show();
            getNearByDrivers();
        }

    }

    private void getDriverLocation() {
        // check for new value under this node ...
        driverLocationRef.child(driverFoundID).child("l").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            List<Object> driverLocationMap = (List<Object>) dataSnapshot.getValue();
                            double locationlat = 0;
                            double locationLng = 0;
                            customersCallButton.setText("Driver Found");

                            if (driverLocationMap.get(0) != null) {
                                locationlat = Double.parseDouble(driverLocationMap.get(0).toString());
                            }
                            if (driverLocationMap.get(0) != null) {
                                locationLng = Double.parseDouble(driverLocationMap.get(1).toString());
                            }

                            LatLng driverLatLgn = new LatLng(locationlat, locationLng);

                            if (driverMarker != null) {
                                driverMarker.remove();
                            }

                            Location location1 = new Location("");
                            location1.setLatitude(customerPickUpLocation.latitude);
                            location1.setLongitude(customerPickUpLocation.longitude);

                            Location location2 = new Location("");
                            location2.setLatitude(driverLatLgn.latitude);
                            location2.setLongitude(driverLatLgn.longitude);

                            float distance = location1.distanceTo(location2);
                            customersCallButton.setText("Driver Found in: " + String.valueOf(distance));

                            driverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLgn).title("Your driver"));
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    protected synchronized void buildGoogleApiClient () {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    private void sendUserToWelcomeActivity() {
        Intent intent = new Intent (this, WelcomeActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


}
