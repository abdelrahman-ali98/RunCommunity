package com.abdelrahman.runcommunity;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.text.DecimalFormat;

/**
 * Created by abdalrahman on 1/26/2018.
 */

public class RunningFragment extends Fragment implements
        OnMapReadyCallback, RunService.RunServiceCallbacks{

    private MapView myMap;
    private TextView distance;
    private TextView speed;
    private Chronometer myChronometer;
    private Button pauseResumeBtn;
    private Button stopBtn;
    private OnFragmentInteractionListener mListener;

    private boolean mBound = false;
    private RunService mMyService;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), RunService.class);
        if(!isMyServiceRunning(RunService.class)) {getActivity().startService(intent);}
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mListener != null) {mListener.onFragmentInteraction("Keep running");}

        View view= inflater.inflate(R.layout.running_layout, container, false);

        myMap = (MapView) view.findViewById(R.id.map);
        distance = (TextView) view.findViewById(R.id.distance);
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    getActivity().stopService(new Intent(getActivity(), RunService.class));
                    getActivity().unbindService(mConnection);
                    mBound = false;
                }

            }
        });
        speed = (TextView) view.findViewById(R.id.speed);
        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),isMyServiceRunning(RunService.class)+ "", Toast.LENGTH_SHORT).show();
            }
        });
        myChronometer =(Chronometer) view.findViewById(R.id.myChronometer);
        pauseResumeBtn=(Button) view.findViewById(R.id.pauseResumeBtn);
        pauseResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pauseResumeBtn.getText().toString().equals("Pause")) {

                    pauseResumeBtn.setText("Resume");
                    if(mBound) {
                        mMyService.stopChronometer();
                        mMyService.setIsCronometerEnabled(false);
                    }
                    myChronometer.stop();

                }else if(pauseResumeBtn.getText().toString().equals("Resume")){
                    pauseResumeBtn.setText("Pause");
                    myChronometer.setBase(SystemClock.elapsedRealtime() + mMyService.getWhenChronometerStopped());
                    myChronometer.start();
                    if(mBound) {
                        mMyService.setIsCronometerEnabled(true);
                        mMyService.resumeChronometerBase(myChronometer.getBase());
                    }
                }
            }
        });
        stopBtn = (Button) view.findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().stopService(new Intent(getActivity(),RunService.class));
                getActivity().unbindService(mConnection);
                myChronometer.stop();
                mBound = false;
                mListener.stopTheRun();
            }
        });

        myMap.onCreate(savedInstanceState);
        myMap.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    @Override
    public void updateUI(float distance, float speed) {

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float distanceNewFormat = Float.valueOf(decimalFormat.format(distance));

        //  long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

        this.distance.setText("Distance Km\n" + String.valueOf(distanceNewFormat));
        if (!(speed == 0)) {
            int speedMins = (int)(speed);
            int speedSecs= (int) (speed-speedMins)*60;
            this.speed.setText("Speed min/km\n" + speedMins+":"+speedSecs);
        } else {

            this.speed.setText("Speed \n" + "layover");
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
        void stopTheRun();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBound) {
            mMyService.setCallbacks(null);
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        myChronometer.stop();
        myChronometer=null;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            RunService.MyBinder binder = (RunService.MyBinder) service;
            mMyService = binder.getService();
            mBound = true;

            mMyService.setCallbacks(RunningFragment.this);
            myChronometer.setBase(mMyService.getChronometerBase());
            if(mMyService.getIsCronomrterEnabled()){myChronometer.start();}
            if(!mMyService.getIsCronomrterEnabled()){pauseResumeBtn.setText("Resume");}
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
    }

}
