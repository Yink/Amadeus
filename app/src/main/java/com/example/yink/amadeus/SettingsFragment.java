package com.example.yink.amadeus;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Yink on 05.03.2017.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
