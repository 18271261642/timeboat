package com.device.rxble.internal;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.Nullable;

import com.device.rxble.internal.util.CheckerConnectPermission;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.device.rxble.ConnectionSetup;
import com.device.rxble.RxBleConnection;
import com.device.rxble.RxBleDevice;
import com.device.rxble.Timeout;
import com.device.rxble.exceptions.BleAlreadyConnectedException;
import com.device.rxble.internal.connection.Connector;

import com.device.rxble.internal.logger.LoggerUtil;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.Observable;

@DeviceScope
class RxBleDeviceImpl implements RxBleDevice {

    final BluetoothDevice bluetoothDevice;
    final Connector connector;
    private final BehaviorRelay<RxBleConnection.RxBleConnectionState> connectionStateRelay;
    private final CheckerConnectPermission checkerConnectPermission;
    final AtomicBoolean isConnected = new AtomicBoolean(false);

    @Inject
    RxBleDeviceImpl(
            BluetoothDevice bluetoothDevice,
            Connector connector,
            BehaviorRelay<RxBleConnection.RxBleConnectionState> connectionStateRelay,
            CheckerConnectPermission checkerConnectPermission
    ) {
        this.bluetoothDevice = bluetoothDevice;
        this.connector = connector;
        this.connectionStateRelay = connectionStateRelay;
        this.checkerConnectPermission = checkerConnectPermission;
    }

    @Override
    public Observable<RxBleConnection.RxBleConnectionState> observeConnectionStateChanges() {
        return connectionStateRelay.distinctUntilChanged().skip(1);
    }

    @Override
    public RxBleConnection.RxBleConnectionState getConnectionState() {
        return connectionStateRelay.getValue();
    }

    @Override
    public Observable<RxBleConnection> establishConnection(final boolean autoConnect) {
        ConnectionSetup options = new ConnectionSetup.Builder()
                .setAutoConnect(autoConnect)
                .setSuppressIllegalOperationCheck(true)
                .build();
        return establishConnection(options);
    }

    @Override
    public Observable<RxBleConnection> establishConnection(final boolean autoConnect, final Timeout timeout) {
        ConnectionSetup options = new ConnectionSetup.Builder()
                .setAutoConnect(autoConnect)
                .setOperationTimeout(timeout)
                .setSuppressIllegalOperationCheck(true)
                .build();
        return establishConnection(options);
    }

    public Observable<RxBleConnection> establishConnection(final ConnectionSetup options) {
        return Observable.defer(() -> {
            if (isConnected.compareAndSet(false, true)) {
                return connector.prepareConnection(options)
                        .doFinally(() -> isConnected.set(false));
            } else {
                return Observable.error(new BleAlreadyConnectedException(bluetoothDevice.getAddress()));
            }
        });
    }

    @Override
    @Nullable
    public String getName() {
        return getName(false);
    }

    private String getName(boolean placeholderIfNoPermission) {
        if (placeholderIfNoPermission && !checkerConnectPermission.isConnectRuntimePermissionGranted()) {
            return "[NO BLUETOOTH_CONNECT PERMISSION]";
        }
        return bluetoothDevice.getName();
    }

    @Override
    public String getMacAddress() {
        return bluetoothDevice.getAddress();
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RxBleDeviceImpl)) {
            return false;
        }

        RxBleDeviceImpl that = (RxBleDeviceImpl) o;
        return bluetoothDevice.equals(that.bluetoothDevice);
    }

    @Override
    public int hashCode() {
        return bluetoothDevice.hashCode();
    }

    @Override
    public String toString() {
        return "RxBleDeviceImpl{"
                + LoggerUtil.commonMacMessage(bluetoothDevice.getAddress())
                + ", name=" + getName(true)
                + '}';
    }
}
