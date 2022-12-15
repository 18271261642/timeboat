package com.device.rxble.internal.scan;


import androidx.annotation.RestrictTo;
import com.device.rxble.scan.ScanFilter;
import com.device.rxble.scan.ScanSettings;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface ScanSetupBuilder {

    ScanSetup build(ScanSettings scanSettings, ScanFilter... scanFilters);
}
