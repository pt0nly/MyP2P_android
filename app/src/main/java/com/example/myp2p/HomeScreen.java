package com.example.myp2p;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.net.wifi.WifiManager;

import com.example.myp2p.db.DBAdapter;
import com.example.myp2p.notification.NotificationToast;
import com.example.myp2p.transfer.TransferConstants;
import com.example.myp2p.utils.ConnectionUtils;
import com.example.myp2p.utils.Utility;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class HomeScreen extends AppCompatActivity {

    public static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int WRITE_PERM_REQ_CODE = 19;

    static WifiManager wifiManager;

    EditText etUsername;
    TextView tvPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);

        etUsername = (EditText) findViewById(R.id.et_home_player_name);
        tvPort = (TextView) findViewById(R.id.tv_port_info);

        String userNameHint = getString(R.string.enter_name_hint) + " (default = " + Build.MANUFACTURER + ")";
        etUsername.setHint(userNameHint);

        checkWritePermission();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        checkWifiEnabled(this);

        printInterfaces();
    }

    public static void checkWifiEnabled(Context mContext) {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            NotificationToast.showToast(mContext, "Wifi Disabled. Enabling WiFi");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            NotificationToast.showToast(HomeScreen.this, "This permission is needed for file sharing.");
            finish();
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onResume() {
        super.onResume();

        checkWifiEnabled(this);

        DBAdapter.getInstance(HomeScreen.this).clearDatabase();
        tvPort.setText(String.format(getString(R.string.port_info), ConnectionUtils.getPort(HomeScreen.this)));
    }

    private void printInterfaces() {
        try {
            Enumeration<NetworkInterface> x = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface ni : Collections.list(x)) {
                Log.v("NetWorkInterfaces", "display name: " + ni.getDisplayName());
                Log.v("NetWorkInterfaces", "name: " + ni.getName());
                Log.v("NetWorkInterfaces", "is up and running ? : " + String.valueOf(ni.isUp()));
                Log.v("NetWorkInterfaces", "Loopback?: " + String.valueOf(ni.isLoopback()));
                Log.v("NetWorkInterfaces", "Supports multicast: " + String.valueOf(ni.supportsMulticast()));
                Log.v("NetWorkInterfaces", "is virtual: " + String.valueOf(ni.isVirtual()));
                Log.v("NetWorkInterfaces", "Hardware address: " + Arrays.toString(ni.getHardwareAddress()));
                Log.v("NetWorkInterfaces", "Sub interfaces.....");

                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();

                for (InetAddress singleNI : Collections.list(inetAddresses)) {
                    Log.v("NetWorkInterfaces", "sub ni inetaddress: " + singleNI.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void saveUsername() {
        String userName = etUsername.getText().toString();

        if (userName != null && userName.trim().length() > 0) {
            Utility.saveString(HomeScreen.this, TransferConstants.KEY_USER_NAME, userName);
        }
    }

    private void checkWritePermission() {
        boolean isGranted = Utility.checkPermission(WRITE_PERMISSION, this);

        if (!isGranted) {
            Utility.requestPermission(WRITE_PERMISSION, WRITE_PERM_REQ_CODE, this);
        }
    }

    public void startWiFiDirect(View v) {
        if (Utility.isWiFiEnabled(HomeScreen.this)) {
            saveUsername();

            Intent wifiDirectIntent = new Intent(HomeScreen.this, LocalDashWiFiDirect.class);
            startActivity(wifiDirectIntent);

            finish();
        } else {
            NotificationToast.showToast(HomeScreen.this, getString(R.string.wifi_not_enabled_error));
        }
    }

    public void startWiFiDirectServiceDiscovery(View v) {
        if (Utility.isWiFiEnabled(HomeScreen.this)) {
            saveUsername();

            Intent wifiDirectServiceDiscoveryIntent = new Intent(HomeScreen.this, LocalDashWiFiP2PSD.class);
            startActivity(wifiDirectServiceDiscoveryIntent);

            finish();
        } else {
            NotificationToast.showToast(HomeScreen.this, getString(R.string.wifi_not_enabled_error));
        }
    }
}
