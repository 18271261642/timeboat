package com.device.rxble.internal.operations;

import android.bluetooth.BluetoothGatt;
import androidx.annotation.RequiresApi;

import com.device.rxble.exceptions.BleGattOperationType;
import com.device.rxble.internal.SingleResponseOperation;
import com.device.rxble.internal.connection.RxBleGattCallback;


import javax.inject.Inject;

import io.reactivex.Single;

@RequiresApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
public class MtuRequestOperation extends SingleResponseOperation<Integer> {

    private final int mtu;

    @Inject
    MtuRequestOperation(
            RxBleGattCallback rxBleGattCallback,
            BluetoothGatt bluetoothGatt,
            TimeoutConfiguration timeoutConfiguration, int requestedMtu) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.ON_MTU_CHANGED, timeoutConfiguration);
        mtu = requestedMtu;
    }

    @Override
    protected Single<Integer> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback.getOnMtuChanged().firstOrError();
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        return bluetoothGatt.requestMtu(mtu);
    }

    @Override
    public String toString() {
        return "MtuRequestOperation{"
                + super.toString()
                + ", mtu=" + mtu
                + '}';
    }
}
