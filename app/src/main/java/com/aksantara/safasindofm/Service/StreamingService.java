package com.aksantara.safasindofm.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class StreamingService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {

    //player
    private MediaPlayer mediaPlayer = new MediaPlayer();

    //phone
    private boolean isPausedCall = false;

    //receiver
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter filter;

    @Override
    public void onCreate() {

        //init receiver
        filter = new IntentFilter();
        filter.addAction("exit");
        filter.addAction("playpause");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("playpause")) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying())
                            pauseMedia();
                        else
                            playMedia();
                    }
                } else if (action.equals("exit")) {
                    stopSelf();
                }
            }
        };

        registerReceiver(broadcastReceiver, filter);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initIfPhoneCall();
        showNotif(intent.getExtras().getString("name"));
        mediaPlayer.reset();
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.setDataSource(String.valueOf(Uri.parse(intent.getExtras().getString("url"))));
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        unregisterReceiver(broadcastReceiver);
        hideNotif();
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(this, "mediaStart", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void initIfPhoneCall() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            isPausedCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mediaPlayer != null) {
                            if (isPausedCall) {
                                isPausedCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void showNotif(String name) {
        String NOTIFICATION_CHANNEL_ID = "channel_radioislami";
        Context context = this.getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Radio Islami Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent stopIntent = new Intent(this, StreamingReceiver.class);
        stopIntent.setAction("exit");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 12345, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, StreamingReceiver.class);
        playIntent.setAction("playpause");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 12345, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_media_play)
                .setTicker("Listening " + name)
                .setOngoing(true)
                .setContentTitle(name)
                .setContentText("By Aksantara Digital")
                .addAction(android.R.drawable.ic_media_play, "PLAY/PAUSE", playPendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "EXIT", stopPendingIntent)
        ;
        startForeground(115, builder.build());
    }

    private void hideNotif() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(115);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

