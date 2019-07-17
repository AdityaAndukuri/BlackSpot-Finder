package com.example.adityaabhiram.locamatic;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpotAdapter extends ArrayAdapter<Zones> {

    private Activity context;
    DatabaseReference databaseReference;
    DataSnapshot dataSnapshot;
    ImageView image;
    TextView date,time,latitude,longitude,desc;
    private List<Zones> zonesList;
    public SpotAdapter(Activity context,List<Zones> zonesList)
    {   super(context,R.layout.fragment_spot_adapter,zonesList);
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
        Zones zone = zonesList.get(position);
        textViewname.setText("Date : "+zone.getDate());
        textViewlatitude.setText("Time : "+zone.getTime());
        textViewlongitude.setText("Latitiude : "+zone.getLatitude());
        ImageView imageView = (ImageView)listviewzone.findViewById(R.id.disp);
        // textViewaltitude.setText("Altitude: "+zone.getAltitude());
        textlon.setText("Longitude : "+zone.getLongitutde());

        return listviewzone;
    }
}
