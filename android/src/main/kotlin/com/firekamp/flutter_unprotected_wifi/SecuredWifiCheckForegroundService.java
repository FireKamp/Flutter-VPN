package com.firekamp.flutter_unprotected_wifi;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.strongswan.android.logic.VpnStateService;

import java.util.Objects;

import io.xdea.flutter_vpn.VpnStateHandler;

import static com.firekamp.flutter_unprotected_wifi.NotificationHelper.serviceNotificationId;

public class SecuredWifiCheckForegroundService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static boolean connect = false;
    private NetworkChangeBroadcastReceiver mConnectivityReceiver;
    private VpnStateService.VpnStateListener listener;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UnProtectedWiFiConfiguration unProtectedWiFiConfiguration = new UnProtectedWiFiConfiguration();
        unProtectedWiFiConfiguration = unProtectedWiFiConfiguration.getSavedConfiguration(getApplicationContext());
        if (unProtectedWiFiConfiguration == null) {
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "UnProtectedPlugin is not initialized");
        } else {
            final VpnStateService service = VpnStateHandler.INSTANCE.getVpnStateService();
            NetworkHelper networkHelper = new NetworkHelper();
            final Notification notification = getNotification(getApplicationContext(), service == null ? VpnStateService.State.DISABLED : service.getState(), networkHelper.isSecuredWiFi(getApplicationContext()), unProtectedWiFiConfiguration);
            if (notification != null) {
                startForeground(serviceNotificationId, notification);
                if (mConnectivityReceiver != null) {
                    unregisterReceiver(mConnectivityReceiver);
                }
                mConnectivityReceiver = new NetworkChangeBroadcastReceiver();
                registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }

        }
        registerListeners();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mConnectivityReceiver);
        unregisterListeners();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerListeners() {
        try {
            final VpnStateService service = VpnStateHandler.INSTANCE.getVpnStateService();

            listener = () -> {
                if (service.getState() != null && (service.getState() == VpnStateService.State.DISABLED || service.getState() == VpnStateService.State.CONNECTED)) {
                    NetworkHelper networkHelper = new NetworkHelper();
                    NotificationHelper notificationHelper = new NotificationHelper();
                    boolean isSecuredWiFi = networkHelper.isSecuredWiFi(getApplicationContext());
                    UnProtectedWiFiConfiguration unProtectedWiFiConfiguration = new UnProtectedWiFiConfiguration();
                    unProtectedWiFiConfiguration = unProtectedWiFiConfiguration.getSavedConfiguration(getApplicationContext());
                    if (unProtectedWiFiConfiguration != null) {
                        showNotification(getApplicationContext(), service.getState(), isSecuredWiFi, unProtectedWiFiConfiguration);
                    }
                }
                Log.i(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "State Changed " + VpnStateHandler.INSTANCE.getVpnStateService().getState());
            };

            if (service != null) {
                service.registerListener(listener);
            }
        } catch (Exception err) {
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "registerListeners" + err.getMessage());
        }
    }

    private void unregisterListeners() {
        final VpnStateService service = VpnStateHandler.INSTANCE.getVpnStateService();
        try {
            if (service != null && listener != null) {
                service.unregisterListener(listener);
            }
        } catch (Exception e) {
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "unregisterListeners" + e.getMessage());
        }
    }

    static Notification getNotification(Context context, VpnStateService.State state, boolean isSecured, UnProtectedWiFiConfiguration unProtectedWiFiConfiguration) {
        if (unProtectedWiFiConfiguration == null) return null;
        String title = unProtectedWiFiConfiguration.serviceNotificationTitle;
        String body = unProtectedWiFiConfiguration.serviceNotificationBody;
        NotificationHelper notificationHelper = new NotificationHelper();
        if (unProtectedWiFiConfiguration.isAutoConnect) {
            title = unProtectedWiFiConfiguration.autoConnectTitle;
            body = unProtectedWiFiConfiguration.autoConnectBody;
        } else if (unProtectedWiFiConfiguration.isWarn) {
            if (isSecured || state == VpnStateService.State.CONNECTED) {
                title = unProtectedWiFiConfiguration.serviceNotificationTitle;
                body = unProtectedWiFiConfiguration.serviceNotificationBody;
            } else {
                title = unProtectedWiFiConfiguration.warnTitle;
                body = unProtectedWiFiConfiguration.warnBody;
            }
        }
        return notificationHelper.getNotification(context, unProtectedWiFiConfiguration, title, body);
    }


    public static void showNotification(Context context, VpnStateService.State state, boolean isSecured, UnProtectedWiFiConfiguration unProtectedWiFiConfiguration) {
        try {
            NotificationHelper notificationHelper = new NotificationHelper();
            final Notification notification = getNotification(context, state, isSecured, unProtectedWiFiConfiguration);
            notificationHelper.showNotification(context, notification);
        } catch (Exception e) {
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, "unregisterListeners" + e.getMessage());
        }
    }

    public static void startService(Context context, boolean connect) {
        try {
            Intent serviceIntent = new Intent(context, SecuredWifiCheckForegroundService.class);
            SecuredWifiCheckForegroundService.connect = connect;
            ContextCompat.startForegroundService(context, serviceIntent);
            //scheduleOnReboot(context, true);
        } catch (Exception e) {
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    public static void stopService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, SecuredWifiCheckForegroundService.class);
            context.getApplicationContext().stopService(serviceIntent);
            NotificationHelper notificationHelper = new NotificationHelper();
            notificationHelper.clearNotification(context);
            //scheduleOnReboot(context, false);

            //Clear Configuration
            UnProtectedWiFiConfiguration unprotectedWiFlutterVpnPlugin = new UnProtectedWiFiConfiguration();
            unprotectedWiFlutterVpnPlugin.clearConfiguration(context);
        } catch (Exception e) {
            Log.e(FlutterUnprotectedWifiPlugin.PLUGIN_TAG, Objects.requireNonNull(e.getMessage()));
        }
    }


    private static void scheduleOnReboot(Context context, boolean enable) {
        ComponentName receiver = new ComponentName(context, RebootBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
