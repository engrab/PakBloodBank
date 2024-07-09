package com.pakbloodbank.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.pakbloodbank.R;
//import com.onesignal.NotificationExtenderService;
//import com.onesignal.OSNotificationReceivedResult;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;


import com.onesignal.OSNotification;
import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler;

import org.json.JSONObject;


public class NotificationExtenderExample
        implements OSRemoteNotificationReceivedHandler {

    private static final int    NOTIFICATION_ID = 1;

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        OSNotification notification = notificationReceivedEvent.getNotification();

        // Example of modifying the notification's accent color
        OSMutableNotification mutableNotification = notification.mutableCopy();
        mutableNotification.setExtender(builder -> {
            // Sets the accent color to Green on Android 5+ devices.
            // Accent color controls icon and action buttons on Android 5+. Accent color does not change app title on Android 10+
            builder.setColor(new BigInteger("FF00FF00", 16).intValue());
            // Sets the notification Title to Red
            Spannable spannableTitle = new SpannableString(notification.getTitle());
            spannableTitle.setSpan(new ForegroundColorSpan(Color.RED),0,notification.getTitle().length(),0);
            builder.setContentTitle(spannableTitle);
            // Sets the notification Body to Blue
            Spannable spannableBody = new SpannableString(notification.getBody());
            spannableBody.setSpan(new ForegroundColorSpan(Color.BLUE),0,notification.getBody().length(),0);
            builder.setContentText(spannableBody);


//            title      = notification.getTitle();
//            message    = notification.getBody();
//            bigpicture = notification.getBigPicture();


//            NotificationChannel mChannel;
            String              NOTIFICATION_CHANNEL_ID = "bloodbank_ch_1";
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                CharSequence name       = "BloodBank Notifications";// The user-visible name of the channel.
//                int          importance = NotificationManager.IMPORTANCE_HIGH;
//                mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
//                mNotificationManager.createNotificationChannel(mChannel);
//            }

            if (notification.getBigPicture() != null) {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(notification.getBigPicture())).setSummaryText(notification.getBody()));
            } else {
                builder.setContentText(notification.getBody());
            }

            Uri           uri           = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            builder.setAutoCancel(true)
                    .setSound(uri)
                    .setAutoCancel(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setLights(Color.RED, 800, 800);



            builder.setSmallIcon(getNotificationIcon(builder));

            builder.setContentTitle(notification.getTitle());
            builder.setTicker(notification.getBody());



//            bi.setContentIntent(contentIntent);
//            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

            //Force remove push from Notification Center after 30 seconds
//            builder.setTimeoutAfter(30000);
            return builder;
        });
        JSONObject data = notification.getAdditionalData();
        Log.i("OneSignalExample", "Received Notification Data: " + data);


        // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
        // To omit displaying a notification, pass `null` to complete()
        notificationReceivedEvent.complete(mutableNotification);
    }
//        NotificationExtenderService {

//    private              String message;
//    private              String bigpicture;
//    private              String title;
//    private              String cid;
//    private              String cname;
//    private              String url;

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL               url        = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

//    @Override
//    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
//
//        title      = receivedResult.payload.title;
//        message    = receivedResult.payload.body;
//        bigpicture = receivedResult.payload.bigPicture;

//        try {
//            cid   = receivedResult.payload.additionalData.getString("cat_id");
//            cname = receivedResult.payload.additionalData.getString("cat_name");
//            url   = receivedResult.payload.additionalData.getString("external_link");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Log.e("asdf", "cid: " + cid  + " name: " + cname + " url : " + url);

//        sendNotification();
//        return true;
//    }

//    private void sendNotification() {
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent intent;

//        if (!url.trim().isEmpty()) {
////        if (cid.equals("0") && !url.equals("false") && !url.trim().isEmpty()) {
//            intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(url));
//        } else {
//            intent = new Intent(this, SplashActivity.class);
//            intent.putExtra("cid", cid);
//            intent.putExtra("cname", cname);
//        }

//        NotificationChannel mChannel;
//        String              NOTIFICATION_CHANNEL_ID = "bloodbank_ch_1";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name       = "BloodBank Notifications";// The user-visible name of the channel.
//            int          importance = NotificationManager.IMPORTANCE_HIGH;
//            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
//            mNotificationManager.createNotificationChannel(mChannel);
//        }

//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Uri           uri           = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "blood_bank_channel_01")
//                .setAutoCancel(true)
//                .setSound(uri)
//                .setAutoCancel(true)
//                .setChannelId(NOTIFICATION_CHANNEL_ID)
//                .setLights(Color.RED, 800, 800);

//        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));
//
//        mBuilder.setContentTitle(title);
//        mBuilder.setTicker(message);
//
//        if (bigpicture != null) {
//            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)).setSummaryText(message));
//        } else {
//            mBuilder.setContentText(message);
//        }
//
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
            return R.mipmap.ic_launcher;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    private int getColour() {
        return 0x3F51B5;
    }
}