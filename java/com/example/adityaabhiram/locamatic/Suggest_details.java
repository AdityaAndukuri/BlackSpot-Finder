package com.example.adityaabhiram.locamatic;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.Calendar;

import static java.lang.Math.log;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Suggest_details extends Fragment  implements View.OnClickListener{

    private static final int RESULT_LOAD_IMAGE = 1;

    private TextView uploadtxt;
    private Button add;
    public ImageView picture;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatePicker datePicker;
    private Calendar calendar;
    private static TextView dateView;
    private int year, month, day;
    private TextView time;
    String uri="";
    TextView description;
    String date,timer,desc;
    Double latitude,longitude;
    Button date_add, time_add;
    public Suggest_details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggest_details, container, false);
        progressDialog = new ProgressDialog(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        date_add = view.findViewById(R.id.btnDate);
        time_add = view.findViewById(R.id.btnTime);
        date_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        time_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(getContext(), onTimeSetListener, 17, 30, true);
                timePickerDialog.show();
            }
        });
        Bundle args = getArguments();
        if(args!=null){
            latitude = args.getDouble("latitude");
            longitude = args.getDouble("longitude");
        }
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        uploadtxt = (TextView)view.findViewById(R.id.tvupload);
        add = (Button)view.findViewById(R.id.btnAdd);
        picture = (ImageView)view.findViewById(R.id.ivPic);
        dateView = (TextView)view.findViewById(R.id.tvDate);
        calendar = Calendar.getInstance();
        description=(TextView)view.findViewById(R.id.tvDescription);
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
        time = (TextView)view.findViewById(R.id.tvTime);
        calendar = Calendar.getInstance();

        picture.setOnClickListener(this);


        return view;

    }

    @Override
    public void onClick(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null){

            final Uri selectedimage = data.getData();
            picture.setImageURI(selectedimage);
            progressDialog.setMessage("Uploading....");
            progressDialog.show();
            progressDialog.dismiss();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedimage));
            UploadTask upload = fileReference.putFile(selectedimage);
            upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //final String uri = taskSnapshot.getUploadSessionUri().toString();
                    uri = fileReference.getPath();

                    add.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (validate()) {

                                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = firebaseDatabase.getReference().child("Zones");
                                        Zones z=new Zones(date,timer,latitude,longitude,desc,uri);
                                        myRef.push().setValue(z);
                                        Toast.makeText(getContext(),"Zone added",Toast.LENGTH_SHORT).show();
                                        //Intent i = new Intent(getContext(), Display.class);
                                        //startActivity(i);
                                    }
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    //double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                }
            });
        }
    }

    private boolean validate(){

        Boolean check = false;
        date=dateView.getText().toString().trim();
        timer=time.getText().toString().trim();
        desc=description.getText().toString().trim();


        if(picture.getDrawable()==null || date.isEmpty() || timer.isEmpty() || latitude == null || longitude == null || desc.isEmpty())
        {
            Toast.makeText(getContext(),"Please fill all the details",Toast.LENGTH_SHORT).show();
        }
        else{
            check = true;
        }

        return check;
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void setupId(){



    }
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    private TimePickerDialog.OnTimeSetListener onTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            if((log(hourOfDay)+1==1)&&log(minute)+1==1){
//                System.out.println("here 1");
//                time.setText("0"+hourOfDay+":"+"0"+minute);
//            }
//            else if((log(hourOfDay)+1==1)&&log(minute)+1!=1){
//                System.out.println("here 2");
//                time.setText("0"+hourOfDay+":"+minute);
//            }
//            else if((log(hourOfDay)+1!=1)&&log(minute)+1==1){
//                System.out.println("here 3");
//                time.setText(hourOfDay+":"+"0"+minute);
//            }
//            else{
//                System.out.println("here 4");
//                time.setText(hourOfDay+":"+minute);
//            }
            if(hourOfDay<10 && minute<10){
                time.setText("0"+hourOfDay+":"+"0"+minute);
            }
            else if(hourOfDay<10 && minute>=10){
                time.setText("0"+hourOfDay+":"+minute);
            }
            else if(hourOfDay>=10 && minute<10){
                time.setText(hourOfDay+":"+"0"+minute);
            }
            else{
                time.setText(hourOfDay+":"+minute);
            }
        }
    };
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm+1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            Suggest_details.dateView.setText(month+"/"+day+"/"+year);
        }

    }

}
