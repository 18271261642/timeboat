package com.device.rxble.internal.scan;


import com.device.rxble.exceptions.BleScanException;

public interface ScanPreconditionsVerifier {

    void verify(boolean checkLocationProviderState) throws BleScanException;
}
