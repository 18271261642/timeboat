package com.device.rxble.internal;

import android.bluetooth.BluetoothDevice;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.device.rxble.ClientComponent;
import com.device.rxble.ClientComponent.NamedSchedulers;
import com.device.rxble.RxBleConnection;
import com.device.rxble.RxBleDevice;
import com.device.rxble.internal.connection.ConnectionComponent;
import com.device.rxble.internal.connection.ConnectionStateChangeListener;
import com.device.rxble.internal.connection.Connector;
import com.device.rxble.internal.connection.ConnectorImpl;
import com.device.rxble.internal.operations.TimeoutConfiguration;
import com.device.rxble.internal.util.RxBleAdapterWrapper;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;

@Module(subcomponents = ConnectionComponent.class)
public abstract class DeviceModule {

    public static final String MAC_ADDRESS = "mac-address";
    public static final String OPERATION_TIMEOUT = "operation-timeout";
    public static final String DISCONNECT_TIMEOUT = "disconnect-timeout";
    public static final String CONNECT_TIMEOUT = "connect-timeout";

    private static final int DEFAULT_OPERATION_TIMEOUT = 30;
    private static final int DEFAULT_DISCONNECT_TIMEOUT = 10;
    private static final int DEFAULT_CONNECT_TIMEOUT = 35;

    @Provides
    static BluetoothDevice provideBluetoothDevice(@Named(MAC_ADDRESS) String macAddress, RxBleAdapterWrapper adapterWrapper) {
        return adapterWrapper.getRemoteDevice(macAddress);
    }

    @Provides
    @Named(CONNECT_TIMEOUT)
    static TimeoutConfiguration providesConnectTimeoutConf(@Named(ClientComponent.NamedSchedulers.TIMEOUT) Scheduler timeoutScheduler) {
        return new TimeoutConfiguration(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS, timeoutScheduler);
    }

    @Provides
    @Named(DISCONNECT_TIMEOUT)
    static TimeoutConfiguration providesDisconnectTimeoutConf(@Named(NamedSchedulers.TIMEOUT) Scheduler timeoutScheduler) {
        return new TimeoutConfiguration(DEFAULT_DISCONNECT_TIMEOUT, TimeUnit.SECONDS, timeoutScheduler);
    }

    @Provides
    @DeviceScope
    static BehaviorRelay<RxBleConnection.RxBleConnectionState> provideConnectionStateRelay() {
        return BehaviorRelay.createDefault(RxBleConnection.RxBleConnectionState.DISCONNECTED);
    }

    @Provides
    @DeviceScope
    static ConnectionStateChangeListener provideConnectionStateChangeListener(
            final BehaviorRelay<RxBleConnection.RxBleConnectionState> connectionStateBehaviorRelay
    ) {
        return new ConnectionStateChangeListener() {
            @Override
            public void onConnectionStateChange(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
                connectionStateBehaviorRelay.accept(rxBleConnectionState);
            }
        };
    }

    @Binds
    abstract Connector bindConnector(ConnectorImpl rxBleConnectionConnector);

    @Binds
    abstract RxBleDevice bindDevice(RxBleDeviceImpl rxBleDevice);
}
