package com.example.adityaabhiram.locamatic;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Spots_list extends Fragment {
    private FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    ListView listViewspots;
    List<Zones> zonesList;
    String input;


    public Spots_list() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spots_list, container, false);
                firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null)
        {
          /*  finish();
            startActivity(new Intent(get,Signin.class));
            */
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
       // Intent in = getIntent();
        //input = in.getStringExtra("type");
        ref=FirebaseDatabase.getInstance().getReference("Zones");
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        listViewspots=(ListView)view.findViewById(R.id.ListViewSpots);
        zonesList = new ArrayList<>();

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                zonesList.clear();
                for(DataSnapshot audiozoneSnapShot: dataSnapshot.getChildren()){
                    Zones zone = audiozoneSnapShot.getValue(Zones.class);
                    zonesList.add(zone);
                }
                SpotAdapter adapter = new SpotAdapter(getActivity(),zonesList);
                listViewspots.setAdapter(adapter);
                listViewspots.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i,long l) {
                        //Intent in = new Intent(getContext(),ZoneInfo.class);
                        //startActivity(new Intent(getApplicationContext(),ZoneInfo.class));
                       /* in.putExtra("name",zonesList.get(i).name);
                        in.putExtra("latitude",zonesList.get(i).latitude);
                        in.putExtra("longitude",zonesList.get(i).longitude);
                        in.putExtra("onoff",zonesList.get(i).onoff);
                        in.putExtra("radius",zonesList.get(i).radius);
                        in.putExtra("type",input);
                        startActivity(in);
                        */
                        SpotShow spotShow = new SpotShow();
                        Bundle bundle = new Bundle();
                        bundle.putString("image",zonesList.get(i).getImageuri());
                        bundle.putString("desc",zonesList.get(i).getDesc());
                        bundle.putString("date",zonesList.get(i).getDate());
                        bundle.putString("time",zonesList.get(i).getTime());
                        bundle.putDouble("longitude",zonesList.get(i).getLongitutde());
                        bundle.putDouble("latitude",zonesList.get(i).getLatitude());
                        if (bundle != null) {
                            spotShow.setArguments(bundle);
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.MainLayout, spotShow).commit();
                        } else {
                            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                        }



                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
