package com.example.adityaabhiram.locamatic;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpotMapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        android.location.LocationListener {

    GoogleMap map;
    AutoCompleteTextView spotres;
    Button search;
    private FusedLocationProviderClient mFusedLocationClient;
    boolean mylocperm;
    private Location mylastloc;
    private static final int DEFAULT_ZOOM = 15;
    private LatLng mydefaultlocation = new LatLng(-33.5363, 140.13265321);
    Button button;
    String location = "";
    boolean found;
    LatLng latLng;
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth firebaseAuth;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 2000; //2 secs
    private static int FAST_INTERVAL = 2000; //1 secs
    private static int DISPLACEMENT = 10;
    private static LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
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
    FirebaseUser user;
    Button add;
    boolean current = true;
    LocationManager lm;
    public TextToSpeech myTTS;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    public SpotMapsFragment() {
        // Required empty public constructor
    }
    @Override

    public void onDestroy() {
        if(myTTS != null)
        {
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
        if(mGoogleApiClient!= null && mGoogleApiClient.isConnected()) {
           // mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spot_maps, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_sugg);
        mapFragment.getMapAsync(this);
        setUpLocation();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        spotres = (AutoCompleteTextView) view.findViewById(R.id.spotsearch);
        search = (Button) view.findViewById(R.id.search);
        button = (Button) view.findViewById(R.id.spotsubmit);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        found = false;
        ref = FirebaseDatabase.getInstance().getReference("Mylocation");
        ref2 = FirebaseDatabase.getInstance().getReference("Spots");
        geoFire = new GeoFire(ref);
        add = view.findViewById(R.id.add_location);
        lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        myTTS = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status != TextToSpeech.ERROR) {
                    myTTS.setLanguage(Locale.ENGLISH);
                }
            }});
        speakWords("GPS is necessary for this app to run");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onMapSearch();

                } catch (IndexOutOfBoundsException ae) {
                    Toast.makeText(getActivity().getApplicationContext(), "No results found!!", Toast.LENGTH_SHORT).show();
                    found = false;
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current == false)
                {
                    if (location != "" && found == true) {
                   /* Intent in = new Intent(getActivity(), AddSpotDetails.class);
                    in.putExtra("latitude", latLng.latitude);
                    in.putExtra("longitude", latLng.longitude);
                    startActivity(in);*/
                        AddSpotDetails addspotsFragment = new AddSpotDetails();
                        Bundle bundle = new Bundle();
                        bundle.putDouble("latitude", latLng.latitude);
                        bundle.putDouble("longitude", latLng.longitude);
                        if (bundle != null) {
                            hideSoftKeyboard();
                            addspotsFragment.setArguments(bundle);
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.MainLayout, addspotsFragment).commit();
                        } else {
                            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No results found!!", Toast.LENGTH_SHORT).show();
                    }
            }
            else {
                    if ((mLastLocation != null))

                    {  AddSpotDetails addspotsFragment = new AddSpotDetails();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", mLastLocation.getLatitude());
                    bundle.putDouble("longitude", mLastLocation.getLongitude());
                    if (bundle != null) {
                        hideSoftKeyboard();
                        addspotsFragment.setArguments(bundle);
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.MainLayout, addspotsFragment).commit();
                    } else {
                        Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                        Toast.makeText(getContext(), "No location found!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, LAT_LNG_BOUNDS,null);
        spotres.setAdapter(placeAutocompleteAdapter);

    }
    public void speakWords(String speech) {
        Toast.makeText(getContext(),speech,Toast.LENGTH_SHORT).show();
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void onMapSearch() throws IndexOutOfBoundsException {


        location = spotres.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList != null) {
                Address address = addressList.get(0);

                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                String Fulladdress = address.getAddressLine(0) + ", " + address.getLocality() + ", " + address.getAdminArea();
                map.addMarker(new MarkerOptions().position(latLng).title(Fulladdress));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));
                found = true;
                current = false;
            } else {
                Toast.makeText(getActivity(), "No results found!!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

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
        if (getActivity()!=null&&ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(map!=null) {
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    current = true;
                    return true;
                }
            });
            map.getUiSettings().setAllGesturesEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            map.setTrafficEnabled(true);
            displayLocation();
            startLocationUpdates();
        }

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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

       /* LatLng pp = new LatLng(17.40891,78.45813);
        MarkerOptions options = new MarkerOptions();
        options.position(pp).title("My location");
        map.addMarker(options);
        map.moveCamera(CameraUpdateFactory.newLatLng(pp));*/


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(),"hello",Toast.LENGTH_LONG).show();
                    SpotMapsFragment spotMapsFragment=new SpotMapsFragment();
                    FragmentManager fragmentManager=getFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.MainLayout,spotMapsFragment).addToBackStack(null).commit();
                    fragmentManager.popBackStack();

                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();

                        displayLocation();
                    }
                }
                break;

        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
               // .enableAutoManage(getActivity(),this)
                .build();
        mGoogleApiClient.connect();

    }

    private void setUpLocation() {
        if (getActivity()!=null&&ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request runtime permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }

    }

    private void displayLocation() {
       /*if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }*/
      if (getActivity()!=null&&ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
          boolean isGPS=false;
        if(lm!=null) {
            isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPS) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            } else {
                Toast.makeText(getContext(), "Please Enable GPS", Toast.LENGTH_LONG).show();
            }
        }

       mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null && current==true)
        {
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
               // address.setText(Fulladdress);
            }
            catch(IOException i)
            {

            }
            if(Mycurrent!=null)
            {
                Mycurrent.remove();//remove old marker
            }
            Mycurrent=map.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you").icon(BitmapDescriptorFactory.fromResource(R.drawable.red)));
            //move camera to this position
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),20.0f));


            // update to firebase
           /* geoFire.setLocation("you", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //Add marker
                    if(Mycurrent!=null)
                    {
                        Mycurrent.remove();//remove old marker
                    }
                    Mycurrent=map.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you").icon(BitmapDescriptorFactory.fromResource(R.drawable.red)));
                    //move camera to this position
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),20.0f));
                }
            });
*/

        }
        else
        {
            //textView.setText("cannot get your location");
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
                Toast.makeText(getContext(),"This devide is not supported",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

   /* private void getPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mylocperm = true;
            getLocation();

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            getLocation();

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mylocperm=false;
        switch (requestCode)
        {
            case 1:
            {
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    mylocperm=true;
                    getLocation();

                }
            }
        }

    }
    private void getLocation()
    {
        try
        {
            if(mylocperm==true)
            {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    double latitude=location.getLatitude();
                                    double longitude=location.getLongitude();
                                    map.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Current Location"));
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),20.0f));
                                }
                            }
                        });
            }
        }catch(SecurityException e)
        {
            Log.e("SecurityException",e.getMessage());
        }
    }
*/

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    @Override
    public void onPause() {
        super.onPause();
        lm.removeUpdates(this);
        if(mGoogleApiClient!= null&& mGoogleApiClient.isConnected()) {
           // mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }

    }


}
