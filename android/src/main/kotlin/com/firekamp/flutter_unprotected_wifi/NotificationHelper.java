package com.firekamp.flutter_unprotected_wifi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.firekamp.flutter_unprotected_wifi.SecuredWifiCheckForegroundService.CHANNEL_ID;

public class NotificationHelper {
    public final static int dissmissableNotificationId = 5;
    public final static int serviceNotificationId = 2;

    Notification getNotification(Context context, UnProtectedWiFiConfiguration unProtectedWiFiConfiguration, String title, String body) {
        createNotificationChannel(context, title);
        Intent notificationIntent = null;
        try {
            notificationIntent = new Intent(context, Class.forName(unProtectedWiFiConfiguration.activityClassName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(unProtectedWiFiConfiguration.notificationResourceId, "drawable", context.getPackageName());

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
//                    .setSmallIcon(resourceId) // TODO: Need to get the appropriate notification icon.
                .setSmallIcon(androidx.appcompat.R.drawable.abc_ic_star_black_48dp)
                .setContentIntent(pendingIntent)
                .build();
        return notification;
    }

    private NotificationManager createNotificationChannel(Context context, String serviceNotificationTitle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    serviceNotificationTitle,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            return manager;
        }
        return null;
    }

    void updateServiceNotification(Context context, UnProtectedWiFiConfiguration unProtectedWiFiConfiguration, String title, String body) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NotificationManager notificationManager = createNotificationChannel(context, title);
            if (notificationManager == null) {
                notificationManager = context.getSystemService(NotificationManager.class);
            }
            final Notification notification = getNotification(context, unProtectedWiFiConfiguration, title, body);
            notificationManager.notify(serviceNotificationId, notification);
        }
    }

    void showNotification(int id, Context context, UnProtectedWiFiConfiguration unProtectedWiFiConfiguration, String title, String body) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NotificationManager notificationManager = createNotificationChannel(context, title);
            if (notificationManager == null) {
                notificationManager = context.getSystemService(NotificationManager.class);
            }
            final Notification notification = getNotification(context, unProtectedWiFiConfiguration, title, body);
            notificationManager.notify(id, notification);
        }
    }

    void showNotification(Context context, Notification notification) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NotificationManager notificationManager = createNotificationChannel(context, "UnProtectedWiFi");
            if (notificationManager == null) {
                notificationManager = context.getSystemService(NotificationManager.class);
            }
            notificationManager.notify(serviceNotificationId, notification);
        }
    }

    void clearNotification(Context context) {
        try {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.cancel(dissmissableNotificationId);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, Objects.requireNonNull(e.getMessage()));
        }
    }
}

