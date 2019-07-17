package com.example.adityaabhiram.locamatic;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class Showmapsuggestion extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView textView;
    Button button;
    Geocoder geocoder;
    Bundle bundle;
    List<Address> addresses;
    double latitude,longitude;
    public Showmapsuggestion() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
     //   bundle = getArguments();
       // if(bundle!=null) {



            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
          ///  Toast.makeText(getContext(),""+latitude+"\n"+ bundle.getDouble("longitude")+bundle.getString("date"),Toast.LENGTH_LONG).show();
            LatLng mall = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(mall).title("Black Spot"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mall, 20.0f));
        //}
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_showmapsuggestion, container, false);
       // textView = view.findViewById(R.id.address_sugg1);
        button = view.findViewById(R.id.Add_sugg1);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        bundle = getArguments();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_sugg1);
        mapFragment.getMapAsync(this);
        String Fulladdress="";
        try
        {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
           // Toast.makeText(getContext(),""+bundle.getDouble("latitude")+"\n"+bundle.getDouble("longitude")+bundle.getString("date"),Toast.LENGTH_LONG).show();
            //Fulladdress = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
            //textView.setText(Fulladdress);
        }
        catch(IOException i)
        {

        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSpotDetails addspotsFragment = new AddSpotDetails();
                if (bundle != null) {
                    addspotsFragment.setArguments(bundle);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.MainLayout, addspotsFragment).commit();
                } else {
                    Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
