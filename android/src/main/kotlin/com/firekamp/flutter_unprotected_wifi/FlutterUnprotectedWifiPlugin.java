package com.firekamp.flutter_unprotected_wifi;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import io.xdea.flutter_vpn.VpnStateHandler;

public class FlutterUnprotectedWifiPlugin {
    public final static String PLUGIN_TAG = "UnProtectedWiFiPlugin";

    public static Bundle getConfigurationBundle(Context context) {
        Bundle profileInfo = new Bundle();
        try {
            UnProtectedWiFiConfiguration unProtectedWiFiConfiguration = new UnProtectedWiFiConfiguration();
            unProtectedWiFiConfiguration = unProtectedWiFiConfiguration.getSavedConfiguration(context);
            profileInfo.putString("Address", unProtectedWiFiConfiguration.address);
            profileInfo.putString("PackageName", unProtectedWiFiConfiguration.packageName);
            profileInfo.putString("ActivityName", unProtectedWiFiConfiguration.activityClassName);
            profileInfo.putString("UserName", unProtectedWiFiConfiguration.userName);
            profileInfo.putString("DisplayName", unProtectedWiFiConfiguration.displayName);
            profileInfo.putString("Password", unProtectedWiFiConfiguration.password);
            profileInfo.putString("VpnType", unProtectedWiFiConfiguration.vpnType);
            profileInfo.putInt("MTU", unProtectedWiFiConfiguration.mtu);
        } catch (Exception e) {
            Log.e(PLUGIN_TAG, e.getMessage());
        }
        return profileInfo;
    }
}
