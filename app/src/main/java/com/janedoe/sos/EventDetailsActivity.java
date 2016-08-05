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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {

        private Button sendButton;
        private Button photoButton;
        private EditText editText;

        private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        private StorageReference storage = FirebaseStorage.getInstance().getReference();

        private Uri photoUri = Uri.parse("");

        private byte[] photo = new byte[2];
        private String time;

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
                    addEventToDatabase(getCurrentLocation(), message, time, photo);

                    AlertDialog.Builder dialog  = new AlertDialog.Builder(EventDetailsActivity.this);
                    dialog.setMessage("Message sent");
                    dialog.setPositiveButton("OK", null);
                    dialog.create().show();

                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            });

            photoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    takePicture();
                }
            });
        }


        private String addEventToDatabase(String location, String time, String message, byte[] photo) {
            DatabaseReference events = database.child("events");
            DatabaseReference newEvent = events.push();

            uploadPicture(photo, newEvent.getKey());
            Event event = new Event(location, time, message, photoUri.toString());

            newEvent.setValue(event);
            return newEvent.getKey();
        }

        /**
         * uploads picture and sets its uri in global variable photoUri;
         * photoUri is not changed when uploading does not succeed
         * @param photo can be null
         * @param key unique key for object we want to add to database, used to create unique path for photo
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


        private byte[] convertBitmapToByteArray(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }

        private void takePicture(){
            Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
            File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
            camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            photoUri = Uri.fromFile(photo);
            if (camera.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(camera, 1);
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode,resultCode,data);
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
                    time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    //addEventToDatabase(location, time, message, photo);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }

            }
        }

        private String getCurrentLocation() {
            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return "" + location.getLatitude() + "," + location.getLongitude();
        }
    }



