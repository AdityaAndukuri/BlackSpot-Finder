package com.example.adityaabhiram.locamatic;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Add_ids extends AppCompatActivity {
     EditText editText;
     DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ids);
        editText=findViewById(R.id.edt_id);
        ref=FirebaseDatabase.getInstance().getReference("emp_ids");
    }

    public void add(View view) {
        String id = editText.getText().toString();
        String key = ref.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(key,id);
        ref.updateChildren(hashMap);


    }
}
