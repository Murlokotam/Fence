package com.example.sergey.fence;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sergey.fence.camera.tech.PictureManager2;
import com.example.sergey.fence.preferences.ServerPreference;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                int permissionCam = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);

                int permissionStorage = ActivityCompat.checkSelfPermission(MainActivity.this,  Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCam != PackageManager.PERMISSION_GRANTED &&permissionStorage != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);

                }
                permissionCam = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                permissionStorage = ActivityCompat.checkSelfPermission(MainActivity.this,  Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCam != PackageManager.PERMISSION_GRANTED &&permissionStorage != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                PictureManager2 manager = new PictureManager2();
                manager.makePictures(MainActivity.this);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ServerPreference.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
