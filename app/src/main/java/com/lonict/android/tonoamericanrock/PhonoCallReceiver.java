package com.lonict.android.tonoamericanrock;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhonoCallReceiver extends BroadcastReceiver {
    public PhonoCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Phone Call when intent =>>>
       /* String message = "Broadcast intent detected "
                + intent.getAction();
        Toast.makeText(context, message,
                Toast.LENGTH_LONG).show();*/
        android.os.Process.killProcess(android.os.Process.myPid());
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
