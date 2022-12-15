package com.device.rxble.internal.connection;

import com.device.rxble.RxBleConnection;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class NoRetryStrategy implements RxBleConnection.WriteOperationRetryStrategy {

    @Override
    public Observable<LongWriteFailure> apply(Observable<LongWriteFailure> observable) {
        return observable.flatMap(new Function<LongWriteFailure, Observable<LongWriteFailure>>() {
            @Override
            public Observable<LongWriteFailure> apply(LongWriteFailure longWriteFailure) {
                return Observable.error(longWriteFailure.getCause());
            }
        });
    }
}
