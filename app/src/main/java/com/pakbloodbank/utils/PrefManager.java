package com.pakbloodbank.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import static com.pakbloodbank.utils.Constant.TAG;


public class PrefManager {

    private final SharedPreferences        sharedPreferences;
    private final SharedPreferences.Editor editor;

    public PrefManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public Boolean getIsConsent() {
        return sharedPreferences.getBoolean("consent", true);
    }

    public void setIsConsent(Boolean isConsent) {
        editor.putBoolean("consent", isConsent);
        editor.apply();
    }

    public String getPhoneNumber() {
        if (Methods.isEmulator()) {
            return "03001122334";
        } else
            return sharedPreferences.getString("phoneNumber", null);
    }

    public void setPhoneNumber(String phoneNumber) {
        editor.putString("phoneNumber", phoneNumber).apply();
    }

    public boolean isFirstRun() {
        return sharedPreferences.getBoolean("first", true);
    }

    public void setIsFirstRun(boolean yesNo) {
        editor.putBoolean("first", yesNo).apply();
    }


    public void saveUserData(String data) {
        editor.putString("user_data", data).apply();

        try {
            if (data != null) {
                JSONObject user = new JSONObject(data);

                setPhoneNumber(user.getString("mobile"));

            } else {
                Log.e(TAG, "saveUserData: null data " + data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //.apply();
    }

    public void setNightMode(int i) {
        AppCompatDelegate.setDefaultNightMode(i);
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.putInt(Constant.NIGHT_MODE, i);
        edit.apply();
    }

    public int getNightMode() {
        return this.sharedPreferences.getInt(Constant.NIGHT_MODE, AppCompatDelegate.getDefaultNightMode());
    }


    public String getAppLanguage() {
        return sharedPreferences.getString("language", "en");
    }

    public void setAppLanguage(String lang) {
        editor.putString("language", lang).apply();
    }



    public String getUserData() {
        return sharedPreferences.getString("user_data", "");
    }

    public void clearPref() {
        editor.clear().apply();
    }
}
