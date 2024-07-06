package com.example.phonedialer.UI.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.phonedialer.DialerActivity;
import com.example.phonedialer.R;
import com.example.phonedialer.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private ActivitySplashBinding activitySplashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());
        Log.i(TAG, "onCreate: fired!");


        AlphaAnimation phoneFadeInAlphaAnimation = new AlphaAnimation(0, 1);
        phoneFadeInAlphaAnimation.setDuration(3200);
        phoneFadeInAlphaAnimation.setAnimationListener(phoneFadeInAnimationListener);

//        TranslateAnimation translateAnimation = new TranslateAnimation(100, 0, 0, 0);
//        translateAnimation.setDuration(3000);

//        activitySplashBinding.batAppCompatImageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_translate_rotate));
        activitySplashBinding.phoneAppCompatImageView.setAnimation(phoneFadeInAlphaAnimation);

    }

    private final Animation.AnimationListener phoneFadeInAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            Log.i(TAG, "onAnimationStart: fired!");
            activitySplashBinding.batAppCompatImageView.setAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.anim_translate_rotate));
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.i(TAG, "onAnimationEnd: fired!");
            Toast.makeText(SplashActivity.this, "Bat Dialer Launched!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashActivity.this, DialerActivity.class));
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            Log.i(TAG, "onAnimationRepeat: fired!");
        }
    };
}