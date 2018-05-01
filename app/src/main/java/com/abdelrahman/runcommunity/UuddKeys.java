package com.abdelrahman.runcommunity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by abdalrahman on 2/1/2018.
 */

public class UuddKeys {
    private String mDate;
    private float distance;
    private long duration;

    public UuddKeys(){}
    public UuddKeys(String mDate,float distance,long duration){this.distance = distance;this.duration=duration;this.mDate=mDate;}
    public String getmDate() {return mDate;}
    public void setmDate(String mDate) {this.mDate = mDate;}
    public float getDistance() {return distance;}
    public void setDistance(long distance) {this.distance = distance;}
    public long getDuration() {return duration;}
    public void setDuration(long duration) {this.duration = duration;}

}
