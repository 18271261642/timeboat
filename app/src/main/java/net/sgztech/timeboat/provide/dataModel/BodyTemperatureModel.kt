package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class BodyTemperatureModel : Serializable {
    var id =0
    var bodyTemperature = 0f
    var deviceTime =""
    //周 月
    var date =""
    var averageBodyTemperature =0
    //年
    var month = ""
}