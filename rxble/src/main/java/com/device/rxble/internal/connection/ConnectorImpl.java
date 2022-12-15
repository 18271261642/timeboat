package com.device.rxble.internal.connection;

import android.bluetooth.BluetoothGatt;

import com.device.rxble.ClientComponent;
import com.device.rxble.ConnectionSetup;
import com.device.rxble.RxBleConnection;
import com.device.rxble.internal.serialization.ClientOperationQueue;

import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class ConnectorImpl implements Connector {

    private final ClientOperationQueue clientOperationQueue;
    final ConnectionComponent.Builder connectionComponentBuilder;
    final Scheduler callbacksScheduler;

    @Inject
    public ConnectorImpl(
            ClientOperationQueue clientOperationQueue,
            ConnectionComponent.Builder connectionComponentBuilder,
            @Named(ClientComponent.NamedSchedulers.BLUETOOTH_CALLBACKS) Scheduler callbacksScheduler) {
        this.clientOperationQueue = clientOperationQueue;
        this.connectionComponentBuilder = connectionComponentBuilder;
        this.callbacksScheduler = callbacksScheduler;
    }

    @Override
    public Observable<RxBleConnection> prepareConnection(final ConnectionSetup options) {
        return Observable.defer(() -> {
            final ConnectionComponent connectionComponent = connectionComponentBuilder
                    .autoConnect(options.autoConnect)
                    .suppressOperationChecks(options.suppressOperationCheck)
                    .operationTimeout(options.operationTimeout)
                    .build();

            final Set<ConnectionSubscriptionWatcher> connSubWatchers = connectionComponent.connectionSubscriptionWatchers();
            return obtainRxBleConnection(connectionComponent)
                    .mergeWith(observeDisconnections(connectionComponent))
                    .delaySubscription(enqueueConnectOperation(connectionComponent))
                    .doOnSubscribe(disposable -> {
                        for (ConnectionSubscriptionWatcher csa : connSubWatchers) {
                            csa.onConnectionSubscribed();
                        }
                    })
                    .doFinally(() -> {
                        for (ConnectionSubscriptionWatcher csa : connSubWatchers) {
                            csa.onConnectionUnsubscribed();
                        }
                    })
                    .subscribeOn(callbacksScheduler)
                    .unsubscribeOn(callbacksScheduler);
        });
    }

    static Observable<RxBleConnection> obtainRxBleConnection(final ConnectionComponent connectionComponent) {
        // BluetoothGatt is needed for RxBleConnection
        // BluetoothGatt is produced by RxBleRadioOperationConnect
        return Observable.fromCallable(connectionComponent::rxBleConnection);
    }

    static Observable<RxBleConnection> observeDisconnections(ConnectionComponent connectionComponent) {
        return connectionComponent.gattCallback().observeDisconnect();
    }

    Observable<BluetoothGatt> enqueueConnectOperation(ConnectionComponent connectionComponent) {
        return clientOperationQueue.queue(connectionComponent.connectOperation());
    }
}
