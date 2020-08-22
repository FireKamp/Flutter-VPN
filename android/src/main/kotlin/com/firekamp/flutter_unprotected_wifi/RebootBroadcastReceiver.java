package com.firekamp.flutter_unprotected_wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RebootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "Rescheduling after boot!");
            //TODO:Need to implement this one
            //SecuredWifiCheckForegroundService.startService(context);
        }
    }
}