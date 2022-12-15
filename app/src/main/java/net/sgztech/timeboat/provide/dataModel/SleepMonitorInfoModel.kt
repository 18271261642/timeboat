package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class SleepMonitorInfoModel :Serializable{

    var totalMinute = 0
    var totalDeepSleepMinute =0
    var totalLightSleepMinute =0
    var totalSoberMinute =0

    var averageDeepSleepMinute =0
    var averageLightSleepMinute =0
    var averageSoberMinute= 0

}