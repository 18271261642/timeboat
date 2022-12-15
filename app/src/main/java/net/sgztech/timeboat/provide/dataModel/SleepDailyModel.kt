package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class SleepDailyModel : Serializable {
    var time =""
    var duration = 0
    var sleepState =0
}