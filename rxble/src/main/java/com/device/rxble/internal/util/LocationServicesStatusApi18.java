package com.device.rxble.internal.util;


import javax.inject.Inject;

public class LocationServicesStatusApi18 implements LocationServicesStatus {

    @Inject
    LocationServicesStatusApi18() {

    }

    public boolean isLocationPermissionOk() {
        return true;
    }

    public boolean isLocationProviderOk() {
        return true;
    }
}
