package com.device.rxble.internal;

import static com.device.rxble.internal.DeviceModule.MAC_ADDRESS;

import com.device.rxble.RxBleDevice;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Subcomponent;


@DeviceScope
@Subcomponent(modules = {DeviceModule.class})
public interface DeviceComponent {

    @Subcomponent.Builder
    interface Builder {
        DeviceComponent build();

        @BindsInstance
        Builder macAddress(@Named(MAC_ADDRESS) String deviceMacAddress);
    }

    @DeviceScope
    RxBleDevice provideDevice();
}
