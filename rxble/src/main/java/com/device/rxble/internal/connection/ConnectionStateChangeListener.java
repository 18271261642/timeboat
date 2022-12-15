package com.device.rxble.internal.connection;


import com.device.rxble.RxBleConnection;

public interface ConnectionStateChangeListener {

    void onConnectionStateChange(RxBleConnection.RxBleConnectionState rxBleConnectionState);
}
