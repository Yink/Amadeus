package com.example.yink.amadeus;

/**
 * Created by Yink on 28.02.2017.
 */

public class VoiceLine {
    public int getId(){
        return id;
    }
    public int getState() {
        return state;
    }

    final private int id;
    final private int state;

    public VoiceLine(int id, int state){
        this.id = id;
        this.state = state;
    }
}
