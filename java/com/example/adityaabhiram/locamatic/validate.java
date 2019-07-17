package com.example.adityaabhiram.locamatic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class validate extends AppCompatActivity {
    TextView txt;

    EditText id;
    Button login;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userNameRef;
    boolean flag = false;
    String user_type="";
    String ids="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        txt=findViewById(R.id.textView2);
        id = findViewById(R.id.editText);
        login = findViewById(R.id.button);
        user_type = getIntent().getStringExtra("selectkey");
        Toast.makeText(validate.this,user_type,Toast.LENGTH_LONG).show();
        Intent in;
        if(user_type!=null)
        switch(user_type){
            case "ministry":
                ids="Ministry_ids";
                id.setHint("Enter Ministry id");
                txt.setText("Ministry Id:");
                break;
            case "safety":
                ids="emp_ids";
                id.setHint("Enter Employee id");
                txt.setText("Employee Id:");
                break;
            default:
                break;

        }
        userNameRef = FirebaseDatabase.getInstance().getReference(ids);
        Toast.makeText(this, ""+userNameRef,Toast.LENGTH_LONG).show();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               userNameRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       Toast.makeText(getApplicationContext(),"button",Toast.LENGTH_LONG).show();
                       for (DataSnapshot x : dataSnapshot.getChildren()) {
                           System.out.println(x);
                           if (x.getValue(String.class).equals(id.getText().toString())) {
                               System.out.println("hello");
                               flag = true;
                               // Start NewActivity.class
                                /*Intent myIntent = new Intent(validate.this,
                                        Signup.class);
                                startActivity(myIntent);*/
                               Intent myintent=new Intent(validate.this, Signup.class).putExtra("selectedkey", user_type);
                               startActivity(myintent);
                           }
                       }
                       if (!flag) {
                           Toast.makeText(validate.this, "invalid id please register", Toast.LENGTH_SHORT).show();
                           //System.out.println("toast");
                            /*Intent myIntent = new Intent(MainActivity.this,
                                    RegistrationActivity.class);
                            startActivity(myIntent);*/
                       }


                   }


                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"nothing",Toast.LENGTH_LONG).show();
                   }
               });
            }
        });

    }

}
