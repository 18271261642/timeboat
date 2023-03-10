package com.device.rxble.internal.connection;

import android.bluetooth.BluetoothGattCharacteristic;

import com.device.rxble.internal.BleIllegalOperationException;
import com.device.rxble.internal.BluetoothGattCharacteristicProperty;


import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.functions.Action;

/**
 * Class for checking whether the requested operation is legal on chosen characteristic.
 */
public class IllegalOperationChecker {

    final IllegalOperationHandler resultHandler;

    @Inject
    public IllegalOperationChecker(IllegalOperationHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    /**
     * This method checks whether the supplied characteristic possesses properties supporting the requested kind of operation, specified by
     * the supplied bitmask.
     *
     * Emits {@link BleIllegalOperationException} if there was no match between supported and necessary properties of characteristic and
     * check has not been suppressed
     *
     * @param characteristic   a {@link BluetoothGattCharacteristic} the operation is done on
     * @param neededProperties properties required for the operation to be successfully completed
     * @return {@link Completable} deferring execution of the check till subscription
     */
    public Completable checkAnyPropertyMatches(final BluetoothGattCharacteristic characteristic,
                                               final @BluetoothGattCharacteristicProperty int neededProperties) {
        return Completable.fromAction(new Action() {
            public void run() {
                final int characteristicProperties = characteristic.getProperties();

                if ((characteristicProperties & neededProperties) == 0) {
                    BleIllegalOperationException exception = resultHandler.handleMismatchData(characteristic, neededProperties);
                    if (exception != null) {
                        throw exception;
                    }
                }
            }
        });
    }
}