package com.janedoe.sos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.provider.*;
import android.net.Uri;
import java.io.File;
import android.os.Environment;
import android.app.Activity;
import android.graphics.*;
import android.util.Log;
import android.content.pm.*;
import android.content.pm.PackageManager.*;
import android.support.v4.content.*;
import android.support.v4.app.*;

public class MainActivity extends AppCompatActivity {

    private Button help_button;
    private ImageView display_pick;

    private void takePicture(){
        Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(camera, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            display_pick.setImageBitmap(imageBitmap);
        }
    }

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
}
