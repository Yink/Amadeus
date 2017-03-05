package com.example.yink.amadeus;

/**
 * Created by Yink on 28.02.2017.
 */

class VoiceLine {
    final private int id;
    final private int mood;
    final private int subtitle;

    VoiceLine(int id, int mood, int subtitle) {
        this.id = id;
        this.mood = mood;
        this.subtitle = subtitle;
    }

    int getId(){
        return id;
    }

    int getMood() {
        return mood;
    }

    int getSubtitle() {
        return subtitle;
    }
}
