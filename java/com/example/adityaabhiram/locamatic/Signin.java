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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signin extends AppCompatActivity {
    private Button signinBut;
    private EditText mailsiginin;
    private EditText passsignin;
    private TextView signuplink;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    public static Activity signinActivity;
    String user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signinBut=(Button)findViewById(R.id.signinbutton);
        mailsiginin=(EditText)findViewById(R.id.signinmail);
        passsignin=(EditText)findViewById(R.id.signinpassword);
        signuplink=(TextView)findViewById(R.id.signuplink);
        signinActivity = this;
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            //start profile activity here
            //startActivity(this,getApplicationContext());
            user_type = getIntent().getStringExtra("selectkey");
            Intent in;
            switch(user_type){
                case "ministry":
                   in = new Intent(this, validate.class);
                    startActivity(in);
                   break;
                case "safety":
                    in = new Intent(this, validate.class);
                    startActivity(in);
                    break;
                case "user":
                    in = new Intent(this, Signup.class);
                    startActivity(in);
                    break;
            }

            finish();

        }
        signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_sign_up();
            }
        });
        signinBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSignin();
            }
        });

    }
    private void open_sign_up()
    {   finish();
        user_type = getIntent().getStringExtra("selectkey");

        Intent in;
        switch(user_type){
            case "ministry":
                in = new Intent(this, validate.class).putExtra("selectkey",user_type);
                startActivity(in);
                break;
            case "safety":
                in = new Intent(this, validate.class).putExtra("selectkey",user_type);
                startActivity(in);
                break;
            case "user":
                in = new Intent(this, Signup.class).putExtra("selectedkey",user_type);
                startActivity(in);
                break;
        }


    }
    private void userSignin()
    {
        String email = mailsiginin.getText().toString().trim();
        String password = passsignin.getText().toString().trim();
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
        progressDialog.setMessage("Signing-in!!!");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    //finishing one activity from another activity
                    //  Signup.signupActivity.finish();
                    Toast.makeText(Signin.this, "Signed in - sucessfully :) ", Toast.LENGTH_SHORT).show();
                    String user_type = getIntent().getStringExtra("selectkey");
                    Intent in;
                    switch(user_type){
                        case "ministry":
                            in = new Intent(Signin.this, HomeActivity.class);
                            startActivity(in);
                            break;
                        case "safety":
                            in = new Intent(Signin.this, Safety.class);
                            startActivity(in);
                            break;
                        case "user":
                            in = new Intent(Signin.this, Users_activity.class);
                            startActivity(in);
                            break;
                    }

                    finish();


                }
                else
                {
                    Toast.makeText(Signin.this,"Failed to sign-in :( .. try again",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
