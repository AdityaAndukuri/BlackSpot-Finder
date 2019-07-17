package com.example.adityaabhiram.locamatic;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpotShow extends Fragment {
    DatabaseReference databaseReference;
    DataSnapshot dataSnapshot;
    ImageView image;
    TextView date,time,latitude,longitude,desc;
    Button button;
    Bundle bundle,bundle2;

    public SpotShow() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spot_show, container, false);
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        date=(TextView)view.findViewById(R.id.tvDate);
        time=(TextView)view.findViewById(R.id.tvTime);
        latitude=(TextView)view.findViewById(R.id.tvLatitude);
        longitude=(TextView)view.findViewById(R.id.tvLongitude);
        desc=(TextView)view.findViewById(R.id.tvDesc);
        image=(ImageView)view.findViewById(R.id.tvImage);
        button = view.findViewById(R.id.sub);
        String imageuri="", desc_str="",date_str="",time_str="";
        double lat=0,longi=0;
        bundle = getArguments();
        bundle2=new Bundle();
        if(bundle!=null)
        {
            imageuri=bundle.getString("image");
            desc_str = bundle.getString("desc");
            date_str = bundle.getString("date");
            time_str = bundle.getString("time");
            lat = bundle.getDouble("latitude");
            longi = bundle.getDouble("longitude");
            bundle2.putString("image",imageuri);
            bundle2.putString("desc",desc_str);
            bundle2.putString("date",date_str);
            bundle2.putString("time",time_str);
            bundle2.putDouble("latitude",lat);
            bundle2.putDouble("longitude",longi);

        }
        date.setText("Date : "+date_str);
        time.setText("Time : "+time_str);
        latitude.setText("Latitude : "+lat);
        longitude.setText("Longitude : "+longi);
        desc.setText(desc_str);
        storageReference.child(imageuri).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200, 200, true));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //errors handler
            }
        });
          button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Showmapsuggestion sugg = new Showmapsuggestion();
                  sugg.setArguments(bundle);
                  FragmentManager manager = getActivity().getSupportFragmentManager();
                  manager.popBackStack();
                  manager.beginTransaction().replace(R.id.MainLayout,sugg).addToBackStack(null).commit();
              }
          });
        return view;
    }

}
