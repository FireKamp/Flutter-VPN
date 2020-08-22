package com.firekamp.flutter_unprotected_wifi;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.strongswan.android.logic.VpnStateService;

import io.xdea.flutter_vpn.VpnStateHandler;


public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    private long lastCalledTime;
    private VpnStateService vpnStateService = new VpnStateService();
    private VpnStateService.VpnStateListener listener;
    private UnProtectedWiFiConfiguration unProtectedWiFiConfiguration;

    @Override
    public void onReceive(Context context, Intent intent) {


        final boolean isConnected = isConnected(context);
        NetworkHelper networkHelper = new NetworkHelper();
        NotificationHelper notificationHelper = new NotificationHelper();
        boolean isSecuredWiFi = networkHelper.isSecuredWiFi(context);
        unProtectedWiFiConfiguration = new UnProtectedWiFiConfiguration();
        unProtectedWiFiConfiguration = unProtectedWiFiConfiguration.getSavedConfiguration(context);

        if (VpnStateHandler.INSTANCE.getVpnStateService() != null) {
            VpnStateService.State state = VpnStateHandler.INSTANCE.getVpnStateService().getState();
            if ((state == VpnStateService.State.DISABLED || state == VpnStateService.State.CONNECTED) && isSecuredWiFi && !unProtectedWiFiConfiguration.isAutoConnect) {
                notificationHelper.clearNotification(context);
            }
            if (state == VpnStateService.State.CONNECTED) {
                return;
            } else if (isConnected) {
                SecuredWifiCheckForegroundService.showNotification(context, state, isSecuredWiFi, unProtectedWiFiConfiguration);
            }
            Log.i(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "getVpnStateService " + state);
        } else {
            Log.i(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "getVpnStateService is null");
        }


        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(context);
        final boolean lastConnectedStatus = sharedPreferenceHelper.GetLastConnectionStatus();
        if (lastConnectedStatus == isConnected) {
            if (!SecuredWifiCheckForegroundService.connect)
                return;
        }
        sharedPreferenceHelper.SaveLastConnectionStatus(isConnected);
        if (isConnected) {

            if (unProtectedWiFiConfiguration == null) {
                Log.i(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "NetworkChangeBroadcastReceiver UnProtectedWiFiPlugin is not configured" + isConnected);
                return;
            }
            Log.i(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "getVpnStateService  new line");


            if (VpnStateHandler.INSTANCE.getVpnStateService() != null) {
                vpnStateService = VpnStateHandler.INSTANCE.getVpnStateService();
            }


            if (!isSecuredWiFi && unProtectedWiFiConfiguration.isAutoConnect) {
                if (System.currentTimeMillis() - lastCalledTime <= 500) return;
                lastCalledTime = System.currentTimeMillis();

                Bundle profileInfo = FlutterUnprotectedWifiPlugin.getConfigurationBundle(context);
                vpnStateService.connect(profileInfo, true, context);
            }
            Log.i(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "NetworkChangeBroadcastReceiver isSecuredWiFi: " + isSecuredWiFi);
        }
        Log.i(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "NetworkChangeBroadcastReceiver isConnected: " + isConnected);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
