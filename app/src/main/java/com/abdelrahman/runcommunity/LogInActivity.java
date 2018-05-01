package com.abdelrahman.runcommunity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.InetAddress;

public class LogInActivity extends AppCompatActivity{

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private SharedPreferences mSharedPreferences;
    private static final int RC_SIGN_IN = 1;
    private NetworkChangeReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        networkReceiver = new NetworkChangeReceiver();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
       if(user!= null){
           makeUserLogIn(user);
       }
       else {
           mAuthStateListener = new FirebaseAuth.AuthStateListener() {
               @Override
               public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                   FirebaseUser mUser = firebaseAuth.getCurrentUser();
                   if (mUser != null) {
                       makeUserLogIn(mUser);
                   } else {
                       startActivityForResult(
                               AuthUI.getInstance()
                                       .createSignInIntentBuilder()
                                       .setIsSmartLockEnabled(false)
                                       .setProviders(
                                               AuthUI.EMAIL_PROVIDER,
                                               AuthUI.GOOGLE_PROVIDER)
                                       .build(),
                               RC_SIGN_IN);
                   }

               }
           };
           if (!isInternetAvailable()) {
               Toast.makeText(this, "Please Check your Internet Connection and try again", Toast.LENGTH_SHORT).show();
           }
       }
    }

    private void makeUserLogIn(FirebaseUser logInUser) {
        if (!(mSharedPreferences.getString("currentDataBelongTo", "").equals(logInUser.getDisplayName()))) {
            startService(new Intent(LogInActivity.this,DownloadData.class));
        }
        startActivity(new Intent(LogInActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_CANCELED){
                finish();
            }

        }
    }
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) ((LogInActivity.this)
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReceiver,new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isInternetAvailable()){
                mFirebaseAuth.addAuthStateListener(mAuthStateListener);
            }else{
                mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            }
        }
    }
}
