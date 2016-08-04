package com.janedoe.sos;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;

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
import android.os.Environment;
import android.provider.MediaStore;

public class MainActivity extends AppCompatActivity {

    private Button help_button;
    private ImageView display_pick;
    String mCurrentPhotoPath;

    private void takePicture(){
        Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
        if (camera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camera, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                byte[] photo = convertBitmapToByteArray(bitmap);
                String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                //addEventToDatabase(location, time, message, photo);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private StorageReference storage = FirebaseStorage.getInstance().getReference();
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        help_button = (Button) findViewById(R.id.help_button);
        display_pick = (ImageView) findViewById(R.id.image_view) ;

        help_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
     * @param photo
     * @param key unique key for object we want to add to database, used to create unique path for photo
     */
    private void uploadPicture(byte[] photo, String key) {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android);
        //byte[] photo = convertBitmaptoByteArray(bitmap);

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


}
