package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class BloodOxygenModel : Serializable {
    var id =0
    var bloodOxygen = 0
    var deviceTime =""
    //周 月
    var date =""
    //年
    var month =0
    var averageBloodOxygen =0
}