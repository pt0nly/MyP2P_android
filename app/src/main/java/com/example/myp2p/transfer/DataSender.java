package com.example.myp2p.transfer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.myp2p.model.ChatDTO;
import com.example.myp2p.model.DeviceDTO;
import com.example.myp2p.utils.ConnectionUtils;
import com.example.myp2p.utils.Utility;

public class DataSender {
    public static void sendData(Context context, String destIP, int destPort, ITransferable data) {
        Intent serviceIntent = new Intent(context,
                DataTransferService.class);
        serviceIntent.setAction(DataTransferService.ACTION_SEND_DATA);
        serviceIntent.putExtra(
                DataTransferService.DEST_IP_ADDRESS, destIP);
        serviceIntent.putExtra(
                DataTransferService.DEST_PORT_NUMBER, destPort);

        serviceIntent.putExtra(DataTransferService.EXTRAS_SHARE_DATA, data);
        context.startService(serviceIntent);
    }

    public static void sendFile(Context context, String destIP, int destPort, Uri fileUri) {
        Intent serviceIntent = new Intent(context, DataTransferService.class);
        serviceIntent.setAction(DataTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(DataTransferService.DEST_IP_ADDRESS, destIP);
        serviceIntent.putExtra(DataTransferService.DEST_PORT_NUMBER, destPort);
        serviceIntent.putExtra(DataTransferService.EXTRAS_FILE_PATH, fileUri.toString());
        //serviceIntent.putExtra(DataTransferService.EXTRAS_FILE_PATH, Utility.getPath(fileUri, context) );

        context.startService(serviceIntent);
    }

    public static void sendCurrentDeviceData(Context context, String destIP, int destPort, boolean isRequest) {
        DeviceDTO currentDevice = new DeviceDTO();
        currentDevice.setPort(ConnectionUtils.getPort(context));
        String playerName = Utility.getString(context, TransferConstants.KEY_USER_NAME);
        if (playerName != null) {
            currentDevice.setPlayerName(playerName);
        }
        currentDevice.setIp(Utility.getString(context, TransferConstants.KEY_MY_IP));

        ITransferable transferData = null;
        if (!isRequest) {
            transferData = TransferModelGenerator.generateDeviceTransferModelResponse
                    (currentDevice);
        } else {
            transferData = TransferModelGenerator.generateDeviceTransferModelRequest
                    (currentDevice);
        }

        sendData(context, destIP, destPort, transferData);
    }

    public static void sendCurrentDeviceDataWD(Context context, String destIP, int destPort, boolean isRequest) {
        DeviceDTO currentDevice = new DeviceDTO();
        currentDevice.setPort(ConnectionUtils.getPort(context));
        String playerName = Utility.getString(context, TransferConstants.KEY_USER_NAME);
        if (playerName != null) {
            currentDevice.setPlayerName(playerName);
        }
        currentDevice.setIp(Utility.getString(context, TransferConstants.KEY_MY_IP));

        ITransferable transferData = null;
        if (!isRequest) {
            transferData = TransferModelGenerator.generateDeviceTransferModelResponseWD
                    (currentDevice);
        } else {
            transferData = TransferModelGenerator.generateDeviceTransferModelRequestWD(currentDevice);
        }

        sendData(context, destIP, destPort, transferData);
    }

    public static void sendChatRequest(Context context, String destIP, int destPort) {
        DeviceDTO currentDevice = new DeviceDTO();
        currentDevice.setPort(ConnectionUtils.getPort(context));
        String playerName = Utility.getString(context, TransferConstants.KEY_USER_NAME);
        if (playerName != null) {
            currentDevice.setPlayerName(playerName);
        }
        currentDevice.setIp(Utility.getString(context, TransferConstants.KEY_MY_IP));
        ITransferable transferData = TransferModelGenerator.generateChatRequestModel(currentDevice);
        sendData(context, destIP, destPort, transferData);
    }

    public static void sendChatResponse(Context context, String destIP, int destPort, boolean isAccepted) {
        DeviceDTO currentDevice = new DeviceDTO();
        currentDevice.setPort(ConnectionUtils.getPort(context));
        String playerName = Utility.getString(context, TransferConstants.KEY_USER_NAME);
        if (playerName != null) {
            currentDevice.setPlayerName(playerName);
        }
        currentDevice.setIp(Utility.getString(context, TransferConstants.KEY_MY_IP));
        ITransferable transferData = TransferModelGenerator.generateChatResponseModel(currentDevice, isAccepted);
        sendData(context, destIP, destPort, transferData);
    }

    public static void sendChatInfo(Context context, String destIP, int destPort, ChatDTO chat) {
        ITransferable transferableData = TransferModelGenerator.generateChatTransferModel(chat);
        sendData(context, destIP, destPort, transferableData);
    }
}
