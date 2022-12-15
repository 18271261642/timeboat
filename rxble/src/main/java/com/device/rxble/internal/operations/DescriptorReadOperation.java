package com.device.rxble.internal.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.device.rxble.exceptions.BleGattOperationType;
import com.device.rxble.internal.SingleResponseOperation;
import com.device.rxble.internal.connection.ConnectionModule;
import com.device.rxble.internal.connection.RxBleGattCallback;
import com.device.rxble.internal.logger.LoggerUtil;
import com.device.rxble.internal.util.ByteAssociation;


import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;

import static com.device.rxble.internal.util.ByteAssociationUtil.descriptorPredicate;

public class DescriptorReadOperation extends SingleResponseOperation<ByteAssociation<BluetoothGattDescriptor>> {

    private final BluetoothGattDescriptor bluetoothGattDescriptor;

    @Inject
    DescriptorReadOperation(RxBleGattCallback rxBleGattCallback, BluetoothGatt bluetoothGatt,
                            @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration,
                            BluetoothGattDescriptor descriptor) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.DESCRIPTOR_READ, timeoutConfiguration);
        bluetoothGattDescriptor = descriptor;
    }

    @Override
    protected Single<ByteAssociation<BluetoothGattDescriptor>> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback
                .getOnDescriptorRead()
                .filter(descriptorPredicate(bluetoothGattDescriptor))
                .firstOrError();
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        return bluetoothGatt.readDescriptor(bluetoothGattDescriptor);
    }

    @Override
    public String toString() {
        return "DescriptorReadOperation{"
                + super.toString()
                + ", descriptor=" + LoggerUtil.wrap(bluetoothGattDescriptor, false)
                + '}';
    }
}
