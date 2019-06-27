package com.example.myp2p.transfer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.service.autofill.Dataset;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.example.myp2p.AppController;
import com.example.myp2p.BuildConfig;
import com.example.myp2p.HomeScreen;
import com.example.myp2p.LocalDashWiFiDirect;
import com.example.myp2p.LocalDashWiFiP2PSD;
import com.example.myp2p.notification.NotificationToast;
import com.example.myp2p.utils.DialogUtils;
import com.example.myp2p.utils.Utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener extends Thread {

    private int mPort;
    private Context mContext;
    private ServerSocket mServer;

    private boolean acceptRequests = true;

    public ConnectionListener(Context context, int port) {
        this.mContext = context;
        this.mPort = port;
    }

    @Override
    public void run() {
        try {
            Log.d("TetrisP2P", Build.MANUFACTURER + ": conn listener: " + mPort);
            mServer = new ServerSocket(mPort);
            mServer.setReuseAddress(true);

            if (mServer != null && !mServer.isBound()) {
                mServer.bind(new InetSocketAddress(mPort));
            }

            Log.d("TetrisP2P", "Inet4Address: " + Inet4Address.getLocalHost().getHostAddress());

            Socket socket = null;
            while (acceptRequests) {
                // this is a blocking operation
                socket = mServer.accept();
                handleData(socket.getInetAddress().getHostAddress(), socket.getInputStream());
            }

            Log.e("TetrisP2P", Build.MANUFACTURER + ": Connection listener terminated. acceptRequests: " + acceptRequests);
            socket.close();
            socket = null;

        } catch (IOException e) {
            Log.e("TetrisP2P", Build.MANUFACTURER + ": Connection listener EXCEPTION. " + e.toString());
            e.printStackTrace();
        }
    }

    private void handleData(String senderIP, InputStream inputStream) {
        try {
            byte[] input = Utility.getInputStreamByteArray(inputStream);

            ObjectInput oin = null;

            try {
                Log.d("TetrisP2P", "Bytes to receive: [" + input.length + "]");

                oin = new ObjectInputStream(new ByteArrayInputStream(input));
                ITransferable transferObject = (ITransferable) oin.readObject();

                // Processing incoming data
                (new DataHandler(mContext, senderIP, transferObject)).process();

                oin.close();
                return;

            } catch (ClassNotFoundException cnfe) {
                Log.e("TetrisP2P", cnfe.toString());
                cnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (oin != null) {
                    oin.close();
                }
            }


            // Processing it as a file (JPEG)
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + "/myp2p/" + System.currentTimeMillis() + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists()) {
                boolean dirsSuccess = dirs.mkdirs();
            }
            boolean fileCreationSuccess = f.createNewFile();


            Log.e("TetrisP2P", "Saving file");
            Utility.copyFile(new ByteArrayInputStream(input), new FileOutputStream(f));
            Log.e("TetrisP2P", "File saved");


            // Openning the received file. (if exists)
            if (f.exists() && f.length() > 0) {
                Log.e("TetrisP2P", "File exists, and is not empty");

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                boolean errorOpenning = false;

                if (Build.VERSION.SDK_INT >= 24) {
                    // Must provide
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    try {
                        Uri photoUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", f);

                        intent.setData(photoUri);
                    } catch (Exception ex) {
                        errorOpenning = true;
                        ex.printStackTrace();
                    }

                } else {
                    try {
                        intent.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()), "image/*");
                    } catch (Exception ex) {
                        errorOpenning = true;
                        ex.printStackTrace();
                    }
                }

                if (!errorOpenning)
                    mContext.startActivity(intent);
                else
                    NotificationToast.showToast(mContext, "Error trying to open image. Please check folder 'external storage/myp2p'");


            } else {
                Log.e("TetrisP2P", "File doesn't exist or is empty");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tearDown() {
        acceptRequests = false;
    }
}
