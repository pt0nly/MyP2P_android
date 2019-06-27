package com.example.myp2p.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Utility {

    public static String getPath(Uri uri, Context context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null)
            return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String s = cursor.getString(column_index);
        cursor.close();

        return s;
    }

    public static boolean copyFile2(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;

        try {
            while((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("Copy Error: ", e.toString());
            return false;
        }

        return true;
    }

    public static byte[] getBytesFromUri(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        byte[] bytes = null;

        try {
            bytes = getBytesFromStream(iStream);
        } finally {
            try {
                iStream.close();
            } catch (IOException ignored) { }
        }

        return bytes;
    }

    public static byte[] getBytesFromStream(InputStream inputStream) throws IOException {
        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        try {
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            bytesResult = byteBuffer.toByteArray();
        } finally {
            try {
                byteBuffer.close();
            } catch (IOException ignored) { }
        }

        return bytesResult;
    }

    public static byte[] readBytes(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;

        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("TetrisP2P", e.toString());
            return false;
        }

        return true;
    }

    public static byte[] getInputStreamByteArray_2(InputStream input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        try {
            while((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }

            baos.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return baos.toByteArray();
    }

    public static byte[] getInputStreamByteArray(InputStream input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        try {
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }

            baos.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return baos.toByteArray();
    }

    public static String getMyMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface nintf : all) {
                if (!nintf.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nintf.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder sb1 = new StringBuilder();
                for (byte b : macBytes) {
                    String hex = Integer.toHexString(b & 0xFF);

                    if (hex.length() == 1) {
                        hex = "0" + hex;
                    }

                    sb1.append(hex + ":");
                }

                if (sb1.length() > 0) {
                    sb1.deleteCharAt(sb1.length() - 1);
                }

                return sb1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    public static String getMyIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Utility", ex.toString());
        }
        return null;
    }

    public static String getWiFiIPAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        return getDottedDecimalIP(wm.getConnectionInfo().getIpAddress());
    }

    public static String getDottedDecimalIP(int ipAddr) {
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddr = Integer.reverseBytes(ipAddr);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddr).toByteArray();

        return getDottedDecimalIP(ipByteArray);
    }

    public static String getDottedDecimalIP(byte[] ipAddr) {
        String ipAddrStr = "";

        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }

            ipAddrStr += ipAddr[i] & 0xFF;
        }

        return ipAddrStr;
    }

    public static boolean isWifiConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) {
            // WiFi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo != null && wifiInfo.getNetworkId() == -1) {
                // Not connected to an access-Point
                return false;
            }

            // Connected to an Access Point
            return true;
        } else {
            // WiFi adapter is OFF
            return false;
        }
    }

    public static boolean isWiFiEnabled(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.isWifiEnabled();
    }

    public static void requestPermission(String strPermission, int perCode, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, strPermission)) {
            Toast.makeText(activity, "GPS permission allows us to access location data." +
                            " Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{strPermission}, perCode);
        }
    }

    public static boolean checkPermission(String strPermission, Context _c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ContextCompat.checkSelfPermission(_c, strPermission);

            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            }

            return false;
        } else {
            return true;
        }
    }

    public static void deletePersistentGroups(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
        try {
            Method[] methods = WifiP2pManager.class.getMethods();

            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(wifiP2pManager, channel, netid, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearKey(Context cxt, String key) {
        SharedPreferences.Editor prefsEditor = cxt.getSharedPreferences("TetrisP2P", Context.MODE_PRIVATE).edit();
        prefsEditor.remove(key);
        //prefsEditor.commit();
        prefsEditor.apply();
    }

    public static void saveString(Context cxt, String key, String value) {
        SharedPreferences.Editor prefsEditor = cxt.getSharedPreferences("TetrisP2P", Context.MODE_PRIVATE).edit();
        prefsEditor.putString(key, value);
        //prefsEditor.commit();
        prefsEditor.apply();
    }

    public static String getString(Context cxt, String key) {
        SharedPreferences prefs = cxt.getSharedPreferences("TetrisP2P", Context.MODE_PRIVATE);
        String val = prefs.getString(key, null);
        return val;
    }

    public static void saveInt(Context cxt, String key, int value) {
        SharedPreferences.Editor prefsEditor = cxt.getSharedPreferences("TetrisP2P", Context.MODE_PRIVATE).edit();
        prefsEditor.putInt(key, value);
        //prefsEditor.commit();
        prefsEditor.apply();
    }

    public static int getInt(Context cxt, String key) {
        SharedPreferences prefs = cxt.getSharedPreferences("TetrisP2P", Context.MODE_PRIVATE);
        int val = prefs.getInt(key, -1);
        return val;
    }

    public static void saveBool(Context cxt, String key, boolean value) {
        SharedPreferences.Editor prefsEditor = cxt.getSharedPreferences("TetrisP2P", Context.MODE_PRIVATE).edit();
        prefsEditor.putBoolean(key, value);
        //prefsEditor.commit();
        prefsEditor.apply();
    }

    public static boolean getBool(Context cxt, String key) {
        SharedPreferences prefs = cxt.getSharedPreferences("TetrisP2P", Context.MODE_PRIVATE);
        boolean val = prefs.getBoolean(key, false);
        return val;
    }

    public static void clearPreferences(Context cxt) {
        SharedPreferences.Editor prefsEditor = cxt.getSharedPreferences("TetrisP2P", Context
                .MODE_PRIVATE).edit();
        //prefsEditor.clear().commit();
        prefsEditor.apply();
    }

}
