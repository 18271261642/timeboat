package com.device.rxble.internal.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.device.rxble.exceptions.BleGattOperationType;
import com.device.rxble.internal.SingleResponseOperation;
import com.device.rxble.internal.connection.ConnectionModule;
import com.device.rxble.internal.connection.RxBleGattCallback;
import com.device.rxble.internal.logger.LoggerUtil;


import javax.inject.Named;

import io.reactivex.Single;

import static com.device.rxble.internal.util.ByteAssociationUtil.characteristicUUIDPredicate;
import static com.device.rxble.internal.util.ByteAssociationUtil.getBytesFromAssociation;

public class CharacteristicReadOperation extends SingleResponseOperation<byte[]> {

    private final BluetoothGattCharacteristic bluetoothGattCharacteristic;

    CharacteristicReadOperation(RxBleGattCallback rxBleGattCallback, BluetoothGatt bluetoothGatt,
                                @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration,
                                BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.CHARACTERISTIC_READ, timeoutConfiguration);
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
    }

    @Override
    protected Single<byte[]> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback
                .getOnCharacteristicRead()
                .filter(characteristicUUIDPredicate(bluetoothGattCharacteristic.getUuid()))
                .firstOrError()
                .map(getBytesFromAssociation());
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        return bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
    }

    @Override
    public String toString() {
        return "CharacteristicReadOperation{"
                + super.toString()
                + ", characteristic=" + LoggerUtil.wrap(bluetoothGattCharacteristic, false)
                + '}';
    }
}
