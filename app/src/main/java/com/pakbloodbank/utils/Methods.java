package com.pakbloodbank.utils;

import static com.pakbloodbank.utils.Constant.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pakbloodbank.R;
import com.pakbloodbank.activities.SplashActivity;
import com.yariksoffice.lingver.Lingver;


public class Methods {

    private final Context context;


    // constructor
    public Methods(Context context) {
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getScreenWidth(Context ctx) {
        int columnWidth;

        final Point point = new Point();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((AppCompatActivity) ctx).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        point.x = displaymetrics.widthPixels;
        point.y = displaymetrics.heightPixels;

        columnWidth = point.x;
        return columnWidth;
    }






    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }


    public static boolean check_internet(Context context) {
        ConnectionDetector cd = new ConnectionDetector(context);
        return cd.isConnectingToInternet();
    }

    public static boolean check_gps(Context context) {
        GPSTracker gps = new GPSTracker(context);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            try {

                Constant.curr_latitude = gps.getLatitude();
                Constant.curr_longitude = gps.getLongitude();

                gps.stopUsingGPS();

            } catch (NullPointerException e) {

            } catch (NumberFormatException e) {

            }
        } else {
            if (!Constant.isAlertShowing) {
                gps.showSettingsAlert();
            }
        }
        return gps.canGetLocation();
    }

    public static void openGoogleMaps(Context context, String latitude, String longitude) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }


}