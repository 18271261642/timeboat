package com.blala.blalable;

import android.app.Application;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.BluetoothContext;

import org.litepal.LitePalApplication;

/**
 * Created by Admin
 * Date 2021/9/3
 */
public class BleApplication  {

    private static BleApplication bleApplication;
    private static BleManager bleManager;
    private static Application mApp;


    public static void initBleApplication(Application application){
        BluetoothContext.set(application);
       mApp = application;
    }


    public static BleApplication getInstance(){
        if(bleApplication == null){
            bleApplication = new BleApplication();
        }
        return bleApplication;
    }

    public BleManager getBleManager() {
        if (bleManager == null) {
            bleManager = BleManager.getInstance(mApp);
        }

        return bleManager;
    }
}
