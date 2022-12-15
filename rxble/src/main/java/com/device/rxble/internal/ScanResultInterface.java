package com.device.rxble.internal;

import com.device.rxble.scan.ScanCallbackType;
import com.device.rxble.scan.ScanRecord;

public interface ScanResultInterface {
    /**
     * Get the address from the device
     */
    String getAddress();

    /**
     * Get the device name from the device (not from scan record)
     */
    String getDeviceName();

    /**
     * Get the RSSI of the scan result
     */
    int getRssi();

    /**
     * Get the scan record
     */
    ScanRecord getScanRecord();

    /**
     * Get the timestamp the scan result was produced
     */
    long getTimestampNanos();

    /**
     * Get the type of scan callback
     */
    ScanCallbackType getScanCallbackType();
}
