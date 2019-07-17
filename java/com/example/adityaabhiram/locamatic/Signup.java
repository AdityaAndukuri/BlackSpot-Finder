package com.example.adityaabhiram.locamatic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    private Button signup;
    private EditText textemail;
    private EditText textpassword;
   // private TextView textsignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    public static Activity signupActivity;
    DatabaseReference ref;
    String user_type;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup=(Button)findViewById(R.id.signupbutton);
        textemail=(EditText)findViewById(R.id.emailid);
        textpassword=(EditText)findViewById(R.id.password);
       // textsignin=(TextView)findViewById(R.id.signinlink);
        signupActivity = this;
        ref = FirebaseDatabase.getInstance().getReference("users");
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        user_type = getIntent().getStringExtra("selectedkey");
        if(firebaseAuth.getCurrentUser()!=null)
        {   finish();
            Intent in;
            switch(user_type){
                case "ministry":
                    in = new Intent(Signup.this, HomeActivity.class);
                    startActivity(in);
                    break;
                case "safety":
                    in = new Intent(Signup.this, Safety.class);
                    startActivity(in);
                    break;
                case "user":
                    in = new Intent(Signup.this, Users_activity.class);
                    startActivity(in);
                    break;
            }
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();
                // textemail.setText("hello");

            }
        });


    }
    private void open_signin()
    {   finish();
        Intent in = new Intent(this,Signin.class);
        startActivity(in);
    }
    private void registerUser()
    {
        String email=textemail.getText().toString().trim();
        String password = textpassword.getText().toString().trim();
        if(TextUtils.isEmpty(email))
        {
            //email is empty
            Toast.makeText(this,"please enter email id",Toast.LENGTH_SHORT).show();
            //stop the function from executing further
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            //password is empty
            Toast.makeText(this,"please enter password",Toast.LENGTH_SHORT).show();
            //stop the function from executing further
            return;
        }
        //valid inputs
        //show process bar
        progressDialog.setMessage("Signing-up!!!");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    //successful sign-up
                    firebaseAuth = FirebaseAuth.getInstance();
                   final FirebaseUser user = firebaseAuth.getCurrentUser();

                    databaseReference=firebaseDatabase.getInstance().getReference("users");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String key = user.getUid();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            UserClass u =new UserClass(user_type,0,0);
                            hashMap.put(key,(Object)u);
                            ref.updateChildren(hashMap);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(Signup.this, "Signed up - sucessfully :) ", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent in;
                    switch (user_type) {
                        case "ministry":
                            in = new Intent(Signup.this, HomeActivity.class);
                            startActivity(in);
                            break;
                        case "safety":
                            in = new Intent(Signup.this, Safety.class);
                            startActivity(in);
                            break;
                        case "user":
                            in = new Intent(Signup.this, Users_activity.class);
                            startActivity(in);
                            break;
                    }
                }
                else
                {
                    Toast.makeText(Signup.this,"Failed to sign-up :( .. try again",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
