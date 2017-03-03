package com.example.yink.amadeus;

/**
 * Created by Yink on 28.02.2017.
 */

class VoiceLine {
    final private int id;
    final private int mood;

    VoiceLine(int id, int mood) {
        this.id = id;
        this.mood = mood;
    }

    int getId(){
        return id;
    }

    int getMood() {
        return mood;
    }
}
