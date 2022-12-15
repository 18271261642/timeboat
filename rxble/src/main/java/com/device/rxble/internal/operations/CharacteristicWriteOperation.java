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

public class CharacteristicWriteOperation extends SingleResponseOperation<byte[]> {

    private final BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private final byte[] data;

    CharacteristicWriteOperation(RxBleGattCallback rxBleGattCallback, BluetoothGatt bluetoothGatt,
                                 @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration,
                                 BluetoothGattCharacteristic bluetoothGattCharacteristic,
                                 byte[] data) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.CHARACTERISTIC_WRITE, timeoutConfiguration);
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        this.data = data;
    }

    @Override
    protected Single<byte[]> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback
                .getOnCharacteristicWrite()
                .filter(characteristicUUIDPredicate(bluetoothGattCharacteristic.getUuid()))
                .firstOrError()
                .map(getBytesFromAssociation());
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        bluetoothGattCharacteristic.setValue(data);
        return bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

    @Override
    public String toString() {
        return "CharacteristicWriteOperation{"
                + super.toString()
                + ", characteristic=" + new LoggerUtil.AttributeLogWrapper(bluetoothGattCharacteristic.getUuid(), data, true)
                + '}';
    }
}
