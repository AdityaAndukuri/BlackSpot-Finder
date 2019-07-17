package com.example.adityaabhiram.locamatic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash_Activity extends AppCompatActivity {

    private int timer=3;
    private FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    String user_type="users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//no toolbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//make it fullscreen
        setContentView(R.layout.activity_splash_);//setting layout and loads it
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
                                    in = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(in);
                                    break;
                                case "safety":
                                    in = new Intent(getApplicationContext(), Safety.class);
                                    startActivity(in);
                                    break;
                                case "user":
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
        else{
            LogoLauncher l = new LogoLauncher();
            l.start();
        }
       //
        // getSupportActionBar().hide();//hide toolbar

    }

    private class LogoLauncher extends Thread{
        public void run(){
            try{
                sleep(1000*timer);
            }
            catch(InterruptedException  e){
                e.printStackTrace();
            }
            Intent i = new Intent(getApplicationContext(),WelcomeActivity.class);//to move from one ativity to other
            startActivity(i);
            Splash_Activity.this.finish();//press back goes to homepage
        }
    }
}
