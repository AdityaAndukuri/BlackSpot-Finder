package com.example.adityaabhiram.locamatic;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
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
public class AddSpotDetails extends Fragment  {

    EditText name_inp;
    EditText rad_inp;
    EditText vel_inp;
    Button button;
    String name;
    int radius;
    int velocity;
    double latitude;
    double longitude;
    Geocoder geocoder;
    List<Address> addresses;
    DatabaseReference databaseReference;
    String address="";

    public AddSpotDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_add_spot_details, container, false);
        Bundle bundle = getArguments();
        name_inp = view.findViewById(R.id.zonename);
        rad_inp =  view.findViewById(R.id.radiustext);
        vel_inp =  view.findViewById(R.id.velocityid);
        button =   view.findViewById(R.id.submitid);
        databaseReference = FirebaseDatabase.getInstance().getReference("Spots");
        if(bundle!=null){
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            geocoder= new Geocoder(getContext(),Locale.getDefault());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = name_inp.getText().toString();
                    radius = Integer.parseInt(rad_inp.getText().toString());
                    velocity = Integer.parseInt(vel_inp.getText().toString());
                    try
                    {

                        addresses = geocoder.getFromLocation(latitude,longitude,1);
                        address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea();
                        //address.setText(Fulladdress);
                    }
                    catch(Exception i)
                    {

                    }
                    final SavedZones zone = new SavedZones(latitude,longitude,name,radius,address,velocity);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(name))
                            {
                                Toast.makeText(getContext(),"Already existing zone name :( try again",Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                //String id=ref2.push().getKey();

                                databaseReference.child(name).setValue(zone, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Toast.makeText(getContext(),"Successfully added zone",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });


        }


        return view;
    }


}
