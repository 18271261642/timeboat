package com.device.rxble.internal.connection;


import com.device.rxble.ConnectionSetup;
import com.device.rxble.RxBleConnection;

import io.reactivex.Observable;

public interface Connector {

    Observable<RxBleConnection> prepareConnection(ConnectionSetup autoConnect);
}
