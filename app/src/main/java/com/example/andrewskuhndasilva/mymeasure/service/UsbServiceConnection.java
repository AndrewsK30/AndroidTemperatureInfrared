package com.example.andrewskuhndasilva.mymeasure.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.andrewskuhndasilva.mymeasure.handlers.UsbHandler;

import lombok.Getter;

/**
 * Created by andrewskuhndasilva on 19/11/2017.
 */

public class UsbServiceConnection implements ServiceConnection {

    @Getter
    private UsbService mUsbService;
    private UsbHandler mHandler;

    public UsbServiceConnection(UsbHandler handler){
        mHandler = handler;
    }
    @Override
    public void onServiceConnected(ComponentName arg0, IBinder arg1) {
        mUsbService = ((UsbService.UsbBinder) arg1).getService();
        mUsbService.setHandler(mHandler);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        mUsbService = null;
    }

    public void sendMessage(String string){
        if(mUsbService != null) mUsbService.write(string.getBytes());
    }

}
