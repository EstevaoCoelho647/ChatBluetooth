package com.project.estevao.chatbluetooth;

import android.app.Application;

/**
 * Created by C1284520 on 30/11/2015.
 */
public class BluetoothManagerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationUtil.setContext(getApplicationContext());
    }
}