package com.device.rxble.internal.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.os.DeadObjectException;
import androidx.annotation.RestrictTo;

import com.device.rxble.ClientComponent;
import com.device.rxble.RxBleConnection;
import com.device.rxble.exceptions.BleDisconnectedException;
import com.device.rxble.exceptions.BleException;
import com.device.rxble.internal.DeviceModule;
import com.device.rxble.internal.QueueOperation;
import com.device.rxble.internal.RxBleLog;
import com.device.rxble.internal.connection.BluetoothGattProvider;
import com.device.rxble.internal.connection.ConnectionStateChangeListener;
import com.device.rxble.internal.connection.RxBleGattCallback;
import com.device.rxble.internal.logger.LoggerUtil;
import com.device.rxble.internal.serialization.QueueReleaseInterface;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Emitter;
import io.reactivex.ObservableEmitter;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.device.rxble.RxBleConnection.RxBleConnectionState.DISCONNECTED;
import static com.device.rxble.RxBleConnection.RxBleConnectionState.DISCONNECTING;

public class DisconnectOperation extends QueueOperation<Void> {

    private final RxBleGattCallback rxBleGattCallback;
    private final BluetoothGattProvider bluetoothGattProvider;
    private final String macAddress;
    private final BluetoothManager bluetoothManager;
    private final Scheduler bluetoothInteractionScheduler;
    private final TimeoutConfiguration timeoutConfiguration;
    private final ConnectionStateChangeListener connectionStateChangeListener;

    @Inject
    DisconnectOperation(
            RxBleGattCallback rxBleGattCallback,
            BluetoothGattProvider bluetoothGattProvider,
            @Named(DeviceModule.MAC_ADDRESS) String macAddress,
            BluetoothManager bluetoothManager,
            @Named(ClientComponent.NamedSchedulers.BLUETOOTH_INTERACTION) Scheduler bluetoothInteractionScheduler,
            @Named(DeviceModule.DISCONNECT_TIMEOUT) TimeoutConfiguration timeoutConfiguration,
            ConnectionStateChangeListener connectionStateChangeListener) {
        this.rxBleGattCallback = rxBleGattCallback;
        this.bluetoothGattProvider = bluetoothGattProvider;
        this.macAddress = macAddress;
        this.bluetoothManager = bluetoothManager;
        this.bluetoothInteractionScheduler = bluetoothInteractionScheduler;
        this.timeoutConfiguration = timeoutConfiguration;
        this.connectionStateChangeListener = connectionStateChangeListener;
    }

    @Override
    protected void protectedRun(final ObservableEmitter<Void> emitter, final QueueReleaseInterface queueReleaseInterface) {
        connectionStateChangeListener.onConnectionStateChange(DISCONNECTING);
        final BluetoothGatt bluetoothGatt = bluetoothGattProvider.getBluetoothGatt();
        if (bluetoothGatt == null) {
            RxBleLog.w("Disconnect operation has been executed but GATT instance was null - considering disconnected.");
            considerGattDisconnected(emitter, queueReleaseInterface);
        } else {
            disconnectIfRequired(bluetoothGatt)
                    .observeOn(bluetoothInteractionScheduler)
                    .subscribe(new SingleObserver<BluetoothGatt>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            // not used
                        }

                        @Override
                        public void onSuccess(BluetoothGatt bluetoothGatt) {
                            bluetoothGatt.close();
                            considerGattDisconnected(emitter, queueReleaseInterface);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            RxBleLog.w(
                                    throwable,
                                    "Disconnect operation has been executed but finished with an error - considering disconnected."
                            );
                            considerGattDisconnected(emitter, queueReleaseInterface);
                        }
                    });
        }
    }

    private Single<BluetoothGatt> disconnectIfRequired(BluetoothGatt bluetoothGatt) {
        return isDisconnected(bluetoothGatt)
                ? Single.just(bluetoothGatt)
                : disconnect(bluetoothGatt);
    }

    @SuppressWarnings("WeakerAccess")
    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    void considerGattDisconnected(
            final Emitter<Void> emitter,
            final QueueReleaseInterface queueReleaseInterface
    ) {
        connectionStateChangeListener.onConnectionStateChange(DISCONNECTED);
        queueReleaseInterface.release();
        emitter.onComplete();
    }

    private boolean isDisconnected(BluetoothGatt bluetoothGatt) {
        return bluetoothManager.getConnectionState(bluetoothGatt.getDevice(), BluetoothProfile.GATT) == BluetoothProfile.STATE_DISCONNECTED;
    }

    /**
     * TODO: [DS] 09.02.2016 This operation makes the queue to block until disconnection - maybe it would be better if it would not?
     * What would happen then if a consecutive call to BluetoothDevice.connectGatt() would be made? What BluetoothGatt would be returned?
     * 1. A completely fresh BluetoothGatt - would work with the current flow
     * 2. The same BluetoothGatt - in this situation we should probably cancel the pending BluetoothGatt.close() call
     */
    private Single<BluetoothGatt> disconnect(BluetoothGatt bluetoothGatt) {
        return new DisconnectGattObservable(bluetoothGatt, rxBleGattCallback, bluetoothInteractionScheduler)
                .timeout(timeoutConfiguration.timeout, timeoutConfiguration.timeoutTimeUnit, timeoutConfiguration.timeoutScheduler,
                        Single.just(bluetoothGatt));
    }

    private static class DisconnectGattObservable extends Single<BluetoothGatt> {

        final BluetoothGatt bluetoothGatt;
        private final RxBleGattCallback rxBleGattCallback;
        private final Scheduler disconnectScheduler;

        DisconnectGattObservable(BluetoothGatt bluetoothGatt, RxBleGattCallback rxBleGattCallback, Scheduler disconnectScheduler) {
            this.bluetoothGatt = bluetoothGatt;
            this.rxBleGattCallback = rxBleGattCallback;
            this.disconnectScheduler = disconnectScheduler;
        }

        @Override
        protected void subscribeActual(SingleObserver<? super BluetoothGatt> observer) {
            rxBleGattCallback
                    .getOnConnectionStateChange()
                    .filter(new Predicate<RxBleConnection.RxBleConnectionState>() {
                        @Override
                        public boolean test(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
                            return rxBleConnectionState == DISCONNECTED;
                        }
                    })
                    .firstOrError()
                    .map(new Function<RxBleConnection.RxBleConnectionState, BluetoothGatt>() {
                        @Override
                        public BluetoothGatt apply(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
                            return bluetoothGatt;
                        }
                    })
                    .subscribe(observer);
            disconnectScheduler.createWorker().schedule(new Runnable() {
                @Override
                public void run() {
                    bluetoothGatt.disconnect();
                }
            });
        }
    }

    @Override
    protected BleException provideException(DeadObjectException deadObjectException) {
        return new BleDisconnectedException(deadObjectException, macAddress, BleDisconnectedException.UNKNOWN_STATUS);
    }

    @Override
    public String toString() {
        return "DisconnectOperation{"
                + LoggerUtil.commonMacMessage(macAddress)
                + '}';
    }
}
