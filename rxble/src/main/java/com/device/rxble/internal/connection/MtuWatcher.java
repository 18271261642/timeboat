package com.device.rxble.internal.connection;


import com.device.rxble.exceptions.BleGattException;
import com.device.rxble.exceptions.BleGattOperationType;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.Functions;

@ConnectionScope
class MtuWatcher implements ConnectionSubscriptionWatcher, MtuProvider, Consumer<Integer> {

    private Integer currentMtu;
    private final Observable<Integer> mtuObservable;
    private final SerialDisposable serialSubscription = new SerialDisposable();

    @Inject
    MtuWatcher(
            final RxBleGattCallback rxBleGattCallback,
            @Named(ConnectionComponent.NamedInts.GATT_MTU_MINIMUM) final int initialValue
    ) {
        this.mtuObservable = rxBleGattCallback.getOnMtuChanged()
                .retry(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) {
                        return throwable instanceof BleGattException
                                && ((BleGattException) throwable).getBleGattOperationType() == BleGattOperationType.ON_MTU_CHANGED;
                    }
                });
        this.currentMtu = initialValue;
    }

    @Override
    public int getMtu() {
        return currentMtu;
    }

    @Override
    public void onConnectionSubscribed() {
        serialSubscription.set(mtuObservable.subscribe(this,
                // ignoring error, it is expected when the connection is lost.
                Functions.emptyConsumer()));
    }

    @Override
    public void onConnectionUnsubscribed() {
        serialSubscription.dispose();
    }

    @Override
    public void accept(Integer newMtu) {
        this.currentMtu = newMtu;
    }
}
