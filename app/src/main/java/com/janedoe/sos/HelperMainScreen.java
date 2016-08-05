package com.janedoe.sos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HelperMainScreen extends AppCompatActivity {

    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private Bitmap bitmap;
    private String geo;


    private String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_main_screen);


        Intent i = getIntent();
        geo = i.getStringExtra("extraGeo");
        String date = i.getStringExtra("extraDate");
        String date_display = date.replaceFirst("_", " at ");
        String massage = i.getStringExtra("extraMessage");
        //String date = "1.1.2016";
        //String massage = "Helpppp me!";
        ((TextView) findViewById(R.id.textInHelperScreen)).setText(massage);
        ((TextView) findViewById(R.id.dateInHelperScreen)).setText(date_display);
        //geo = "37.7749,-122.4194";
        key = i.getStringExtra("extraFileKey");
        downloadImage(key);
    }

    public void map(View v)
    {
        Intent mapIntent = new Intent();
        mapIntent.setAction("android.intent.action.VIEW");
        mapIntent.setData(Uri.parse("geo:"+geo+"?q="+geo));
        startActivity(mapIntent);
    }

    public void acceptClick(View v){
        ((Button) findViewById(R.id.acceptButton1)).setVisibility(View.GONE);
        database.child("events").child(key).child("isAccepted").setValue("true");

    }


    private void downloadImage(String key) {
        StorageReference photo = storage.child(key + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024 * 10;
        photo.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ((ImageView) findViewById(R.id.eventPhoto)).setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });

    }
}
