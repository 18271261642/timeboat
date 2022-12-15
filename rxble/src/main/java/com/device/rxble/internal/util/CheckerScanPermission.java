package com.device.rxble.internal.util;



import com.device.rxble.ClientComponent;
import com.device.rxble.ClientScope;

import javax.inject.Inject;
import javax.inject.Named;

@ClientScope
public class CheckerScanPermission {

    private final CheckerPermission checkerPermission;
    private final String[][] scanPermissions;

    @Inject
    CheckerScanPermission(
            CheckerPermission checkerPermission,
            @Named(ClientComponent.PlatformConstants.STRING_ARRAY_SCAN_PERMISSIONS) String[][] scanPermissions
    ) {
        this.checkerPermission = checkerPermission;
        this.scanPermissions = scanPermissions;
    }

    public boolean isScanRuntimePermissionGranted() {
        boolean allNeededPermissionsGranted = true;
        for (String[] neededPermissions : scanPermissions) {
            allNeededPermissionsGranted &= checkerPermission.isAnyPermissionGranted(neededPermissions);
        }
        return allNeededPermissionsGranted;
    }

    public String[] getRecommendedScanRuntimePermissions() {
        int allPermissionsCount = 0;
        for (String[] permissionsArray : scanPermissions) {
            allPermissionsCount += permissionsArray.length;
        }
        String[] resultPermissions = new String[allPermissionsCount];
        int i = 0;
        for (String[] permissionsArray : scanPermissions) {
            for (String permission : permissionsArray) {
                resultPermissions[i++] = permission;
            }
        }
        return resultPermissions;
    }
}
