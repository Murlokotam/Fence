package com.example.sergey.fence.preferences;

import android.app.Activity;
import android.app.Fragment;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sergey.fence.R;

public class ServerPreference extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_preference);
    }

}
