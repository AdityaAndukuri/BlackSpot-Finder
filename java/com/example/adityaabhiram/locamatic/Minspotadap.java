package com.example.adityaabhiram.locamatic;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Minspotadap extends ArrayAdapter<AddZones> {

    private Activity context;
    DatabaseReference databaseReference;
    DataSnapshot dataSnapshot;
    ImageView image;
    TextView date,time,latitude,longitude,desc;
    private List<AddZones> zonesList;
    public Minspotadap(Activity context,List<AddZones> zonesList)
    {   super(context,R.layout.fragment_minspotadap,zonesList);
        this.context=context;
        this.zonesList=zonesList;
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final View listviewzone = inflater.inflate(R.layout.fragment_spot_adapter,null,true);
        TextView textViewname = (TextView)listviewzone.findViewById(R.id.name);
        TextView textViewlatitude = (TextView)listviewzone.findViewById(R.id.latitude);
        TextView textViewlongitude = (TextView)listviewzone.findViewById(R.id.longitude);
        TextView textlon = (TextView)listviewzone.findViewById(R.id.longit);
        //  TextView textViewaltitude = (TextView)listviewzone.findViewById(R.id.altitude);
        TextView textViewadd = (TextView)listviewzone.findViewById(R.id.addre);
        textViewadd.setText("");
        AddZones zone = zonesList.get(position);
        textViewname.setText("Date : "+zone.date);
        textViewlatitude.setText("Time : "+zone.time);
        textViewlongitude.setText("Latitiude : "+zone.latitude);
        ImageView imageView = (ImageView)listviewzone.findViewById(R.id.disp);
        // textViewaltitude.setText("Altitude: "+zone.getAltitude());
        textlon.setText("Longitude : "+zone.longitude);

        return listviewzone;
    }
}
