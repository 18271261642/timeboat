package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class HeartRateModel: Serializable {
    var id = 0
    var heartRate =0
    var deviceTime =""
    //周
    var date = ""
    var averageHeartRate =0
    //年
    var month =0
}