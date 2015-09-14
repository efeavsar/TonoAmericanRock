package com.lonict.android.tonoamericanrock;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;

public class SleeperDialog extends MainActivity {
    private Context context;
    private RelativeLayout mainLayout;
    private boolean show=false;
    final Intent mAlarmIntent ;
    private android.app.Dialog progress_dialog;

    public SleeperDialog(Context context, RelativeLayout mainLayout) {
        this.context = context;
        this.mainLayout = mainLayout;
        mAlarmIntent = new Intent (context, AlarmReceiver.class);
    }

    public SleeperDialog(Context context) {
        this.context = context;
        this.mainLayout = (RelativeLayout)((MainActivity)context).findViewById(R.id.MainLayout);
        mAlarmIntent = new Intent (context, AlarmReceiver.class);
    }

    public void showDialog(){
        if(!show){
           show=true;
            final android.app.Dialog dialog = new android.app.Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.sleeper);
            dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.6), (int) (mainLayout.getHeight() * 0.75));
            dialog.getWindow().setLayout((int) (mainLayout.getWidth()* 0.6 ), (int) (mainLayout.getHeight() * 0.75));
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(true);
            Button sleep_btn = (Button) dialog.getWindow().findViewById(R.id.sleep_button);
            if (((MainActivity)context).getSleepminute()!=0)
            {
                switch (((MainActivity)context).getSleepminute() ) {
                    case 5:
                        ((RadioButton) dialog.getWindow().findViewById(R.id.radioButton_15)).setChecked(true);
                        break;
                    case 15:
                        ((RadioButton) dialog.getWindow().findViewById(R.id.radioButton_30)).setChecked(true);
                        break;
                    case 30:
                        ((RadioButton) dialog.getWindow().findViewById(R.id.radioButton_45)).setChecked(true);
                        break;
                    case 60:
                        ((RadioButton) dialog.getWindow().findViewById(R.id.radioButton_60)).setChecked(true);
                        break;
                }
            }
            sleep_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((RadioButton)dialog.getWindow().findViewById(R.id.radioButton_15)).isChecked())
                    {
                        setAlarm(5);
                        Toast.makeText(context, "Radio will be closed in 5 minutes", Toast.LENGTH_SHORT).show();
                    }else if (((RadioButton)dialog.getWindow().findViewById(R.id.radioButton_30)).isChecked())
                    {
                        setAlarm(15);
                        Toast.makeText(context, "Radio will be closed in 15 minutes", Toast.LENGTH_SHORT).show();
                    }
                    else if (((RadioButton)dialog.getWindow().findViewById(R.id.radioButton_45)).isChecked())
                    {
                        setAlarm(30);
                        Toast.makeText(context, "Radio will be closed in 30 minutes", Toast.LENGTH_SHORT).show();
                    }
                    else if (((RadioButton)dialog.getWindow().findViewById(R.id.radioButton_60)).isChecked())
                    {
                        setAlarm(60);
                        Toast.makeText(context, "Radio will be closed in 1 hour", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                    show=false;
                }
            });
            Button cancel_btn = (Button) dialog.getWindow().findViewById(R.id.cancel_button);
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    show=false;
                    cancelAlarm();
                    ((MainActivity)context).setSleepminute(0);
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialog.dismiss();
                    show=false;
                }
            });
            dialog.show();
        }
    }

    public void showNetworkDialog()
    {
        final android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sleeper);
        //mainLayout = (RelativeLayout)((MainActivity)context).findViewById(R.id.MainLayout);
        dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.7), (int) (mainLayout.getHeight() * 0.4));
        dialog.getWindow().setLayout((int) (mainLayout.getWidth()* 0.7 ), (int) (mainLayout.getHeight() * 0.4));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);
        Button sleep_btn = (Button) dialog.getWindow().findViewById(R.id.sleep_button);
        Button cancel_btn = (Button) dialog.getWindow().findViewById(R.id.cancel_button);
        TextView tw = (TextView)dialog.getWindow().findViewById(R.id.textView);
        RadioGroup rg = (RadioGroup)dialog.getWindow().findViewById(R.id.radioGroup);
        sleep_btn.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
        rg.setVisibility(View.GONE);
        tw.setText("Please configure \nyour internet connection");
        tw.setTextAppearance(context, android.R.style.TextAppearance_Small);
        Button close = (Button)dialog.getWindow().findViewById(R.id.close_button);
        close.setVisibility(View.VISIBLE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).clearNotification();
                // Write your code here to execute after dialog closed
                ((MainActivity)context).finishActivity();
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dialog.show();
    }

    public void showStationUnAvaliableDialog()
    {
        final android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sleeper);
        //mainLayout = (RelativeLayout)((MainActivity)context).findViewById(R.id.MainLayout);
        dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.7), (int) (mainLayout.getHeight() * 0.4));
        dialog.getWindow().setLayout((int) (mainLayout.getWidth()* 0.7 ), (int) (mainLayout.getHeight() * 0.4));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);
        Button sleep_btn = (Button) dialog.getWindow().findViewById(R.id.sleep_button);
        Button cancel_btn = (Button) dialog.getWindow().findViewById(R.id.cancel_button);
        TextView tw = (TextView)dialog.getWindow().findViewById(R.id.textView);
        RadioGroup rg = (RadioGroup)dialog.getWindow().findViewById(R.id.radioGroup);
        sleep_btn.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
        rg.setVisibility(View.GONE);
        tw.setText("Station is not available now \nplease try later.");
        tw.setTextAppearance(context, android.R.style.TextAppearance_Small);
        Button close = (Button)dialog.getWindow().findViewById(R.id.close_button);
        close.setVisibility(View.VISIBLE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).clearNotification();
                // Write your code here to execute after dialog closed
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void showProgress()
    {
        progress_dialog = new android.app.Dialog(context);
        progress_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress_dialog.setContentView(R.layout.sleeper);
        mainLayout = (RelativeLayout)((MainActivity)context).findViewById(R.id.MainLayout);
        progress_dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.55), (int) (mainLayout.getHeight() * 0.25));
        progress_dialog.getWindow().setLayout((int) (mainLayout.getWidth()* 0.55 ), (int) (mainLayout.getHeight() * 0.25));
        progress_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progress_dialog.setCanceledOnTouchOutside(true);
        Button sleep_btn = (Button) progress_dialog.getWindow().findViewById(R.id.sleep_button);
        Button cancel_btn = (Button) progress_dialog.getWindow().findViewById(R.id.cancel_button);
        TextView tw = (TextView)progress_dialog.getWindow().findViewById(R.id.textView);
        RadioGroup rg = (RadioGroup)progress_dialog.getWindow().findViewById(R.id.radioGroup);
        sleep_btn.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
        rg.setVisibility(View.GONE);
        tw.setText("Please wait...");
        tw.setTextAppearance(context, android.R.style.TextAppearance_Small);
        Button close = (Button)progress_dialog.getWindow().findViewById(R.id.close_button);
        close.setVisibility(View.GONE);
        progress_dialog.show();
    }

    public void dismissProgress()
    {
        this.progress_dialog.dismiss();
    }

    public void setAlarm(int minute)
    {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Alarm Usage")
                .setAction("Alarm Scheduled")
                .setLabel(minute+" minutes")
                .build());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, minute);
        ((MainActivity)context).setSleepminute(minute);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        // schedule for every 30 seconds
        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
       // registerReceiver( mAlarmReceiver,null);
    }
    public void cancelAlarm()
    {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Alarm Usage")
                .setAction("Alarm Canceled")
                .setLabel("true")
                .build());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        // schedule for every 30 seconds
        alarm.cancel(pendingIntent);
        Toast.makeText(context, "Sleep has been cancelled", Toast.LENGTH_SHORT).show();
    }

    public Intent getmAlarmIntent()
    {
        return mAlarmIntent;
    }

}
