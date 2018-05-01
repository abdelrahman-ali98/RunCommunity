package com.abdelrahman.runcommunity;


import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends  AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener,
        RunsAdapter.ListItemClickListener,
        RunningFragment.OnFragmentInteractionListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked();
            }
        });
        fab.setVisibility(View.INVISIBLE);
        mFirebaseAuth = FirebaseAuth.getInstance();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.userNameView);
        navUsername.setText(mFirebaseAuth.getCurrentUser().getDisplayName());

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                       startActivity(new Intent(MainActivity.this,LogInActivity.class));
                        finish();
                    }
                }
            };

        if (isMyServiceRunning(RunService.class)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new RunningFragment());
            ft.commit();
        }
        else{
            initialFragment();
        }

    }

    private void fabClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share your experience");

        final EditText input = new EditText(MainActivity.this);

        input.setHint("Let us start");
        input.setMaxLines(7);
        builder.setView(input);

        builder.setPositiveButton("push", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String textValue =input.getText().toString();
                if(!(textValue.equals("")|| textValue==null)) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = sdf.format(c.getTime());

                    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("eedd");

                    EeddKeys mEeddkeys = new EeddKeys();
                    mEeddkeys.setPushedusername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    mEeddkeys.setPushedDate(strDate);
                    mEeddkeys.setPushedDetails(input.getText().toString());
                    mEeddkeys.setCounter(1);
                    mEeddkeys.setNamedObject(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    mDatabaseReference.push().setValue(mEeddkeys);

                }else{
                    Toast.makeText(MainActivity.this, "Failed, try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "No settings implemnted for now :)", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.feedsNews) {
            initialFragment();
        } else if (id == R.id.myRuns) {
            fab.setVisibility(View.INVISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new MyRunsFragment());
            ft.commit();
        } else if (id == R.id.startRunning) {
            fab.setVisibility(View.INVISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, new RunningFragment());
            ft.commit();
        } else if (id == R.id.logOut) {
            if(!isMyServiceRunning(RunService.class)) {
                getContentResolver().delete(RunsContract.RunsEntry.CONTENT_URI, null, null);
                AuthUI.getInstance().signOut(this);
            }else{
                Toast.makeText(this, "Please stop the run first", Toast.LENGTH_LONG).show();
            }
        }

        onBackPressed();
        return true;
    }

    private void initialFragment() {
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, new PushedFragment());
        ft.commit();
    }

    @Override
    public void onListItemClick(View view, int clickedItemIndex) {
        Toast.makeText(this, " We haven't implemented onClicked for now ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(String title){getSupportActionBar().setTitle(title);}

    @Override
    public void stopTheRun() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, new MyRunsFragment());
        ft.commit();
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }catch (Exception e){
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }catch (Exception e){
        }
    }
}
