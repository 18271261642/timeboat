package com.device.rxble.internal.connection;

import android.bluetooth.BluetoothGattCharacteristic;
import androidx.annotation.NonNull;

import com.device.rxble.RxBleConnection;
import com.device.rxble.RxBleDeviceServices;
import com.device.rxble.internal.operations.OperationsProvider;
import com.device.rxble.internal.serialization.ConnectionOperationQueue;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public final class LongWriteOperationBuilderImpl implements RxBleConnection.LongWriteOperationBuilder {

    final ConnectionOperationQueue operationQueue;
    private final RxBleConnection rxBleConnection;
    final OperationsProvider operationsProvider;

    private Single<BluetoothGattCharacteristic> writtenCharacteristicObservable;
    PayloadSizeLimitProvider maxBatchSizeProvider;
    RxBleConnection.WriteOperationAckStrategy writeOperationAckStrategy = new ImmediateSerializedBatchAckStrategy();
    RxBleConnection.WriteOperationRetryStrategy writeOperationRetryStrategy = new NoRetryStrategy();

    byte[] bytes;

    @Inject
    LongWriteOperationBuilderImpl(
            ConnectionOperationQueue operationQueue,
            MtuBasedPayloadSizeLimit defaultMaxBatchSizeProvider,
            RxBleConnection rxBleConnection,
            OperationsProvider operationsProvider
    ) {
        this.operationQueue = operationQueue;
        this.maxBatchSizeProvider = defaultMaxBatchSizeProvider;
        this.rxBleConnection = rxBleConnection;
        this.operationsProvider = operationsProvider;
    }

    @Override
    public RxBleConnection.LongWriteOperationBuilder setBytes(@NonNull byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    @Override
    public RxBleConnection.LongWriteOperationBuilder setCharacteristicUuid(@NonNull final UUID uuid) {
        this.writtenCharacteristicObservable = rxBleConnection.discoverServices().flatMap(new Function<RxBleDeviceServices, SingleSource<
                ? extends BluetoothGattCharacteristic>>() {
            @Override
            public SingleSource<? extends BluetoothGattCharacteristic> apply(RxBleDeviceServices rxBleDeviceServices) throws Exception {
                return rxBleDeviceServices.getCharacteristic(uuid);
            }
        });
        return this;
    }

    @Override
    public RxBleConnection.LongWriteOperationBuilder setCharacteristic(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.writtenCharacteristicObservable = Single.just(bluetoothGattCharacteristic);
        return this;
    }

    @Override
    public RxBleConnection.LongWriteOperationBuilder setMaxBatchSize(final int maxBatchSize) {
        this.maxBatchSizeProvider = new ConstantPayloadSizeLimit(maxBatchSize);
        return this;
    }

    @Override
    public RxBleConnection.LongWriteOperationBuilder setWriteOperationRetryStrategy(
            @NonNull RxBleConnection.WriteOperationRetryStrategy writeOperationRetryStrategy) {
        this.writeOperationRetryStrategy = writeOperationRetryStrategy;
        return this;
    }

    @Override
    public RxBleConnection.LongWriteOperationBuilder setWriteOperationAckStrategy(
            @NonNull RxBleConnection.WriteOperationAckStrategy writeOperationAckStrategy) {
        this.writeOperationAckStrategy = writeOperationAckStrategy;
        return this;
    }

    @Override
    public Observable<byte[]> build() {
        if (writtenCharacteristicObservable == null) {
            throw new IllegalArgumentException("setCharacteristicUuid() or setCharacteristic() needs to be called before build()");
        }

        if (bytes == null) {
            throw new IllegalArgumentException("setBytes() needs to be called before build()");
        }

        // TODO: [DS 24.05.2017] Think about a warning if specified maxBatchSize is greater than MTU

        return writtenCharacteristicObservable.flatMapObservable(new Function<BluetoothGattCharacteristic, Observable<byte[]>>() {
            @Override
            public Observable<byte[]> apply(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                return operationQueue.queue(
                        operationsProvider.provideLongWriteOperation(bluetoothGattCharacteristic,
                                writeOperationAckStrategy, writeOperationRetryStrategy, maxBatchSizeProvider, bytes)
                );
            }
        });
    }
}
