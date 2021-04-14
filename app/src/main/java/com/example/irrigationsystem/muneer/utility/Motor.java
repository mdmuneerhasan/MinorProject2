package com.example.irrigationsystem.muneer.utility;

public class Motor {
    public String id;
    public String name=null;
    long starTime;
    boolean isRunning=false;

    public Motor() {
        id= String.valueOf(System.currentTimeMillis());
    }


}
