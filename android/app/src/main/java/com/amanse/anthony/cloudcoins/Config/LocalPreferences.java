package com.amanse.anthony.cloudcoins.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

public class LocalPreferences {
    private static final String preferencesFile = "com.amanse.anthony.fitcoinandroid.localPreferences";
    private FragmentActivity fragmentActivity;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    public LocalPreferences(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        this.sharedPreferences = getSharedPreferences();
        this.editor = sharedPreferences.edit();
    }

    public SharedPreferences getSharedPreferences() {
        return fragmentActivity.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
    }

    public String getCurrentEventSelected() {
        return sharedPreferences.getString("currentEvent",null);
    }

    public boolean setCurrentEventSelected(String eventName) {
        editor.putString("currentEvent", eventName);
        return editor.commit();
    }
}
