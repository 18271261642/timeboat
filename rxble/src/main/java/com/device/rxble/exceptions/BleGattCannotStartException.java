package com.device.rxble.exceptions;

import android.bluetooth.BluetoothGatt;

/**
 * An exception emitted from {@link com.device.rxble.RxBleConnection} functions when the underlying {@link BluetoothGatt}
 * returns `false` from {@link BluetoothGatt#readRemoteRssi()} or other functions associated with device interaction.
 */
public class BleGattCannotStartException extends BleGattException {

    @Deprecated
    public BleGattCannotStartException(BleGattOperationType bleGattOperationType) {
        super(null, bleGattOperationType);
    }

    public BleGattCannotStartException(BluetoothGatt gatt, BleGattOperationType bleGattOperationType) {
        super(gatt, bleGattOperationType);
    }
}
