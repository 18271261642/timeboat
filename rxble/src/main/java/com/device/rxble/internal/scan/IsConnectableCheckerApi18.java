package com.device.rxble.internal.scan;

import android.bluetooth.le.ScanResult;

import com.device.rxble.scan.IsConnectable;

import javax.inject.Inject;

import androidx.annotation.RestrictTo;


@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class IsConnectableCheckerApi18 implements IsConnectableChecker {

    @Inject
    public IsConnectableCheckerApi18() {
    }

    @Override
    public IsConnectable check(ScanResult scanResult) {
        return IsConnectable.LEGACY_UNKNOWN;
    }
}
