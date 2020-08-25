package com.firekamp.flutter_unprotected_wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class NetworkHelper {

    public boolean isSecuredWiFi(Context context) {
        boolean isSecured = false;
        try {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> networkList = wifi.getScanResults();
            WifiInfo wi = wifi.getConnectionInfo();
            String currentSSID = wi.getSSID();
            if (networkList != null) {
                for (ScanResult network : networkList) {
                    if (currentSSID.replace("\"", "").equals(network.SSID)) {
                        String capabilities = network.capabilities;
                        Log.d(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, network.SSID + " capabilities : " + capabilities);
                        if (capabilities.contains("WPA2") || capabilities.contains("WPA") || capabilities.contains("WEP")) {
                            isSecured = true;
                        } else {
                            isSecured = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, e.getMessage());
        }
        return isSecured;
    }
}
