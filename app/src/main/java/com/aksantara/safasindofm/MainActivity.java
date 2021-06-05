package com.aksantara.safasindofm;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aksantara.safasindofm.Service.NetworkUtils;
import com.aksantara.safasindofm.Service.StreamingService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends Activity {

    private static ImageView imgRotation;
    private static ImageView btnPlayPause;
    private static String statusPlay = "pause";
    private ImageView btnShare;

    private String url = "http://radio.safasindo.com:7044/;stream.pls";
    private String name = "RADIO SAFASINDO 98.2 FM";

    static RotateAnimation rotate;

    private static SharedPreferences sharedPref;
    Context context;

    Snackbar snackbar;
    Button btn_on_koneksi;
    View custom_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        sharedPref = context.getSharedPreferences("safasindo", Context.MODE_PRIVATE);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnShare = findViewById(R.id.btnShare);
        imgRotation = (ImageView) findViewById(R.id.imgRotation);
        btnShare = findViewById(R.id.btnShare);

        initSnackbar();

        // Cek terdapat Jaringan atau Tidak
        NetworkUtils.checkNetworkInfo(this, new NetworkUtils.OnConnectionStatusChange() {
            @Override
            public void onChange(boolean type) {
                if(type){
                    snackbar.dismiss();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!btnPlayPause.isEnabled()) btnPlayPause.setEnabled(true);
                        }
                    });


                } else {
                    snackbar.show();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (btnPlayPause.isEnabled())btnPlayPause.setEnabled(false);
                        }
                    });
                    if (statusPlay.equals("play")) {
                        callRadio();
                        pauseRadio();
                    }
                }
            }
        });

        initUI();
        bottomNav();

    }

    private void initSnackbar() {
        snackbar = Snackbar.make(findViewById(android.R.id.content), " ", Snackbar.LENGTH_INDEFINITE);
        custom_view = getLayoutInflater().inflate(R.layout.custom_snackbar, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);
        (custom_view.findViewById(R.id.btn_koneksi_on)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for android Q and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent panelIntent = new
                            Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                    startActivityForResult(panelIntent, 0);
                } else {
                    // for previous android version
                    WifiManager wifiManager = (WifiManager)
                            getApplicationContext().getSystemService(WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                }
            }
        });

        snackBarView.addView(custom_view, 0);
    }

    private void initUI() {

        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());


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


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}

