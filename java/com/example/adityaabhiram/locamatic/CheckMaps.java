package com.example.adityaabhiram.locamatic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CheckMaps extends FragmentActivity implements OnMapReadyCallback,Serializable {

    private GoogleMap mMap;
    public Location loc;

    public double start;
    public double diff;
    public float avg=0;
    public static final int PERMISSION_REQUEST_READ_PHONE_STATE = 111;
    public TextView textView4,textView5;
    public MarkerOptions markerOptions;
    public float DEFAULT_ZOOM = 15f;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    public static final String TAG = "MapActivity";
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    //vars
    public Button button7,button8;
    public AlertDialog alertDialog;
    public TextToSpeech myTTS;
    public EditText editText;
    public int MY_DATA_CHECK_CODE = 0;
    public Boolean mLocationPermissionsGranted = false;
    public Button button2;
    public double max_speed = 10000;
    public LocationRequest mLocationRequest;
    public int REQUEST_CHECK_SETTINGS = 1234;
    public LocationCallback mLocationCallback;
    public long num = 0;
    public String REQUESTING_LOCATION_UPDATES_KEY = " Key";
    public static DatabaseReference mdata;
    public double min,max,av;
    public String id = "hello";
    public int zone_rad = 0;
    DatabaseReference ref, ref2;
    GeoFire geoFire;
    Marker Mycurrent;
    Button click;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;
    TextView address;
    List<Address> addresses;
    public static MediaPlayer dangerSound;
    public Location start_location, end_location;
    public long start_time;
    public double diff_time;
    static double distance=0;
    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_maps);
        dangerSound = MediaPlayer.create(this,R.raw.danger_alarm);
        textView = (TextView) findViewById(R.id.LatLong4);
        address = (TextView) findViewById(R.id.address4);
        geocoder = new Geocoder(this, Locale.getDefault());
        ref = FirebaseDatabase.getInstance().getReference("Mylocation");
        ref2 = FirebaseDatabase.getInstance().getReference("Spots");
        geoFire = new GeoFire(ref);
        click = (Button)findViewById(R.id.Addl4);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent in = new Intent(getContext(),List_tabbed.class);
                in.putExtra("type","AudioZones");
                startActivity(in);*/

            }
        });

        getLocationPermission();
        initMap();
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status != TextToSpeech.ERROR) {
                    myTTS.setLanguage(Locale.ENGLISH);
                }
            }
        });
        speakWords("GPS is necessary for this app to run");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }


        // mdata.child(uid).child(Calendar.getInstance().getTime().toString()).setValue(new Vehicle());


        // phoneCallStateListener = new PhoneCallStateListener(MapsActivity.this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        LocationSettingsRequest.Builder builder1 = new LocationSettingsRequest.Builder();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder1.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                createLocationRequest();
            }
        });


        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(CheckMaps.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
        getLocationPermission();
        getDeviceLocation();


        // speakWords("Pass the String here");


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location1 : locationResult.getLocations()) {
                    getDeviceLocation();
                }
            }
        };


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
        // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        //Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setTrafficEnabled(true);           // mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot audiozoneSnapShot: dataSnapshot.getChildren()){
                    SavedZones zone = audiozoneSnapShot.getValue(SavedZones.class);
                    /*if(firebaseAuth.getCurrentUser()==null)
                    {
                        getActivity().finish();
                        startActivity(new Intent(getContext(),Signin.class));
                    }*/
                    setZones(mMap,zone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override

    public void onDestroy() {
        if(myTTS != null)
        {
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
    }

    public void speakWords(String speech) {
        Toast.makeText(CheckMaps.this,speech,Toast.LENGTH_SHORT).show();
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    @Override
    public void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }



    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location mLastLocation = (Location) task.getResult();
                            if(mLastLocation!=null)
                            {   if(start_location==null){
                                start_location = end_location=mLastLocation;
                                start_time=System.currentTimeMillis();

                            }
                            else {
                                end_location = mLastLocation;
                            }
                                diff_time = System.currentTimeMillis() - start_time;
                                if(mLastLocation!=null && start_location!=null) {
                                    //Toast.makeText(MapsActivity.this," sdhjsdjls",Toast.LENGTH_SHORT).show();

               /* double dLat = Math.toRadians(mLastLocation.getLatitude() - location.getLatitude());
                double dLon = Math.toRadians(mLastLocation.getLongitude() - location.getLongitude());
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(location.getLatitude()))
                        * Math.cos(Math.toRadians(mLastLocation.getLatitude())) * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

                double c = 2 * Math.asin(Math.sqrt(a));*/
                                    distance += (start_location.distanceTo(end_location));
                /*long endTime = System.currentTimeMillis();
                long diff_time2 = start_time-endTime;*/
                                    //diff_time2=TimeUnit.MILLISECONDS.toMinutes(diff_time2);
                                    double sec = diff_time/1000.00;
                                    double speed = distance/sec*(18/5);
                                    //  double dis = mLastLocation.distanceTo(location);
                                    textView.setText("Speed " + new DecimalFormat("#.##").format(speed) + " Km/hr"+distance+"  "+sec);
                                    start_location = end_location;
                                }

                                final double latitude = mLastLocation.getLatitude();
                                final double longitude = mLastLocation.getLongitude();
                                final double altitude = mLastLocation.getAltitude();
                                //  textView.setText(String.format("your location is %f / %f ",latitude,longitude));
                                Log.d("ADITYA",String.format("your location is %f / %f ",latitude,longitude));
                                String Fulladdress="";
                                try
                                {
                                    addresses = geocoder.getFromLocation(latitude,longitude,1);
                                    Fulladdress = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
                                    address.setText(Fulladdress);
                                }
                                catch(IOException i)
                                {

                                }
                                // update to firebase
                                geoFire.setLocation("you", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                        //Add marker
                                        if(Mycurrent!=null)
                                        {
                                            Mycurrent.remove();//remove old marker
                                        }
                                        Mycurrent=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you").icon(BitmapDescriptorFactory.fromResource(R.drawable.red)));
                                        //move camera to this position
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),20.0f));
                                    }
                                });


                            }
                            else
                            {
                                textView.setText("cannot get your location");
                                Log.d("ADITYA","Cannot get your location");
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(CheckMaps.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mLocationPermissionsGranted);
        // ...
        super.onSaveInstanceState(outState);
    }

    public void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
                break;
            case PERMISSION_REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    public void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map4);
        mapFragment.getMapAsync(CheckMaps.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocationPermissionsGranted) {
            startLocationUpdates();
        }
    }

    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null );
    }
    public void setZones(GoogleMap mMap,final SavedZones zone)
    {   Circle mapCircle;
        final LatLng silent_zone = new LatLng(zone.latitude,zone.longitude);
       /* if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),Signin.class));
        }*/
        mapCircle=mMap.addCircle(new CircleOptions()
                .center(silent_zone)
                .radius(zone.getRadius())
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f));
        if(mapCircle!=null){
            mapCircle.remove();

        }
       /* if(zone.getOnoff()==1)
            mapCircle=mMap.addCircle(new CircleOptions()
                    .center(silent_zone)
                    .radius(zone.getRadius())
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
                    .strokeWidth(5.0f));
        else
        {*/
        mapCircle=mMap.addCircle(new CircleOptions()
                .center(silent_zone)
                .radius(zone.getRadius())
                .strokeColor(Color.RED)
                .fillColor(0x22FF3333)
                .strokeWidth(5.0f));
        // }
        //  final int onoff = zone.getOnoff();
        //Add GeoQuery here
        // 0.01f = 10 meters
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(silent_zone.latitude,silent_zone.longitude),((float)zone.getRadius())/1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //   Toast.makeText(getApplicationContext(),""+mLastLocation.getAltitude(),Toast.LENGTH_SHORT).show();
                //  if(mLastLocation.getAltitude() == zone.getAltitude()) {
                // textView.setText(String.format("%s entered silent zone: "+zone.getName(), key));
                dangerSound.start();
            /*    myaudio = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                int currentRingerMode = myaudio.getRingerMode();
                if (onoff == 1) {

                    if (currentRingerMode != AudioManager.RINGER_MODE_VIBRATE) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        Toast.makeText(getContext(), " vibrate", Toast.LENGTH_SHORT).show();
                    }
                }
                if (onoff == 0) {
                    if (currentRingerMode != AudioManager.RINGER_MODE_NORMAL) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        Toast.makeText(getContext(), "normal", Toast.LENGTH_SHORT).show();
                    }
                }
                // } */
            }

            @Override
            public void onKeyExited(String key) {
                // sendNotification("Aditya",String.format("%s exited the silent zone",key));
                // Toast.makeText(getApplicationContext(),""+mLastLocation.getAltitude(),Toast.LENGTH_SHORT).show();
                // if(mLastLocation.getAltitude()==zone.getAltitude()) {
                //  textView.setText(String.format("%s exited silent zone: "+zone.getName(), key));
                dangerSound.stop();
             /*   myaudio = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                int currentRingerMode = myaudio.getRingerMode();
                if (currentRingerMode != AudioManager.RINGER_MODE_NORMAL) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    Toast.makeText(getContext(), "normal", Toast.LENGTH_SHORT).show();
                }*/
                //  }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // if(mLastLocation.getAltitude()==zone.getAltitude()) {
                //  textView.setText(String.format("%s moved within the silent zone:"+zone.getName()+ "-> [%f/%f]", key, location.latitude, location.longitude));
                Log.d("MOVE", String.format("%s moved within the silent zone [%f/%f]", key, location.latitude, location.longitude));
                //  }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("ERROR",""+error);
            }
        });

    }

}
