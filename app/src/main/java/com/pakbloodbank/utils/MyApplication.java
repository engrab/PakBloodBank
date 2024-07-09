package com.pakbloodbank.utils;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.pakbloodbank.R;
import com.onesignal.OneSignal;
import com.yariksoffice.lingver.Lingver;
import com.yariksoffice.lingver.store.PreferenceLocaleStore;

import java.io.IOException;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApplication extends Application {


    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_ENGLISH_COUNTRY = "US";
    public static final String LANGUAGE_ARABIC = "ar";
    public static final String LANGUAGE_ARABIC_COUNTRY = "SA";
    public static final String LANGUAGE_URDU = "ur";
    public static final String LANGUAGE_URDU_COUNTRY = "PK";

    private static final String ONESIGNAL_APP_ID = "283e955c-857c-46bc-bf3a-5098039265fb";

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(new PrefManager(getApplicationContext()).getNightMode());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/GIDOLINYA_REGULAR.OTF")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }


        PreferenceLocaleStore store = new PreferenceLocaleStore(this, new Locale(LANGUAGE_ENGLISH));

        Lingver lingver = Lingver.init(this, store);


//        OneSignal.startInit(this)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();



        // Enable verbose OneSignal logging to debug issues if needed.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
