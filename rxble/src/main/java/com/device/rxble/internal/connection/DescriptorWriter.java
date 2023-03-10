package com.device.rxble.internal.connection;


import android.bluetooth.BluetoothGattDescriptor;

import com.device.rxble.internal.operations.OperationsProvider;
import com.device.rxble.internal.serialization.ConnectionOperationQueue;

import javax.inject.Inject;

import io.reactivex.Completable;

@ConnectionScope
class DescriptorWriter {

    private final ConnectionOperationQueue operationQueue;
    private final OperationsProvider operationsProvider;

    @Inject
    DescriptorWriter(ConnectionOperationQueue operationQueue, OperationsProvider operationsProvider) {
        this.operationQueue = operationQueue;
        this.operationsProvider = operationsProvider;
    }

    Completable writeDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor, byte[] data) {
        return operationQueue.queue(operationsProvider.provideWriteDescriptor(bluetoothGattDescriptor, data))
                .ignoreElements();
    }
}
