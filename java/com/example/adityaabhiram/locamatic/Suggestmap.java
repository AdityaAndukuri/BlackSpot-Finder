package com.example.adityaabhiram.locamatic;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Suggestmap extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        android.location.LocationListener  {

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth firebaseAuth;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 1000; //2 secs
    private static int FAST_INTERVAL = 1000; //1 secs
    private static int DISPLACEMENT = 10;
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
    private ImageButton imageButton;
    public int check = 0;
    public Location start_location, end_location;
    public long start_time;
    public double diff_time;
    static double distance=0;
    public TextToSpeech myTTS;
    Button button;
    @Override

    public void onDestroy() {
        if(myTTS != null)
        {
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
        if(mGoogleApiClient!= null && mGoogleApiClient.isConnected()) {
            //  mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }

    }

    public Suggestmap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggestmap, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.Add_sugg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mLastLocation != null))

                {  Suggest_details suggest_details = new Suggest_details();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", mLastLocation.getLatitude());
                    bundle.putDouble("longitude", mLastLocation.getLongitude());
                    if (bundle != null) {
                        suggest_details.setArguments(bundle);
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.MainLayout, suggest_details).commit();
                    } else {
                        Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "No location found!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        address = (TextView) view.findViewById(R.id.address_sugg);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        ref = FirebaseDatabase.getInstance().getReference("Mylocation");
        geoFire = new GeoFire(ref);
     /*   click = (Button) view.findViewById(R.id.Addl4);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Intent in = new Intent(getContext(),List_tabbed.class);
                in.putExtra("type","AudioZones");
                startActivity(in);*//*

            }
        });*/
        myTTS = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status != TextToSpeech.ERROR) {
                    myTTS.setLanguage(Locale.ENGLISH);

                }
            }});
        speakWords("GPS is necessary for this app to run");
        setUpLocation();
        initmap();



    }
    public void initmap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_sugg);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }
    public void speakWords(String speech) {
        Toast.makeText(getContext(),speech,Toast.LENGTH_SHORT).show();
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setTrafficEnabled(true);
        displayLocation();
        startLocationUpdates();

    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                displayLocation();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        initmap();
                        displayLocation();
                    }
                }
                break;

        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                //.enableAutoManage(getActivity(),this)
                .build();
        mGoogleApiClient.connect();

    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            //request runtime permission
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }
        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                initmap();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if(getActivity()!=null&&ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
               // textView.setText("Speed : "+mLastLocation.getSpeed()*18/5);
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
            if(Mycurrent!=null)
            {
                Mycurrent.remove();//remove old marker
            }
            Mycurrent=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you").icon(BitmapDescriptorFactory.fromResource(R.drawable.red)));
            //move camera to this position
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),20.0f));

            // update to firebase
            geoFire.setLocation("you", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //Add marker
                }
            });


        }
        else
        {
            Log.d("ADITYA","Cannot get your location");
        }
    }
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FAST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if(resultCode!= ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode,getActivity(),PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(getContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient!= null && mGoogleApiClient.isConnected()) {
            //   mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }

    }


}
