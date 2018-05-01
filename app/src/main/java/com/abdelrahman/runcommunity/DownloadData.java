package com.abdelrahman.runcommunity;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by abdalrahman on 2/3/2018.
 */

public class DownloadData extends Service {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getContentResolver().delete(RunsContract.RunsEntry.CONTENT_URI,null,null);
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase= FirebaseDatabase.getInstance();

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {


            }

            @Override
            protected Void doInBackground(Void... voids) {
                startDownload();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(DownloadData.this, "Your Data moved to be locally", Toast.LENGTH_SHORT).show();
                try {
                    //  mDatabaseReference.removeEventListener(mvalueEventListener);
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext()).edit();
                    editor.putString("currentDataBelongTo",user.getDisplayName());
                    editor.apply();
                }catch (Exception e){
                }finally{
                    stopSelf();
                }
            }
        }.execute();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    synchronized private void startDownload(){
        final DatabaseReference mDatabaseReference= mFirebaseDatabase.getReference()
                .child("uudd")
                .child(user.getUid());

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mDataSnapshot) {
                for (DataSnapshot dataSnapshot : mDataSnapshot.getChildren()) {
                    UuddKeys uuddKeys = dataSnapshot.getValue(UuddKeys.class);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(RunsContract.RunsEntry.COLUMN_DISTANCE,uuddKeys.getDistance());
                    contentValues.put( RunsContract.RunsEntry.COLUMN_DURATION,uuddKeys.getDuration());
                    contentValues.put(RunsContract.RunsEntry.COLUMN_DATE, uuddKeys.getmDate() );
                    getContentResolver().insert(RunsContract.RunsEntry.CONTENT_URI, contentValues);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
