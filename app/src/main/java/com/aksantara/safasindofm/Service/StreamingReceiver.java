package com.aksantara.safasindofm.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StreamingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String param = intent.getAction();
        if (param.equals("exit")) {
            context.sendBroadcast(new Intent("exit"));
        } else if (param.equals("playpause")) {
            context.sendBroadcast(new Intent("playpause"));
        }
    }
}
