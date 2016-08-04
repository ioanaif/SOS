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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        help_button = (Button) findViewById(R.id.help_button);

        help_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(camera, 0);
            }

        });
    }
}
