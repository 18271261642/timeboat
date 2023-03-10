package com.device.rxble.internal.util;


import com.device.rxble.ClientComponent;
import com.device.rxble.ClientScope;

import javax.inject.Inject;
import javax.inject.Named;

@ClientScope
public class CheckerConnectPermission {

    private final CheckerPermission checkerPermission;
    private final String[][] connectPermissions;

    @Inject
    CheckerConnectPermission(
            CheckerPermission checkerPermission,
            @Named(ClientComponent.PlatformConstants.STRING_ARRAY_CONNECT_PERMISSIONS) String[][] connectPermissions
    ) {
        this.checkerPermission = checkerPermission;
        this.connectPermissions = connectPermissions;
    }

    public boolean isConnectRuntimePermissionGranted() {
        boolean allNeededPermissionsGranted = true;
        for (String[] neededPermissions : connectPermissions) {
            allNeededPermissionsGranted &= checkerPermission.isAnyPermissionGranted(neededPermissions);
        }
        return allNeededPermissionsGranted;
    }

    public String[] getRecommendedConnectRuntimePermissions() {
        int allPermissionsCount = 0;
        for (String[] permissionsArray : connectPermissions) {
            allPermissionsCount += permissionsArray.length;
        }
        String[] resultPermissions = new String[allPermissionsCount];
        int i = 0;
        for (String[] permissionsArray : connectPermissions) {
            for (String permission : permissionsArray) {
                resultPermissions[i++] = permission;
            }
        }
        return resultPermissions;
    }
}
