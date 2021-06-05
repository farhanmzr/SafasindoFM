package com.aksantara.safasindofm;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aksantara.safasindofm.Service.ConnectivityReceiver;
import com.aksantara.safasindofm.Service.MyApplication;
import com.aksantara.safasindofm.Service.StreamingService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static ImageView imgRotation;
    private static ImageView btnPlayPause;
    private static String statusPlay = "pause";
    private ImageView btnShare;

    private String url = "http://radio.safasindo.com:7044/;stream.pls";
    private String name = "RADIO SAFASINDO 98.2 FM";

    static RotateAnimation rotate;

    private static SharedPreferences sharedPref;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        sharedPref = context.getSharedPreferences("safasindo", Context.MODE_PRIVATE);



        initUI();
        bottomNav();

    }

    private void initUI() {

        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        btnPlayPause = findViewById(R.id.btnPlayPause);

        btnShare = findViewById(R.id.btnShare);
        imgRotation = (ImageView) findViewById(R.id.imgRotation);

        btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (statusPlay.equals("play")) {
                    callRadio();
                    pauseRadio();
                } else if (statusPlay.equals("pause")) {
                    callRadio();
                    playRadio();
                }
            }
        });

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("safasindo", MODE_PRIVATE);

        if (sharedPref.getBoolean("statusPlay", false)) {
            playRadio();
        } else {
            pauseRadio();
        }

    }

    public static void pauseRadio() {
        statusPlay = "pause";

        imgRotation.clearAnimation();
        btnPlayPause.setImageResource(R.drawable.ic_btn_play);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("statusPlay", false);
        editor.apply();
    }

    public static void playRadio() {
        statusPlay = "play";

        imgRotation.startAnimation(rotate);
        btnPlayPause.setImageResource(R.drawable.ic_btn_stop);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("statusPlay", true);
        editor.apply();
    }

    private void callRadio() {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("name", name);
        if (statusPlay.equals("play")) {
            bundle.putString("status", "play");
        } else if (statusPlay.equals("pause")) {
            bundle.putString("status", "pause");
        }
        Intent serviceOn = new Intent(this, StreamingService.class);
        serviceOn.putExtras(bundle);

        startService(serviceOn);
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }

    private void bottomNav() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        // set selected home
        bottomNavigationView.setSelectedItemId(R.id.streaming);

        // item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.streaming:
                        return true;

                    case R.id.sosmed:
                        startActivity(new Intent(getApplicationContext(), SosmedActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message = "Maaf, anda tidak terhubung dengan internet.";
        if (isConnected) {

        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

}

