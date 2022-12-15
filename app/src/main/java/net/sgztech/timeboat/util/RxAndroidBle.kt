package net.sgztech.timeboat.util

import com.device.rxble.RxBleConnection
import com.device.rxble.RxBleDevice


/**
 * Returns `true` if connection state is [CONNECTED][RxBleConnection.RxBleConnectionState.CONNECTED].
 */
internal val RxBleDevice.isConnected: Boolean
    get() = connectionState == RxBleConnection.RxBleConnectionState.CONNECTED
