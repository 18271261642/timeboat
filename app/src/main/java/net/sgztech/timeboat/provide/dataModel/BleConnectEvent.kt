package net.sgztech.timeboat.provide.dataModel

import com.device.rxble.RxBleConnection
import java.io.Serializable

class BleConnectEvent(var connectStatus: RxBleConnection.RxBleConnectionState ) :Serializable{

}