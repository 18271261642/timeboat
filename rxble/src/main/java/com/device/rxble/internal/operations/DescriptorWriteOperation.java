package com.device.rxble.internal.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.device.rxble.exceptions.BleGattOperationType;
import com.device.rxble.internal.SingleResponseOperation;
import com.device.rxble.internal.connection.ConnectionModule;
import com.device.rxble.internal.connection.RxBleGattCallback;


import com.device.rxble.internal.logger.LoggerUtil;

import javax.inject.Named;

import io.reactivex.Single;

import static com.device.rxble.internal.util.ByteAssociationUtil.descriptorPredicate;
import static com.device.rxble.internal.util.ByteAssociationUtil.getBytesFromAssociation;

public class DescriptorWriteOperation extends SingleResponseOperation<byte[]> {

    private final BluetoothGattDescriptor bluetoothGattDescriptor;
    private final byte[] data;
    private final int bluetoothGattCharacteristicDefaultWriteType;

    DescriptorWriteOperation(RxBleGattCallback rxBleGattCallback,
                             BluetoothGatt bluetoothGatt,
                             @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration,
                             int bluetoothGattCharacteristicDefaultWriteType,
                             BluetoothGattDescriptor bluetoothGattDescriptor,
                             byte[] data) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.DESCRIPTOR_WRITE, timeoutConfiguration);
        this.bluetoothGattCharacteristicDefaultWriteType = bluetoothGattCharacteristicDefaultWriteType;
        this.bluetoothGattDescriptor = bluetoothGattDescriptor;
        this.data = data;
    }

    @Override
    protected Single<byte[]> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback
                .getOnDescriptorWrite()
                .filter(descriptorPredicate(bluetoothGattDescriptor))
                .firstOrError()
                .map(getBytesFromAssociation());
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        bluetoothGattDescriptor.setValue(data);

        /*
        * According to the source code below Android 7.0.0 the BluetoothGatt.writeDescriptor() function used
        * writeType of the parent BluetoothCharacteristic which caused operation failure (for instance when
        * setting Client Characteristic Config). With WRITE_TYPE_DEFAULT problem did not occurred.
        * Compare:
        * https://android.googlesource.com/platform/frameworks/base/+/android-6.0.1_r74/core/java/android/bluetooth/BluetoothGatt.java#1039
        * https://android.googlesource.com/platform/frameworks/base/+/android-7.0.0_r1/core/java/android/bluetooth/BluetoothGatt.java#947
        */
        final BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattDescriptor.getCharacteristic();
        final int originalWriteType = bluetoothGattCharacteristic.getWriteType();
        bluetoothGattCharacteristic.setWriteType(bluetoothGattCharacteristicDefaultWriteType);

        final boolean success = bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
        bluetoothGattCharacteristic.setWriteType(originalWriteType);
        return success;
    }

    @Override
    public String toString() {
        return "DescriptorWriteOperation{"
                + super.toString()
                + ", descriptor=" + new LoggerUtil.AttributeLogWrapper(bluetoothGattDescriptor.getUuid(), data, true)
                + '}';
    }
}
