package com.amanse.anthony.cloudcoins.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

public class SelectedEventPreferences {
    private static final String preferencesFile = "com.amanse.anthony.fitcoinandroid.eventPreferences";
    private String selectedEvent;
    private FragmentActivity fragmentActivity;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    public SelectedEventPreferences(FragmentActivity fragmentActivity, String selectedEvent) {
        this.selectedEvent = selectedEvent;
        this.fragmentActivity = fragmentActivity;
        this.sharedPreferences = getSharedPreferences();
        this.editor = sharedPreferences.edit();
    }

    public SharedPreferences getSharedPreferences() {
        return fragmentActivity.getSharedPreferences(preferencesFile + "." + selectedEvent, Context.MODE_PRIVATE);
    }

    public boolean setBlockchainUserId(String userId) {
        editor.putString("blockchainUserId", userId);
        return editor.commit();
    }

    public String getBlockchainUserId() {
        return sharedPreferences.getString("blockchainUserId",null);
    }

    // Sets user info
    // user info should be a string converted from JSON object
    // with name and png keys
    // note: Use gson and UserInfoModel to convert.
    public void setUserInfo(String userInfo) {
        editor.putString("userInfo", userInfo);
        editor.apply();
    }

    public String getUserInfo() {
        return sharedPreferences.getString("userInfo",null);
    }
}
