package com.firekamp.flutter_unprotected_wifi;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Objects;

public class UnProtectedWiFiConfiguration {


    String appName;
    String notificationResourceId;
    String serviceNotificationTitle;
    String serviceNotificationBody;
    String warnTitle;
    String warnBody;
    String autoConnectTitle;
    String autoConnectBody;
    String activityClassName;
    String packageName;
    String userName;
    String displayName;
    String password;
    String address;
    String vpnType;
    int mtu;
    boolean isAutoConnect;
    boolean isWarn;


    public UnProtectedWiFiConfiguration(String appName,
                                        String notificationResourceId,
                                        String serviceNotificationTitle,
                                        String serviceNotificationBody,
                                        String warnTitle,
                                        String warnBody,
                                        String autoConnectTitle,
                                        String autoConnectBody,
                                        String activityClassName,
                                        String packageName,
                                        String userName,
                                        String displayName,
                                        String password,
                                        String address,
                                        String vpnType,
                                        int mtu,
                                        boolean isAutoConnect,
                                        boolean isWarn) {
        this.appName = appName;
        this.notificationResourceId = notificationResourceId;
        this.serviceNotificationTitle = serviceNotificationTitle;
        this.serviceNotificationBody = serviceNotificationBody;
        this.warnTitle = warnTitle;
        this.warnBody = warnBody;
        this.autoConnectTitle = autoConnectTitle;
        this.autoConnectBody = autoConnectBody;
        this.activityClassName = activityClassName;
        this.packageName = packageName;
        this.userName = userName;
        this.displayName = displayName;
        this.password = password;
        this.address = address;
        this.vpnType = vpnType;
        this.mtu = mtu;
        this.isAutoConnect = isAutoConnect;
        this.isWarn = isWarn;
    }

    public UnProtectedWiFiConfiguration() {

    }


    public UnProtectedWiFiConfiguration getSavedConfiguration(Context context) {
        UnProtectedWiFiConfiguration unProtectedWiFiConfiguration = null;
        try {
            SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(context);
            Gson gson = new Gson();
            String json = sharedPreferenceHelper.GetConfiguration();
            if (json != null && !json.isEmpty()) {
                unProtectedWiFiConfiguration = gson.fromJson(json, UnProtectedWiFiConfiguration.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "getSavedConfiguration" + Objects.requireNonNull(e.getMessage()));
        }
        return unProtectedWiFiConfiguration;
    }

    public void savedConfiguration(UnProtectedWiFiConfiguration unProtectedWiFiConfiguration, Context context) {
        try {
            SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(context);
            Gson gson = new Gson();
            String json = gson.toJson(unProtectedWiFiConfiguration);
            sharedPreferenceHelper.SaveConfiguration(json);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "savedConfiguration" + Objects.requireNonNull(e.getMessage()));
        }
    }

    public void clearConfiguration(Context context) {
        try {
            SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(context);
            sharedPreferenceHelper.clearConfiguration();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "clearConfiguration" + Objects.requireNonNull(e.getMessage()));
        }
    }
}
