package com.device.rxble.internal.scan;


import android.bluetooth.le.ScanResult;

import com.device.rxble.scan.IsConnectable;

import androidx.annotation.RestrictTo;



@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface IsConnectableChecker {
    IsConnectable check(ScanResult scanResult);
}
