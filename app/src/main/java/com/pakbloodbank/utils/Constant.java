package com.pakbloodbank.utils;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pakbloodbank.BuildConfig;
import com.pakbloodbank.items.CityModel;
import com.pakbloodbank.items.ItemAbout;
import com.pakbloodbank.items.ItemBlog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.RectangularBounds;

import java.util.ArrayList;

public class Constant {

    public static final String TAG = "BloodBank";


    public static final int NUM_OF_COLUMNS = 2;
    // Gridview image padding
    public static final int GRID_PADDING = 3; // in dp
    private static final long serialVersionUID = 1L;
    public static ItemAbout itemAbout;
    public static int columnWidth = 0;
    public static int columnHeight = 0;
    public static Boolean isBannerAd = true, isInterAd = true;

    public static final String NIGHT_MODE = "NIGHT_MODE";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";





    public static int adShow = 5;
    public static int adCount = 0;

    public static int showAdAfterCount = 4;
    public static int adsCount = 0;

    //    public static FirebaseAuth fireBaseAuth;
    public static boolean check_admob = true;
    public static String selectedBgroup = "";
    public static String searchString = "";
    public static String selectedCity = "";

    public static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(-7.216001d, 0.0d), new LatLng(0.0d, 107.903316d));
    public static final RectangularBounds RECT_BOUNDS = RectangularBounds.newInstance(BOUNDS);


    public static ArrayList<CityModel> citiesArrayList = new ArrayList<>();
    public static double curr_latitude = 0.0, curr_longitude = 0.0;
    public static ItemBlog itemBlog;
    public static boolean isAlertShowing = false;


    public static void showNoNetwork(final Context context) {
        try {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("OK", (dialog, which) -> ((AppCompatActivity) context).onBackPressed());
            alertDialog.show();
        } catch (Exception e) {
            Log.d(Constant.TAG, "Show Dialog: " + e.getMessage());
        }
    }


    public static ArrayList<String> getBloodGroups() {
        ArrayList<String> groups = new ArrayList<>();
        groups.add("A+");
        groups.add("B+");
        groups.add("A-");
        groups.add("B-");
        groups.add("AB+");
        groups.add("AB-");
        groups.add("O+");
        groups.add("O-");
        return groups;
    }

    public static void asdf(String response) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, "asdf: " + response);
    }
}
