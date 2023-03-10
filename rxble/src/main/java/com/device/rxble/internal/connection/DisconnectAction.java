package com.device.rxble.internal.connection;

import com.device.rxble.internal.operations.DisconnectOperation;
import com.device.rxble.internal.serialization.ClientOperationQueue;

import javax.inject.Inject;

import io.reactivex.internal.functions.Functions;

@ConnectionScope
class DisconnectAction implements ConnectionSubscriptionWatcher {

    private final ClientOperationQueue clientOperationQueue;
    private final DisconnectOperation operationDisconnect;

    @Inject
    DisconnectAction(ClientOperationQueue clientOperationQueue, DisconnectOperation operationDisconnect) {
        this.clientOperationQueue = clientOperationQueue;
        this.operationDisconnect = operationDisconnect;
    }

    @Override
    public void onConnectionSubscribed() {
        // do nothing
    }

    @Override
    public void onConnectionUnsubscribed() {
        clientOperationQueue
                .queue(operationDisconnect)
                .subscribe(
                        Functions.emptyConsumer(),
                        Functions.emptyConsumer()
                );
    }
}
