package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable
@Keep
class SleepStatisticsModel :Serializable{
    var id =0
    var date =""
    var imei =""
    var totalDeepSleepMinute =0
    var totalLightSleepMinute =0
    var totalMinute =0
    var totalSoberMinute =0
    var userUUID =""

    var month = 0

}