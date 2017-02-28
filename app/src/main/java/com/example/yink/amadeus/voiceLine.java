package com.example.yink.amadeus;

/**
 * Created by Yink on 28.02.2017.
 */

public class VoiceLine {
    public String getText() {
        return text;
    }

    public int getState() {
        return state;
    }

    final private String text;
    final private int state;

    public VoiceLine(String text, int state){
        this.text = text;
        this.state = state;
    }
}
