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
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.Metadata;

public class StreamingService extends Service {

    //phone
    private boolean isPausedCall = false;

    //player
    // Update sultannamja
    SimpleExoPlayer simpleExoPlayer;

    private final String url = "http://radio.safasindo.com:7044/;stream.pls";
    private final String name = "RADIO SAFASINDO 98.2 FM";

    @Override
    public void onCreate() {

        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();

        if (!simpleExoPlayer.isPlaying()) {
            playMedia();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initIfPhoneCall();
        showNotif(name);

        if(intent.getAction() != null && intent.getAction().equals("exit")) {
            stopSelf();
        } else if (intent.getAction() != null && intent.getAction().equals("playpause")) {
            if (simpleExoPlayer.isPlaying()) {
                simpleExoPlayer.pause();
            } else {
                playMedia();
            }
        }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            if (simpleExoPlayer.isPlaying()) {
                simpleExoPlayer.stop();
            }
            simpleExoPlayer.release();
        }
        hideNotif();
    }

    private void playMedia() {
        if (!simpleExoPlayer.isPlaying()) {
            MediaItem mediaItem = MediaItem.fromUri(url);
            simpleExoPlayer.setMediaItem(mediaItem);
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.prepare();
            simpleExoPlayer.play();
        }
    }

    private void stopMedia() {
        if (simpleExoPlayer.isPlaying()) {
            simpleExoPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (simpleExoPlayer.isPlaying()) {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.pause();
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
                        if (simpleExoPlayer != null) {
                            pauseMedia();
                            isPausedCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (simpleExoPlayer != null) {
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

        Intent stopIntent = new Intent(this, StreamingService.class);
        stopIntent.setAction("exit");
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 12345, stopIntent, 0);

        Intent playIntent = new Intent(this, StreamingService.class);
        playIntent.setAction("playpause");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 12345, playIntent, 0);

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

    /*@Override
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

    }*/


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

