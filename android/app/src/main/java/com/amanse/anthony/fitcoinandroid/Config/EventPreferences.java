package com.amanse.anthony.fitcoinandroid.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;

public class EventPreferences {
    private static final String preferencesFile = "com.amanse.anthony.fitcoinandroid.eventPreferences";
    private FragmentActivity fragmentActivity;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    public EventPreferences(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        this.sharedPreferences = getSharedPreferences();
        this.editor = sharedPreferences.edit();
    }

    public SharedPreferences getSharedPreferences() {
        return fragmentActivity.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
    }

    // Save an event with time now.
    public long enterNewEvent(String eventName) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        editor.putLong(eventName, cal.getTimeInMillis());
        editor.apply();
        return cal.getTimeInMillis();
    }

    public Long getTimeEnteredEvent(String eventName) {
        if (sharedPreferences.contains(eventName)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            return sharedPreferences.getLong(eventName,cal.getTimeInMillis());
        } else {
            return null;
        }
    }

    public String[] getEventsRegistered() {
        return sharedPreferences.getAll().keySet().toArray(new String[0]);
    }
}
