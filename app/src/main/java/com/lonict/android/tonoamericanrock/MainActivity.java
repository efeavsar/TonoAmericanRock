package com.lonict.android.tonoamericanrock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusShare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AnimationDrawable equalizer  ;
    private AnimationDrawable equalizer_dummy  ;
    private int mId;
    private final BroadcastReceiver mNetworkStatusReceiver = new NetworkStatusReceiver();
    //private final BroadcastReceiver mAlarmReceiver = new AlarmReceiver();
    private int mSleepminute=0 ;
    public static SleeperDialog mCustomDialog ;
    private final static ButtonOnClickListener mListener  = new ButtonOnClickListener();

    private static InterstitialAd interstitial;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    private boolean isExit;
    private Toolbar mToolbar;
    private RadioStation mRadioStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mCustomDialog = new SleeperDialog(this,(RelativeLayout)findViewById(R.id.MainLayout)) ;
        initNetworkStatus();
        initContent();
        initCustomActionBar();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice("C208B2B6A2E04BFDC41B87659ADEC74E")
                            // .addTestDevice("0A3F2A664AA0AD56D388AF1DD436A4CC")
                            .build();
       // AdRequest adRequest = new AdRequest.Builder().build();
        //AdRequest.Builder.addTestDevice("C208B2B6A2E04BFDC41B87659ADEC74E");
        mAdView.loadAd(adRequest);

        //initNetworkStatus();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(mNetworkStatusReceiver, filter);
        //IntentFilter filter_alarm = new IntentFilter();
        //filter_alarm.addAction("");
        //registerReceiver(mAlarmReceiver,filter_alarm);
        createNotification("");
        initAnalytics();
        initInterStitial();
    }

    public void initAnalytics()
    {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800); //will upload data to server every 10 minutes
        tracker = analytics.newTracker(getResources().getString(R.string.admob_analytics_tracking_id)); // Replace with actual tracker/property Id

        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        //tracker.setSampleRate(90);

        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    public void initInterStitial()
    {
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
        requestNewInterstitial();
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //super.onAdClosed();
                if (isExit) {
                    Toast.makeText(getApplicationContext(), "Closing ...", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            clearNotification();
                            finishActivity();
                            // android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    }, 2000);
                } else requestNewInterstitial();
            }
        });
    }

    public void showAds(){
        try
        {
            //requestNewInterstitial();
            if (interstitial.isLoaded()) {
                interstitial.show();
            } else {
                if (isExit)
                {
                    clearNotification();
                    finishActivity();
                    //android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
        catch (Exception e)
        {
            Log.d("ERROR showAds",e.getMessage()+"");
        }
    }

    public void requestNewInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("0A3F2A664AA0AD56D388AF1DD436A4CC")
                .build();
        interstitial.loadAd(adRequest);
    }

    private void initCustomActionBar()
    {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        findViewById(R.id.imageView_power).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExit = true;
                showAds();
            }
        });
    }

    @Override
    public void onDestroy()
    {
        clearNotification();
        unregisterReceiver(mNetworkStatusReceiver);
        //unregisterReceiver(mAlarmReceiver);
        super.onDestroy();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //createNotification();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //network status receiver calls this
        Log.d("XX_CLOSE_ALARM","onResume");
        initNetworkStatus();
        initAlarmStatus();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        Log.d("XX_CLOSE_ALARM", "onRestart");
       // Log.d("","");
        //initAlarmStatus();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("XX_CLOSE_ALARM", "onNewIntent");
        Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.getBoolean("XX_CLOSE_ALARM")) {
                    Log.d("XX_CLOSE_ALARM", "onNewIntent");
                    setIntent(intent);
                }
            }
    }

    public void initAlarmStatus()
    {
        // schedule for every 30 seconds
        Log.d("XX_CLOSE_ALARM", "initAlarmStatus 1");
        Bundle extras = getIntent().getExtras();
        int flags = getIntent().getFlags();
        if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            //is not launch from background .
            if (extras != null) {
                if (extras.getBoolean("XX_CLOSE_ALARM")) {
                    Log.d("XX_CLOSE_ALARM", "initAlarmStatus 2");

                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Alarm Usage")
                            .setAction("App Closed")
                            .setLabel("true")
                            .build());

                    clearNotification();
                    finishActivity();
                    //android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d("XX_CLOSE_ALARM", "onStart");

        analytics.reportActivityStart(this);
        if (isValidAdCountResume())
        {
            showAds();
        }
    }


    public void finishActivity()
    {
        analytics.reportActivityStop(this);
        finish();
        System.exit(0);
    }

    private void initNetworkStatus()
    {
        Log.d("xxInitNetwork", "initNetworkStatus") ;
        if (!isNetworkAvailable())
        {
            mCustomDialog.showNetworkDialog();
            mListener.stopMediaPlayer();
        }
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo()!=null)
        {
            if (!cm.getActiveNetworkInfo().isConnectedOrConnecting())
            {
                return false;
            }
            if (!cm.getActiveNetworkInfo().isAvailable())
            {
                return false;
            }
            if (cm.getActiveNetworkInfo().isFailover())
            {
                return false;
            }
            if (!cm.getActiveNetworkInfo().isConnected())
            {
                return false;
            }

        }else return false;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        if(!isGooglePlayAvailable()) {
            //menu.findItem(R.id.menu_item_plus).setEnabled(false);
            menu.findItem(R.id.menu_item_plus).setVisible(false);
        }

        return true;
    }

    private boolean isGooglePlayAvailable ()
    {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())== ConnectionResult.SUCCESS)
        {
            Log.d("xxPlayservices","exists");
            return true;
        }else
        {
            Log.d("xxPlayervices","not exists");
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_rate:

                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Rate Us Link")
                            .setAction("has clicked")
                            .setLabel("yes")
                            .build());
                    startActivity(goToMarket);

                } catch (ActivityNotFoundException e) {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Rate Us Link")
                            .setAction("Exception: couldn't find")
                            .setLabel("forwarded to playstore manually (downloaded from different market))")
                            .build());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
                return true;
            case R.id.menu_item_share:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Share Link")
                        .setAction("Clicked")
                        .setLabel("")
                        .build());
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Please check : https://play.google.com/store/apps/details?id=com.lonict.android.tonoamericanrock";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try Tono Rock Radio!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.menu_item_sleeper:
                mCustomDialog.showDialog();
                return true;
            case R.id.menu_item_plus:
                PlusShare.Builder builder = new PlusShare.Builder(this);
                // Set call-to-action metadata.
                builder.addCallToAction(
                        "INSTALL_APP", /** call-to-action button label */
                        Uri.parse("http://plus.google.com/pages/create"), /** call-to-action url (for desktop use) */
                        "https://play.google.com/store/apps/details?id=com.lonict.android.tonoamericanrock" /** call to action deep-link ID (for mobile use), 512 characters or fewer */);
                // Set the content url (for desktop use).
                builder.setContentUrl(Uri.parse("http://plus.google.com/pages/create"));
                // Set the target deep-link ID (for mobile use).
                builder.setContentDeepLinkId("https://play.google.com/store/apps/details?id=com.lonict.android.tonoamericanrock",
                        null, null, null);
                // Set the share text.
                builder.setText("Try Tono Rock Radio!");
                startActivityForResult(builder.getIntent(), 0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initContent()
    {
        if (!isNetworkAvailable()) return ;
        mListener.startStreamTitleUpdater();
        LinearLayout linearLayout_main = (LinearLayout)findViewById(R.id.linear_layout_1) ;
            List<String> requiredbucketobjectnames = new ArrayList<String>();
            requiredbucketobjectnames.add("tono_american_rock/radiostations.json") ;

            CloudStorage cloudStorage = new CloudStorage.Builder()
                        .setApplicationName("com.lonict.android.tonoamericanrock")
                        .setBucketName("lonict_android")
                        .setEmailAdress(getResources().getString(R.string.google_storage_email_id))
                        .setKeyStream(getResources().openRawResource(R.raw.key))
                        .setStorePass("notasecret")
                        .setAlias("privatekey")
                        .setKeyPassword("notasecret")
                        .setStorageObjectFiles(requiredbucketobjectnames).build();
            cloudStorage.initBucket();

            for (CloudStorage.DownloadedFiles s : cloudStorage.getStorageFiles())
            {
                Log.d("DownloadedFile", s.getFilename());
                if (s.getFilename().equals("tono_american_rock/radiostations.json"))
                {
                    Log.d("DownloadedFileSize", s.getFile().size()+" bytes");
                    mRadioStation = new RadioStation(s.getFile().toString());
                }
            }

            int i = 0 ;
            for (RadioStationPOJO radiostation : mRadioStation.getRadioStations()) {
                Log.d("getRadioName", radiostation.getRadioName());
                //main container
                LinearLayout linearLayout_child = new LinearLayout(this) ;
                LinearLayout.LayoutParams linearLayout_child_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout_child.setLayoutParams(linearLayout_child_params);
                //linearLayout_child.setPadding(0,12,0,12);
                //text first
                LinearLayout linearLayout_text_first = new LinearLayout(this) ;
                LinearLayout.LayoutParams linearLayout_text_first_params = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT,1f);
                linearLayout_text_first.setLayoutParams(linearLayout_text_first_params);
                linearLayout_text_first.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams first_text_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                                             0,1f);
                //text last
                LinearLayout linearLayout_text_last = new LinearLayout(this) ;
                LinearLayout.LayoutParams linearLayout_text_last_params = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT,1.8f);
                linearLayout_text_last_params.setMargins(10,12,0,12);
                linearLayout_text_last.setLayoutParams(linearLayout_text_last_params);
                linearLayout_text_last.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams last_text_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                            0,1.8f);
                //last_text_params.setMargins(10,12,0,12);

                //image buttons
                LinearLayout.LayoutParams image_params_start = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,0);
                image_params_start.setMargins(5,0,5,0);
                LinearLayout.LayoutParams image_params_end = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,0);
                image_params_end.setMargins(5,0,6,0);

                TextView textView = new TextView(this);
                ImageView startbutton = new ImageView(this);
                ImageView stopbutton = new ImageView(this);

                textView.setText(radiostation.getRadioName());
                textView.setLayoutParams(first_text_params);
                textView.setTextAppearance(this, android.R.style.TextAppearance_Large);
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                linearLayout_text_first.addView(textView);

                TextView textViewDesc = new TextView(this);
                textViewDesc.setText(radiostation.getRadiodesc());
                textViewDesc.setLayoutParams(last_text_params);
                textViewDesc.setTextAppearance(this, android.R.style.TextAppearance_Small);
                textViewDesc.setTextColor(Color.WHITE);
                textViewDesc.setGravity(Gravity.CENTER_VERTICAL);
                linearLayout_text_last.addView(textViewDesc);

                startbutton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play_over_video));
                //startbutton.setId(i+1);
                HashMap hp2 = new HashMap(1);
                hp2.put("start", radiostation);
                startbutton.setTag(hp2);

                startbutton.setLayoutParams(image_params_start);
                startbutton.setOnClickListener(mListener);
                startbutton.setBackgroundColor(Color.TRANSPARENT);//transparent

                stopbutton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause_over_video));
                //stopbutton.setId(2);
                HashMap hp = new HashMap(1);
                hp.put("stop",radiostation);
                stopbutton.setTag(hp);
                stopbutton.setLayoutParams(image_params_start);
                stopbutton.setOnClickListener(mListener);
                stopbutton.setBackgroundColor(Color.TRANSPARENT);
                stopbutton.setVisibility(View.GONE);

                ImageView equalizer_dummy = new ImageView(this);
                equalizer_dummy.setBackgroundResource(R.drawable.equalizer_animation_dummy);
                equalizer_dummy.setLayoutParams(image_params_end);
                this.equalizer_dummy = (AnimationDrawable)equalizer_dummy.getBackground();
                this.equalizer_dummy.setAlpha(75);

                ImageView equalizer = new ImageView(this);
                equalizer.setBackgroundResource(R.drawable.equalizer_animation);
                equalizer.setLayoutParams(image_params_end);
                this.equalizer = (AnimationDrawable)equalizer.getBackground();
                this.equalizer.setAlpha(90);

                linearLayout_child.addView(linearLayout_text_first);
                linearLayout_child.addView(startbutton);
                linearLayout_child.addView(stopbutton);
                linearLayout_child.addView(equalizer_dummy);
                linearLayout_child.addView(equalizer);
                linearLayout_child.addView(linearLayout_text_last);

                linearLayout_child.getChildAt(4).setVisibility(View.GONE);//animation invisible
                linearLayout_main.addView(linearLayout_child);

                i++;
            }
    }

    public void clearNotification()
    {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mId);
    }

    public void createNotification(String song_name)
    {
        Intent intent = new Intent(this, MainActivity.class);
        //PendingIntent.FLAG_CANCEL_CURRENT will bring the app back up again
        PendingIntent pIntent =  PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(song_name)
                        .setContentIntent(pIntent)
                .setOngoing(true);

        NotificationManagerCompat mNotificationManager =
                NotificationManagerCompat.from(this);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public void setSleepminute(int minute)
    {
        this.mSleepminute=minute;
        if (mSleepminute==0)
        {
            setSleepRedVisible(false);
        }else
        {
            setSleepRedVisible(true);
        }
    }

    public void setSleepRedVisible(boolean visible)
    {
        if (visible)
        {
            mToolbar.getMenu().findItem(R.id.menu_item_sleeper).setIcon(R.drawable.ic_action_action_alarm_pressed);
            //findViewById(R.id.imageView_sleeper).setVisibility(View.GONE);
            //findViewById(R.id.imageView_sleeper_pressed).setVisibility(View.VISIBLE);
        }
        else
        {
            mToolbar.getMenu().findItem(R.id.menu_item_sleeper).setIcon(R.drawable.ic_action_action_alarm);
            //findViewById(R.id.imageView_sleeper_pressed).setVisibility(View.GONE);
            //findViewById(R.id.imageView_sleeper).setVisibility(View.VISIBLE);
        }
    }

    public int getSleepminute()
    {
        return this.mSleepminute;
    }

    public int getNotifId ()
    {
        return mId;
    }


    private int mAdCount=1  ;
    public  boolean isValidAdCount()
    {
        if ( (mAdCount%15)==0)
        {
            mAdCount=1;
            return true;
        }else
        {
            mAdCount++;
            return false;
        }
    }


    private int mAdResumeCount=1  ;
    public  boolean isValidAdCountResume()
    {
        if ( (mAdResumeCount%2)==0)
        {
            mAdResumeCount=1;
            return true;
        }else
        {
            mAdResumeCount++;
            return false;
        }
    }


}