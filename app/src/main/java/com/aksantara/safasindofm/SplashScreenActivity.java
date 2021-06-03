package com.aksantara.safasindofm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class SplashScreenActivity extends AppCompatActivity {

    private final int waktu_loading = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intro = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intro);
                finish();
                overridePendingTransition(0, 0);
                getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            }
        }, waktu_loading);


    }
}