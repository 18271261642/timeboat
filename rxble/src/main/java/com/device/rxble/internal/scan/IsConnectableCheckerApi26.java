package com.device.rxble.internal.scan;

import android.bluetooth.le.ScanResult;

import com.device.rxble.scan.IsConnectable;

import javax.inject.Inject;

import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;


@RequiresApi(26 /* Build.VERSION_CODES.O */)
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class IsConnectableCheckerApi26 implements IsConnectableChecker {

    @Inject
    public IsConnectableCheckerApi26() {
    }

    @Override
    public IsConnectable check(ScanResult scanResult) {
        return scanResult.isConnectable() ? IsConnectable.CONNECTABLE : IsConnectable.NOT_CONNECTABLE;
    }
}
