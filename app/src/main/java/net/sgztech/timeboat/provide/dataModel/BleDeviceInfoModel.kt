package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class BleDeviceInfoModel : Serializable {
    var name :String?=""
    var mac =""
    var shortMac=""
    var bleMac =""
    var imei ="" ;
    var organization ="time boat" ;
}