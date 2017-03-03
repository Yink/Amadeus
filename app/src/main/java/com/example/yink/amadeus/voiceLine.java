package com.example.yink.amadeus;

/**
 * Created by Yink on 28.02.2017.
 */

public class VoiceLine {
    final private int id;
    final private int mood;

    public VoiceLine(int id, int mood) {
        this.id = id;
        this.mood = mood;
    }

    public int getId(){
        return id;
    }

    public int getMood() {
        return mood;
    }
}