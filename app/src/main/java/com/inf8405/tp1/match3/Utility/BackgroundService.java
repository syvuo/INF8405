package com.inf8405.tp1.match3.Utility;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.inf8405.tp1.match3.R;


// SOURCE http://stackoverflow.com/questions/8209858/android-background-music-service
public class BackgroundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.tobuhope);
        player.setLooping(true); // Set looping
        player.setVolume(30,30);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return START_NOT_STICKY;
    }

    public IBinder onUnBind(Intent arg0) {
        return null;
    }

    public void onStop() {
        player.stop();

    }
    public void onPause() {

    }
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {
    }
}