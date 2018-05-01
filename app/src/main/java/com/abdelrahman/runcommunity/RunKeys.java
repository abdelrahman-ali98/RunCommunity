package com.abdelrahman.runcommunity;

/**
 * Created by abdalrahman on 1/31/2018.
 */

public class RunKeys {
    private String date;
    private int duration;
    private int distance;
    private int myId;

    public int getDistance() {return distance;}
    public int getDuration() {return duration;}
    public String getDate() {return date;}
    public int getMyId() {return myId;}

    public void setDate(String date) {this.date = date;}
    public void setDistance(int distance) {this.distance = distance;}
    public void setDuration(int duration) {this.duration = duration;}
    public void setMyId(int myId) {this.myId = myId;}

}
