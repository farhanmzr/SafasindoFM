package com.aksantara.safasindofm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aksantara.safasindofm.Service.StreamingService;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class MainActivity extends Activity {

    private ImageView imgRotation, btnPause, btnPlay, btnShare;

    private int hoursValue, minutesValue;
    private MediaPlayer mediaPlayer;
    private String url = "http://radio.safasindo.com:7044/;stream.pls";
    private String name = "RADIO SAFASINDO 98.2 FM";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();

        initUI();
        bottomNav();

    }

    private void initUI() {

        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        btnPause = findViewById(R.id.btnPause);
        btnPlay = findViewById(R.id.btnPlay);
        btnShare = findViewById(R.id.btnShare);
        imgRotation = (ImageView) findViewById(R.id.imgRotation);

        btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogTime();
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRadio();
                imgRotation.startAnimation(rotate);
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgRotation.clearAnimation();
            }
        });

    }

    private void callRadio() {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("name", name);
        Intent serviceOn = new Intent(this, StreamingService.class);
        serviceOn.putExtras(bundle);

        startService(serviceOn);
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }



    private void openDialogTime() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_timer_picker, null);
        Button btnSimpan = view.findViewById(R.id.btnSimpan);
        NumberPicker hoursPicker = view.findViewById(R.id.hoursPicker);
        NumberPicker minutesPicker = view.findViewById(R.id.minutesPicker);

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(12);
        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hoursValue = hoursPicker.getValue();
                Log.e("hoursValue", String.valueOf(hoursValue));
            }
        });

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(60);
        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minutesValue = minutesPicker.getValue();
                Log.e("minutesValue", String.valueOf(minutesValue));
            }
        });

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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

}

