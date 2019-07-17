package com.example.adityaabhiram.locamatic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TempActivity extends AppCompatActivity {
    DatabaseReference databaseReference,databaseReference2;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Spots");
        databaseReference2= FirebaseDatabase.getInstance().getReference().child("SpotsNew");
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                           for(DataSnapshot x:dataSnapshot.getChildren()){
                               SavedZones asd=x.getValue(SavedZones.class);
                               SpotsNew s=new SpotsNew(asd.name,asd.address ," " ," " ," " ," " ,asd.radius ,asd.velocity ,asd.latitude ,asd.longitude ,false ,false);
                               databaseReference2.push().setValue(s);
                           }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        databaseReference= FirebaseDatabase.getInstance().getReference("zones");
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot x:dataSnapshot.getChildren()){
                            AddZones az=x.getValue(AddZones.class);
                            SpotsNew s=new SpotsNew(" "," " ,az.date ,az.time ,az.imageuri ,az.desc,0 ,0 ,Double.parseDouble(az.latitude) ,Double.parseDouble(az.longitude) ,false ,false);
                            databaseReference2.push().setValue(s);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }
}
