package com.aksantara.safasindofm.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StreamingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("exit")) {
            Intent intent1 = new Intent();
            intent1.setAction("exit");
            intent1.putExtra("stopAction",true);
            context.sendBroadcast(intent1);
            Toast.makeText(context, " " + intent.getAction(), Toast.LENGTH_SHORT).show();
        }

    }
}
