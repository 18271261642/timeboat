package com.device.rxble.internal.scan;


import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

import com.device.rxble.internal.operations.ScanOperationApi21;
import com.device.rxble.internal.util.RxBleAdapterWrapper;
import com.device.rxble.scan.ScanFilter;
import com.device.rxble.scan.ScanSettings;


import javax.inject.Inject;

import io.reactivex.ObservableTransformer;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class ScanSetupBuilderImplApi21 implements ScanSetupBuilder {

    private final RxBleAdapterWrapper rxBleAdapterWrapper;
    private final InternalScanResultCreator internalScanResultCreator;
    private final ScanSettingsEmulator scanSettingsEmulator;
    private final AndroidScanObjectsConverter androidScanObjectsConverter;

    @Inject
    ScanSetupBuilderImplApi21(
            RxBleAdapterWrapper rxBleAdapterWrapper,
            InternalScanResultCreator internalScanResultCreator,
            ScanSettingsEmulator scanSettingsEmulator,
            AndroidScanObjectsConverter androidScanObjectsConverter
    ) {
        this.rxBleAdapterWrapper = rxBleAdapterWrapper;
        this.internalScanResultCreator = internalScanResultCreator;
        this.scanSettingsEmulator = scanSettingsEmulator;
        this.androidScanObjectsConverter = androidScanObjectsConverter;
    }

    @RequiresApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    @Override
    public ScanSetup build(ScanSettings scanSettings, ScanFilter... scanFilters) {
        /*
         Android 5.0 (API21) does not handle FIRST_MATCH and / or MATCH_LOST callback type
         https://developer.android.com/reference/android/bluetooth/le/ScanSettings.Builder.html#setCallbackType(int)
          */
        final ObservableTransformer<RxBleInternalScanResult, RxBleInternalScanResult> callbackTypeTransformer
                = scanSettingsEmulator.emulateCallbackType(scanSettings.getCallbackType());
        return new ScanSetup(
                new ScanOperationApi21(
                        rxBleAdapterWrapper,
                        internalScanResultCreator,
                        androidScanObjectsConverter,
                        scanSettings,
                        new EmulatedScanFilterMatcher(scanFilters),
                        null),
                callbackTypeTransformer
        );
    }
}
