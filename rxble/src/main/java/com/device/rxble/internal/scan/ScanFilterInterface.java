package com.device.rxble.internal.scan;

import com.device.rxble.internal.ScanResultInterface;

public interface ScanFilterInterface {

    boolean isAllFieldsEmpty();

    boolean matches(ScanResultInterface scanResult);
}
