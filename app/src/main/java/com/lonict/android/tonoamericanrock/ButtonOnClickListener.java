package com.lonict.android.tonoamericanrock;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
//import com.spoledge.aacdecoder.MP3Player;
//import com.spoledge.aacdecoder.PlayerCallback;

/**
 * Created by Efe Avsar on 11.2.2015.
 */
public class ButtonOnClickListener extends MainActivity implements View.OnClickListener  {

    public final MediaPlayer mediaPlayer = new MediaPlayer();
    public static final IcyStreamMetadata streamMeta = new IcyStreamMetadata();
    //public  MediaPlayer mediaPlayer = new MediaPlayer();
    private ImageView mButton  ;
    private RadioStationPOJO radioStationPOJO ;
    //private ProgressDialog progress ;
    private SleeperDialog progress;
    private String mStreamTitle ;
    private boolean mIsStoppedByUser=false;
    private HashMap mHp  ;


    public ButtonOnClickListener( )
    {
        startStreamTitleUpdater();
    }
    public void onClick(View v)
    {

       mButton = (ImageView)v;
      // final  ProgressDialog progress_final = new ProgressDialog(mButton.getContext());
       final  SleeperDialog progress_final = new SleeperDialog(mButton.getContext());

       progress = progress_final;
       // startMediaPlayer(String url) ;
       //tekse start button
        mHp =(HashMap)mButton.getTag();
        if (mHp.get("start")!=null)
        {
            radioStationPOJO = (RadioStationPOJO)mHp.get("start");
            startMediaPlayer();
        }
        else
        {
            stopMediaPlayer();
            stopPressed();
        }
        /*
       if (mButton.getId()%2==1)
       {
           radioStationPOJO = (RadioStationPOJO)mButton.getTag();
           startMediaPlayer();
       }//ciftse stop button
       else if (mButton.getId()%2==0)
       {
           stopMediaPlayer();
           stopPressed();
       }
*/
        if (isValidAdCount())
        {
            showAds();
        }
    }
    public void startMediaPlayer(){
        //progress.setMessage("Please wait...");
        //progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        stopPressed();
        stopMediaPlayer();
        progress.showProgress();
        prepareMediaPlayer();
        //prepareMediaPlayer();
        //StartRadioTask st = new StartRadioTask();
        //st.execute(mButton);
    }

    public void stopMediaPlayer()
    {
        if (mediaPlayer!=null)
        {
            if (mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
                setSongName("");
                //mediaPlayer.release();
                //getMediaPlayer().release();
                //setMediaPlayer(null);
            }
            //setSongName("");
            mStreamTitle="";
            mIsStoppedByUser=true;
        }
        //release must be last time mediaPlayer.release();
    }
    private void stopPressed()
    {
        //buttons
        ViewGroup parent = (ViewGroup)mButton.getParent().getParent();
        for (int i=0 ; i<parent.getChildCount();i++)
        {
            LinearLayout tbr = (LinearLayout)parent.getChildAt(i);
              //stopbutton //equalizer
              tbr.getChildAt(2).setVisibility(View.GONE);
              tbr.getChildAt(4).setVisibility(View.GONE);
              ((AnimationDrawable) tbr.getChildAt(4).getBackground()).stop();
              //startbutton
              tbr.getChildAt(1).setVisibility(View.VISIBLE);
              tbr.getChildAt(3).setVisibility(View.VISIBLE);
        }
    }
    /*private int finishStream()
    {
         //mediaPlayer.start();
         return 10;
         //setMediaPlayer(mediaPlayer);
    }*/

