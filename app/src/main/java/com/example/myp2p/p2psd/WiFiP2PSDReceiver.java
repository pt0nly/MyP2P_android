
package com.example.myp2p.p2psd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

import com.example.myp2p.LocalDashWiFiP2PSD;
import com.example.myp2p.notification.NotificationToast;

public class WiFiP2PSDReceiver extends BroadcastReceiver {

    private static final String TAG = "WiFiP2PSD";

    private WifiP2pManager manager;
    private Channel channel;
    private Activity activity;

    public WiFiP2PSDReceiver(WifiP2pManager manager, Channel channel, Activity activity) {
        super();

        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, action);

        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // Just connected to other device
                Log.d(TAG, "Connected to p2p network. Requesting network details");
                NotificationToast.showToast(activity, "Connected to p2p network. Requesting network details");
                manager.requestConnectionInfo(channel, (ConnectionInfoListener) activity);
            } else {
                // Disconnecting
                NotificationToast.showToast(activity, "SD.onReceive: Disconnecting");
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

            Log.d(TAG, "Device status -" + device.status);
            NotificationToast.showToast(activity, "Device status -" + device.status);

            manager.requestConnectionInfo(channel, (ConnectionInfoListener) activity);
        }
    }
}
