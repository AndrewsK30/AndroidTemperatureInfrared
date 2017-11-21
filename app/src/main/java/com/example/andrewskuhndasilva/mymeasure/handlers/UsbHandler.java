package com.example.andrewskuhndasilva.mymeasure.handlers;

import android.os.Handler;
import android.os.Message;

import com.example.andrewskuhndasilva.mymeasure.MainActivity;
import com.example.andrewskuhndasilva.mymeasure.service.UsbService;

import java.lang.ref.WeakReference;

/**
 * Created by andrewskuhndasilva on 19/11/2017.
 */

public class UsbHandler extends Handler {
    private final WeakReference<MainActivity> mActivity;
    private String mBuffer="";

    public UsbHandler(MainActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case UsbService.MESSAGE_FROM_SERIAL_PORT:
                String data = (String) msg.obj;
                if (data != null && !data.equals("")){
                    MainActivity activity = mActivity.get();
                    mBuffer = mBuffer + data;
                    if(mBuffer.contains("\r\n")) {
                        mBuffer = mBuffer.replace("\r\n","");
                        activity.getTemperature().addTemperature(mBuffer);
                        mBuffer = "";
                    }
                }
                break;
        }
    }
}
