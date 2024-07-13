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
        editor = sharedPreferences.edit();
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




    public String getUserData() {
        return sharedPreferences.getString("user_data", "");
    }

    public void clearPref() {
        editor.clear().apply();
    }

    public void setFullName(String name) {
        editor.putString("name", name).apply();
    }

    public String getFullName() {
        return sharedPreferences.getString("name", "");
    }
    public void setAddress(String address) {
        editor.putString("address", address).apply();
    }

    public String getAddress() {
        return sharedPreferences.getString("address", "");
    }

    public void setCountry(String country) {
        editor.putString("country", country).apply();
    }

    public String getCountry() {
        return sharedPreferences.getString("country", "");
    }

    public void setState(String state) {
        editor.putString("state", state).apply();
    }

    public String getState() {
        return sharedPreferences.getString("state", "");
    }

    public void setCity(String city) {
        editor.putString("city", city).apply();
    }

    public String getCity() {
        return sharedPreferences.getString("city", "");
    }

    public void setBloodGroup(String bloodGroup) {
        editor.putString("bloodGroup", bloodGroup).apply();
    }

    public String getBloodGroup() {
        return sharedPreferences.getString("bloodGroup", "");
    }
    public void setDob(String dob) {
        editor.putString("dob", dob).apply();
    }

    public String getDob() {
        return sharedPreferences.getString("dob", "");
    }
}
