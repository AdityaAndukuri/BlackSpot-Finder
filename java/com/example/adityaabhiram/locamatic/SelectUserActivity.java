package com.example.adityaabhiram.locamatic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SelectUserActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    String user_type="users";
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_select_user);
        firebaseAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("users");

        final FirebaseUser user = firebaseAuth.getCurrentUser();
      //  Toast.makeText(this,""+user,Toast.LENGTH_LONG).show();
        if(user!=null){
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot dst: dataSnapshot.getChildren()){

                        if(dst.getKey().equals(user.getUid())){
                            Toast.makeText(getApplicationContext(),""+dst.getKey()+"\n"+user.getUid(),Toast.LENGTH_LONG).show();
                            UserClass obj = dst.getValue(UserClass.class);
                            user_type=obj.type;
                            Intent in;
                             finish();
                            switch(user_type){
                                case "ministry":
                                    finish();
                                    in = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(in);
                                    break;
                                case "safety":
                                    finish();
                                    in = new Intent(getApplicationContext(), Safety.class);
                                    startActivity(in);
                                    break;
                                case "user":
                                    finish();
                                    in = new Intent(getApplicationContext(), Users_activity.class);
                                    startActivity(in);
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            //request runtime permission
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }


    }
    public void ministry(View view) {
        String value="ministry";
        Intent myintent=new Intent(this, Signin.class).putExtra("selectkey", value);
        startActivity(myintent);
    }

    public void safety(View view) {
        String value="safety";
        Intent myintent=new Intent(this, Signin.class).putExtra("selectkey", value);
        startActivity(myintent);
    }

    public void user(View view) {
        String value="user";
        Intent myintent=new Intent(this, Signin.class).putExtra("selectkey", value);
        startActivity(myintent);
    }
}
