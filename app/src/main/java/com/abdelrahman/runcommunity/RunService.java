package com.abdelrahman.runcommunity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by abdalrahman on 1/26/2018.
 */

public class RunService extends Service implements LocationListener{

    private final IBinder mBinder = new MyBinder();
    public RunServiceCallbacks runServiceCallbacks;
    private Chronometer mChronometerService;
    private LocationManager locationManager;
    private static final int NOTIFICATION_ID =5;
    private float totalDistance;
    private Location oldLocation;
    private float speed;
    private boolean isCronometerEnabled;
    private long whenChronometerStopped;


    @Override
    public IBinder onBind(Intent intent) {return mBinder;}

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        setForegroundMode();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mChronometerService = new Chronometer(this);
        mChronometerService.start();
        setIsCronometerEnabled(true);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mChronometerService.stop();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String strDate = sdf.format(c.getTime());

        ContentValues contentValues = new ContentValues();
        contentValues.put(RunsContract.RunsEntry.COLUMN_DISTANCE, (totalDistance < 100)? 0:totalDistance);
        contentValues.put(RunsContract.RunsEntry.COLUMN_DURATION,isCronometerEnabled?
                ((int)((SystemClock.elapsedRealtime() - mChronometerService.getBase())/1000)):
                (int)(whenChronometerStopped/(-1000)));
        contentValues.put(RunsContract.RunsEntry.COLUMN_DATE, strDate);
        getContentResolver().insert(RunsContract.RunsEntry.CONTENT_URI, contentValues);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase
                .getReference()
                .child("uudd")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        mDatabaseReference.push().setValue(new UuddKeys(
                strDate,
                (totalDistance < 100)? 0:totalDistance,
                isCronometerEnabled?
                ((int)((SystemClock.elapsedRealtime() - mChronometerService.getBase())/1000)):
                (int)(whenChronometerStopped/(-1000)))
        );

        stopForeground(true);

        mChronometerService = null;
        locationManager.removeUpdates(this);

    }


    public long getChronometerBase(){
        if(isCronometerEnabled){
            return mChronometerService.getBase();
        }
        mChronometerService.setBase(whenChronometerStopped+SystemClock.elapsedRealtime());
        return mChronometerService.getBase();
    }
    public void stopChronometer(){
        whenChronometerStopped= mChronometerService.getBase() - SystemClock.elapsedRealtime();
        mChronometerService.stop();
    }

    public void resumeChronometerBase(long s){
        mChronometerService.setBase(s);
        mChronometerService.start();
    }
    public void setIsCronometerEnabled(boolean s){this.isCronometerEnabled = s;}
    public boolean getIsCronomrterEnabled(){return isCronometerEnabled;}
    public long getWhenChronometerStopped() {return whenChronometerStopped;}

   public void setForegroundMode() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new
                Notification.Builder(this)
                .setContentTitle("You started Run")
                .setContentText("Click and go to your run")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }



    @Override
    public void onLocationChanged(Location location) {

        if (!(oldLocation == null)) {
            totalDistance += (float) (location.distanceTo(oldLocation)/1000);
        }

        if(location.hasSpeed()) {
             speed = (float) (16.66666 / (location.getSpeed()));
        }
        if(speed >59) {
            speed =0;
        }
        runServiceCallbacks.updateUI(totalDistance,speed);
        this.oldLocation = location;
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "GPS is disabled, Please Enable it", Toast.LENGTH_LONG).show();
    }


    public class MyBinder extends Binder {
        RunService getService() {
            return RunService.this;
        }
    }
    interface RunServiceCallbacks{void updateUI(float distance, float speed);}
    public void setCallbacks(RunServiceCallbacks callbacks) {this.runServiceCallbacks = callbacks;}

}
