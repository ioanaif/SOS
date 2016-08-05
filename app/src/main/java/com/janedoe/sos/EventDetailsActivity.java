package com.janedoe.sos;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/** Assumptions:
 * everything in database can be null except the location. user cannot put event with null location
 * into database. the popup with message is shown.
 */

public class EventDetailsActivity extends AppCompatActivity {

    private Button sendButton;
    private Button photoButton;
    private EditText editText;

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private StorageReference storage = FirebaseStorage.getInstance().getReference();

    private Uri photoUri = Uri.parse("");
    private byte[] photo = new byte[2];
    DatabaseReference allevents = database.child("events");//root

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        sendButton = (Button) findViewById(R.id.sendbutton);
        photoButton = (Button) findViewById(R.id.photobutton);
        editText = (EditText) findViewById(R.id.edittext);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                String location = getCurrentLocation();
                if (location != null) {
                    addEventToDatabase(getCurrentLocation(), message, photo);
                    ((TextView) findViewById(R.id.message)).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById((R.id.ImageView))).setVisibility(View.GONE);
                    ((EditText) findViewById((R.id.edittext))).setVisibility(View.GONE);
                    ((Button) findViewById((R.id.sendbutton))).setVisibility(View.GONE);
                    ((Button) findViewById((R.id.photobutton))).setVisibility(View.GONE);
                    ((TextView) findViewById((R.id.textview))).setVisibility(View.GONE);
                    Toast.makeText(EventDetailsActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EventDetailsActivity.this, "Message cannot be sent because your location is not found",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        DatabaseReference allevents = database.child("events");//root
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Event e = dataSnapshot.getValue(Event.class);//information of event
                String key = dataSnapshot.getKey();
                if (e.isAccepted.equals("true") && e.userId == FirebaseInstanceId.getInstance().getToken()) {
                    ((TextView) findViewById(R.id.message)).setText("Help is on the way!!!");
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
        };
        allevents.addChildEventListener(eventListener);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }


    /**
     * uploads picture and sets its uri in global variable photoUri.
     * photoUri is not changed when uploading does not succeed.
     *
     * @param photo can be null
     * @param key   unique key for object we want to add to database, used to create unique path for photo
     */
    private void uploadPicture(byte[] photo, String key) {
        StorageReference path = storage.child(key + ".jpg");

        UploadTask uploadTask = path.putBytes(photo);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photoUri = taskSnapshot.getDownloadUrl();
            }
        });
    }


    /**
     * @return concatenation of latitude , longitude
     * never throws an exception.
     * in case of any exceptions returns null string.
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImage = photoUri;
            getContentResolver().notifyChange(selectedImage, null);
            ImageView imageView = (ImageView) findViewById(R.id.ImageView);
            ContentResolver cr = getContentResolver();
            Bitmap bitmap;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                imageView.setImageBitmap(bitmap);
                photo = convertBitmapToByteArray(bitmap);
                Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.e("Camera", e.toString());
            }
        }
    }


    /**
     * @return concatenation of latitude , longitude
     * never throws an exception.
     * in case of any exceptions returns null string.
     */

    private String getCurrentLocation() {
        try {
            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return "" + location.getLatitude() + "," + location.getLongitude();
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private String addEventToDatabase(String location, String message, byte[] photo) {
        DatabaseReference events = database.child("events");
        DatabaseReference newEvent = events.push();

        String time = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss").format(new Date());
        uploadPicture(photo, newEvent.getKey());
        Event event = new Event(location, time, message, photoUri.toString());

        newEvent.setValue(event);
        return newEvent.getKey();
    }

    private void takePicture() {
        Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        photoUri = Uri.fromFile(photo);
        if (camera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camera, 1);
        }
    }
}