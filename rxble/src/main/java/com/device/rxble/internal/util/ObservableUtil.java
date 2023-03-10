package com.device.rxble.internal.util;


import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

public class ObservableUtil {

    private static final ObservableTransformer<?, ?> IDENTITY_TRANSFORMER
            = new ObservableTransformer<Object, Object>() {
        @Override
        public Observable<Object> apply(Observable<Object> rxBleInternalScanResultObservable) {
            return rxBleInternalScanResultObservable;
        }
    };

    private ObservableUtil() {

    }

    public static <T> Observable<T> justOnNext(T onNext) {
        return Observable.<T>never().startWith(onNext);
    }

    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> identityTransformer() {
        return (ObservableTransformer<T, T>) IDENTITY_TRANSFORMER;
    }
}
