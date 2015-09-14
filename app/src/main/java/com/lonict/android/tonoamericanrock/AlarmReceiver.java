package com.lonict.android.tonoamericanrock;

/**
 * Created by Efe Avsar on 06/05/2015.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            intent.setClassName(context, "com.lonict.android.tonoamericanrock.MainActivity");
           // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("XX_CLOSE_ALARM",true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            context.startActivity(intent);

            // ((MainActivity)context).setSleepminute(0);
            //  android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            //Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}