    public void startStreamTitleUpdater()
    {

        final Handler handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Log.d("xxMEtdataEX3", "handler get message");
                    Bundle bundle = msg.getData();
                    if (bundle.getString("networkthread").equals("finished")){
                        Log.d("xxMEtdataEX4", "handler get message");
                        setSongName(mStreamTitle);
                        if (!mStreamTitle.equals("") && mStreamTitle != null) {
                            tracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Player Metadata")
                                    .setAction("Song Name")
                                    .setLabel(mStreamTitle + "")
                                    .build());

                            tracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Player Metadata")
                                    .setAction("ICY-URL")
                                    .setLabel(radioStationPOJO.getRadioURL() + "")
                                    .build());
                        }
                    }
                    return false;
                }
            }) ;

            Thread networkthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        android.os.Process.setThreadPriority(
                                android.os.Process.THREAD_PRIORITY_BACKGROUND
                        );
                        Bundle bundle = new Bundle();
                        bundle.putString("networkthread", "finished");
                        Message msg;
                        while (true)
                        {
                            Thread.sleep(30000); //seconds
                            if (mediaPlayer.isPlaying())
                            {
                                streamMeta.setStreamUrl(new URL(radioStationPOJO.getRadioURL()));
                                streamMeta.refreshMeta();
                                mStreamTitle =  streamMeta.getAllTitle(); //streamMeta.getArtist()+"-"+streamMeta.getTitle();
                                msg = handler.obtainMessage() ;
                                msg.setData(bundle);
                                Log.d("xxMEtdataEX0", "thread finished Metadatainit");
                                handler.sendMessage(msg);
                                Log.d("xxMEtdataEX1", "thread sent message");
                            }
                        }
                    } catch (Exception e) {

                        Log.e("xxMEtdataEX2", e.toString());
                    }
                }
            });
            networkthread.start();
    }

    private void prepareMediaPlayer() {
        //stopMediaPlayer();
        /*if ( mediaPlayer==null)
        {
            mediaPlayer = new MediaPlayer();
        }*/
        //mediaPlayer.setResponseCodeCheckEnabled(true);
        //mediaPlayer.setMetadataEnabled(true);
        //mediaPlayer.reset();
        try
        {
            //mediaPlayer = MediaPlayer.create(mButton.getContext().getApplicationContext(), );
            //mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();

            //mediaPlayer = MediaPlayer.create(mButton.getContext().getApplicationContext(),Uri.parse(radioStationPOJO.getRadioURL()));


            mediaPlayer.setWakeMode(mButton.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(radioStationPOJO.getRadioURL());
           // mediaPlayer.setDataSource(mButton.getContext(), Uri.parse());
            mediaPlayer.prepareAsync();

            Log.d("xxPlayerinit", "completed");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mp == mediaPlayer) {
                        mp.start();
                        progress.dismissProgress();

                        //audioTrack.set(48000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playPressed();
                                // Builder parameters can overwrite the screen name set on the tracker.
                            }
                        });
                    }
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    String errorWhat;
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            errorWhat = "MEDIA_ERROR_UNKNOWN";
                            break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            errorWhat = "MEDIA_ERROR_SERVER_DIED";
                            break;
                        default:
                            errorWhat = "!";
                    }
                    String errorExtra;
                    switch (extra) {
                        case MediaPlayer.MEDIA_ERROR_IO:
                            errorExtra = "MEDIA_ERROR_IO";
                            break;
                        case MediaPlayer.MEDIA_ERROR_MALFORMED:
                            errorExtra = "MEDIA_ERROR_MALFORMED";
                            break;
                        case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                            errorExtra = "MEDIA_ERROR_UNSUPPORTED";
                            break;
                        case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                            errorExtra = "MEDIA_ERROR_TIMED_OUT";
                            break;
                        default:
                            errorExtra = "!";
                    }
                    Log.e("errorWhat", errorWhat);
                    Log.e("errorExtra", errorExtra);
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("xxOnCompletion", "entered");
                }
            });
            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.d("xxMetadatawhat", what + "");
                    if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
                        Log.d("xxMetadataUpdated", "Entered");
                    }
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        Log.d("xxBufferStarted", "Entered");

                    }
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        Log.d("xxBufferEnd", "Entered");

                    }
                    return false;
                }
            });

            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.d("xxMediaBuffer", percent + "");
                }
            });


        }catch (Exception e)
        {
            Log.e("xxIOExp",e.toString());
            e.printStackTrace();
        }


