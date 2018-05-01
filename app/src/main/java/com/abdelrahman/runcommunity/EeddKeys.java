package com.abdelrahman.runcommunity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdalrahman on 2/1/2018.
 */

class EeddKeys {

    private String pushedusername;
    private String pushedDate;
    private String pushedDetails;
    private String nodeKey;
    private int counter;
    private List<String> named = new ArrayList<>();

    public String getPushedusername() {return pushedusername;}
    public void setPushedusername(String pushedusername) {this.pushedusername = pushedusername;}
    public String getPushedDate() {return pushedDate;}
    public void setPushedDate(String pushedData) {this.pushedDate = pushedData;}
    public String getPushedDetails() {return pushedDetails;}
    public void setPushedDetails(String pushedDetails) {this.pushedDetails = pushedDetails;}
    public String getNodeKey() {return nodeKey;}
    public void setNodeKey(String nodeKey) {this.nodeKey = nodeKey;}
    public int getCounter() {return counter;}
    public void setCounter(int counter) {this.counter = counter;}

    public List<String> getNamed() {return named;}
    public void setNamed(List<String> named) {this.named = named;}

    public void setNamedObject(String namedObject) {named.add(namedObject);}
}
