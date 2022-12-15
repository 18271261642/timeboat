package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class DeviceConfigModel :Serializable{
    val heartBeatInterval =0
    val tcpHost = ""
    val tcpPort = ""
    var bleName = ""
    val versionExpired  = 0
}