/*
        final PlayerCallback playerCallback = new PlayerCallback() {
            @Override
            public void playerStarted() {

                Log.d("playerStarted:", "yes");
            }

            @Override
            public void playerStopped(int i) {
                //Log.e("stopped:",i+"");

                Log.d("playerStopped:", "yes");

                if(!mIsStoppedByUser)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startMediaPlayer();
                        }
                    });
                    mIsStoppedByUser=false;
                }
            }

            @Override
            public void playerMetadata(String s, String s2) {
                Log.d("PLAYER METAs",s+"");
                Log.d("PLAYER METAs2",s2+"");

                if ((""+s).equals("icy-url"))
                {
                    if (!s2.equals("")&&s2!=null)
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Player Metadata")
                            .setAction("ICY-URL")
                            .setLabel(s2+"")
                            .build());
                }

                if ((""+s).equals("StreamTitle"))
                {
                    if (s2.length()>45)
                    {
                        mStreamTitle = s2.substring(0,43)+"..";
                    }else mStreamTitle=s2;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSongName(mStreamTitle);
                            // Builder parameters can overwrite the screen name set on the tracker.
                            if (!mStreamTitle.equals("")&&mStreamTitle!=null)
                            {
                                tracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Player Metadata")
                                        .setAction("Song Name")
                                        .setLabel(mStreamTitle+"")
                                        .build());
                            }
                        }
                    });
                }
            }

            @Override
            public void playerPCMFeedBuffer(boolean b, int i, int i2) {
               // boolean isBufferFinished = b ;
               // Log.d("playerPCMFeedBuffer","yes");
               // Log.d("Buffer i:", "" + i);
               // Log.d("Buffer i2:", "" + i2);
               // if (!isBufferFinished) {
               //     Log.d("Buffered:", "no");
               // }
               // else
               // {
              //      Log.d("Buffered:", "yes");
              //  }
            }
            @Override
            public void playerException(Throwable throwable) {
                    //if (throwable instanceof java.net.SocketException)
                    //{
                Log.d("EXCEPTION:",throwable.getMessage());
                if (throwable.getMessage().equals("Error response: 403 Forbidden"))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopMediaPlayer();
                            stopPressed();
                            progress.dismissProgress();
                            mCustomDialog.showStationUnAvaliableDialog();
                        }
                    });
                }
                   // }
            }

            @Override
            public void playerAudioTrackCreated(AudioTrack audioTrack) {
                progress.dismissProgress();
                //audioTrack.set(48000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playPressed();
                    }
                });
            }
        };

        mediaPlayer.setAudioBufferCapacityMs(1250);
        mediaPlayer.setDecodeBufferCapacityMs(1250);
        mediaPlayer.setPlayerCallback(playerCallback);
        try {

            mediaPlayer.playAsync(radioStationPOJO.getRadioURL());

        } catch (IllegalStateException e) {
            Log.d("mediaPlayer.play", e.toString());
        }*/
    }

    private void setSongName(String name)
    {
        TextView song_name = (TextView) ( (MainActivity)mButton.getContext() ).findViewById(R.id.text_view_song_name);
        song_name.setText(name);
        ((MainActivity)mButton.getContext()).createNotification(name);
    }
    private void playPressed()
    {
        //LinearLayout
        ViewGroup parent = (ViewGroup)mButton.getParent().getParent();
        for (int i=0 ; i<parent.getChildCount();i++)
        {
            LinearLayout tbr = (LinearLayout)parent.getChildAt(i) ;
            //child 0 : textview
            //child 1 : ImageView
            //child 2 : ImageView
            if (!( tbr.getChildAt(1).getTag().equals(mButton.getTag() )) )
            {
                //other rows
                //stop button&eq_dummy
                tbr.getChildAt(2).setVisibility(View.GONE);
                tbr.getChildAt(3).setVisibility(View.VISIBLE);
                //start button
                tbr.getChildAt(1).setVisibility(View.VISIBLE);
                tbr.getChildAt(4).setVisibility(View.GONE);
                ((AnimationDrawable) tbr.getChildAt(4).getBackground()).stop();
            }else
            {
                //current button pressed row
                // stop button&eq_dummy
                tbr.getChildAt(2).setVisibility(View.VISIBLE);
                tbr.getChildAt(3).setVisibility(View.GONE);
                //start button //equalizer
                tbr.getChildAt(1).setVisibility(View.GONE);
                tbr.getChildAt(4).setVisibility(View.VISIBLE);
                ((AnimationDrawable) tbr.getChildAt(4).getBackground()).start();
            }
        }
    }

}
