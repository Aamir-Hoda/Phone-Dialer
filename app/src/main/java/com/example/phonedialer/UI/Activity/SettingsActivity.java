package com.example.phonedialer.UI.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.phonedialer.R;
import com.example.phonedialer.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private ActivitySettingsBinding activitySettingsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(activitySettingsBinding.getRoot());
        Log.i(TAG, "onCreate: fired!");


    }
}